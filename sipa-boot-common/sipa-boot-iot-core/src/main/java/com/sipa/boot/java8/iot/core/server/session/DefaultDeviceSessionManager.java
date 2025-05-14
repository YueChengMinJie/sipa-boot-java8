package com.sipa.boot.java8.iot.core.server.session;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

import com.sipa.boot.java8.common.archs.cache.Caches;
import com.sipa.boot.java8.common.log.Log;
import com.sipa.boot.java8.common.log.LogFactory;
import com.sipa.boot.java8.iot.core.constant.IDeviceState;
import com.sipa.boot.java8.iot.core.device.base.IDeviceRegistry;
import com.sipa.boot.java8.iot.core.enumerate.EDeviceConfigKey;
import com.sipa.boot.java8.iot.core.message.codec.base.ITransport;
import com.sipa.boot.java8.iot.core.server.monitor.base.IGatewayServerMonitor;
import com.sipa.boot.java8.iot.core.server.session.base.IDeviceSession;
import com.sipa.boot.java8.iot.core.server.session.base.IDeviceSessionManager;

import reactor.core.publisher.*;
import reactor.core.scheduler.Schedulers;

/**
 * @author caszhou
 * @date 2021/10/3
 */
public class DefaultDeviceSessionManager implements IDeviceSessionManager {
    private static final Log log = LogFactory.get(DefaultDeviceSessionManager.class);

    private final Map<String, IDeviceSession> cache = Caches.newCache();

    private final Map<String, Map<String, ChildrenDeviceSession>> childrenCache = Caches.newCache();

    private final FluxProcessor<IDeviceSession, IDeviceSession> onDeviceRegister = EmitterProcessor.create(false);

    private final FluxSink<IDeviceSession> registerListener = onDeviceRegister.sink(FluxSink.OverflowStrategy.BUFFER);

    private final FluxProcessor<IDeviceSession, IDeviceSession> onDeviceUnRegister = EmitterProcessor.create(false);

    private final FluxSink<IDeviceSession> unregisterListener =
        onDeviceUnRegister.sink(FluxSink.OverflowStrategy.BUFFER);

    private final EmitterProcessor<IDeviceSession> unregisterHandler = EmitterProcessor.create(false);

    private final FluxSink<IDeviceSession> unregisterSession = unregisterHandler.sink(FluxSink.OverflowStrategy.BUFFER);

    private final Queue<Runnable> scheduleJobQueue = new ArrayDeque<>();

    private final Map<String, LongAdder> transportCounter = new ConcurrentHashMap<>();

    private Map<String, Long> transportLimits = new ConcurrentHashMap<>();

    private IGatewayServerMonitor gatewayServerMonitor;

    private IDeviceRegistry registry;

    private String serverId;

    @Override
    public boolean isOutOfMaximumSessionLimit(ITransport transport) {
        long max = getMaximumSession(transport);
        return max > 0 && getCurrentSession(transport) >= max;
    }

    @Override
    public long getMaximumSession(ITransport transport) {
        Long counter = transportLimits.get(transport.getId());
        return counter == null ? -1 : counter;
    }

    @Override
    public long getCurrentSession(ITransport transport) {
        LongAdder counter = transportCounter.get(transport.getId());
        return counter == null ? 0 : counter.longValue();
    }

    public Mono<Long> checkSession() {
        AtomicLong startWith = new AtomicLong();
        return Flux.fromIterable(cache.values()).distinct().publishOn(Schedulers.parallel()).filterWhen(session -> {
            if (!session.isAlive() || session.getOperator() == null) {
                return Mono.just(true);
            }
            return Mono
                .zip(session.getOperator().getState().defaultIfEmpty(IDeviceState.offline),
                    session.getOperator().getConnectionServerId().defaultIfEmpty(""))
                .filter(tp2 -> !tp2.getT1().equals(IDeviceState.online) || !tp2.getT2().equals(serverId))
                .flatMap((ignore) -> {
                    // 设备设备状态为在线
                    return session.getOperator()
                        .online(serverId, session.getId())
                        .then(Mono.fromRunnable(() -> registerListener.next(session)));
                })
                .thenReturn(false);
        }).map(IDeviceSession::getId).doOnNext(this::unregister).count().doOnNext((l) -> {
            if (log.isInfoEnabled() && l > 0) {
                log.info("expired sessions:{}", l);
            }
        }).doOnError(log::error).doOnSubscribe(subscription -> {
            log.trace("start check session");
            startWith.set(System.currentTimeMillis());
        }).doFinally(s -> {
            // 上报session数量
            transportCounter.forEach(
                (transport, number) -> gatewayServerMonitor.metrics().reportSession(transport, number.intValue()));
            // 执行任务
            for (Runnable runnable = scheduleJobQueue.poll(); runnable != null; runnable = scheduleJobQueue.poll()) {
                try {
                    runnable.run();
                } catch (Exception e) {
                    log.error(e);
                }
            }
            if (log.isTraceEnabled()) {
                log.trace("check session complete,current server sessions:{}.use time:{}ms.", transportCounter,
                    System.currentTimeMillis() - startWith.get());
            }
        });
    }

    public void init() {
        Objects.requireNonNull(gatewayServerMonitor, "gatewayServerMonitor");
        Objects.requireNonNull(registry, "registry");
        serverId = gatewayServerMonitor.getCurrentServerId();
        Flux.interval(Duration.ofSeconds(10), Duration.ofSeconds(30), Schedulers.newSingle("device-session-checker"))
            .flatMap(i -> this.checkSession().onErrorContinue((err, val) -> log.error(err)))
            .subscribe();

        unregisterHandler.publishOn(Schedulers.parallel()).flatMap(session -> {
            // 注册中心下线
            return Optional.ofNullable(session.getOperator())
                .orElseThrow(RuntimeException::new)
                .offline()
                .doFinally(s -> {
                    if (onDeviceUnRegister.hasDownstreams()) {
                        unregisterListener.next(session);
                    }
                })
                .onErrorContinue((err, obj) -> log.error(err))
                .then(Mono.justOrEmpty(childrenCache.remove(session.getDeviceId()))
                    .flatMapIterable(Map::values)
                    .flatMap(childrenDeviceSession -> Optional.ofNullable(childrenDeviceSession.getOperator())
                        .orElseThrow(RuntimeException::new)
                        .offline()
                        .doFinally(s -> {
                            if (onDeviceUnRegister.hasDownstreams()) {
                                unregisterListener.next(childrenDeviceSession);
                            }
                            scheduleJobQueue.add(childrenDeviceSession::close);
                        }))
                    .then());
        }).onErrorContinue((err, obj) -> log.error(err)).subscribe();
    }

    @Override
    public IDeviceSession getSession(String clientId) {
        IDeviceSession session = cache.get(clientId);
        if (session == null || !session.isAlive()) {
            return null;
        }
        return session;
    }

    @Override
    public ChildrenDeviceSession getSession(String deviceId, String childrenId) {
        return Optional.ofNullable(childrenCache.get(deviceId))
            .map(map -> map.get(childrenId))
            .filter(ChildrenDeviceSession::isAlive)
            .orElse(null);
    }

    @Override
    public Mono<ChildrenDeviceSession> registerChildren(String deviceId, String childrenDeviceId) {
        return Mono.defer(() -> {
            IDeviceSession session = getSession(deviceId);
            if (session == null) {
                log.warn("device[{}] session not alive", deviceId);
                return Mono.empty();
            }
            return registry.getDevice(childrenDeviceId)
                .switchIfEmpty(
                    Mono.fromRunnable(() -> log.warn("children device [{}] not fond in registry", childrenDeviceId)))
                .flatMap(deviceOperator -> deviceOperator
                    .online(session.getServerId().orElse(serverId), session.getId(),
                        session.getClientAddress().map(String::valueOf).orElse(null))
                    .then(deviceOperator.setConfig(EDeviceConfigKey.parentGatewayId, deviceId))
                    .thenReturn(new ChildrenDeviceSession(childrenDeviceId, session, deviceOperator)))
                .doOnNext(s -> {
                    registerListener.next(s);
                    childrenCache.computeIfAbsent(deviceId, __ -> new ConcurrentHashMap<>()).put(childrenDeviceId, s);
                });
        });
    }

    @Override
    public Mono<ChildrenDeviceSession> unRegisterChildren(String deviceId, String childrenId) {
        return Mono.justOrEmpty(childrenCache.get(deviceId))
            .flatMap(map -> Mono.justOrEmpty(map.remove(childrenId)))
            .doOnNext(ChildrenDeviceSession::close)
            .flatMap(session -> Optional.ofNullable(session.getOperator())
                .orElseThrow(RuntimeException::new)
                .offline()
                .doFinally(s -> {
                    // 通知
                    if (onDeviceUnRegister.hasDownstreams()) {
                        unregisterListener.next(session);
                    }
                })
                .thenReturn(session));
    }

    @Override
    public IDeviceSession replace(IDeviceSession oldSession, IDeviceSession newSession) {
        IDeviceSession old = cache.put(oldSession.getDeviceId(), newSession);
        if (old != null) {
            if (!old.getId().equals(old.getDeviceId())) {
                cache.put(oldSession.getId(), newSession);
            }
        }
        return newSession;
    }

    @Override
    public IDeviceSession register(IDeviceSession session) {
        IDeviceSession old = cache.put(session.getDeviceId(), session);
        if (old != null) {
            if (!old.getId().equals(old.getDeviceId())) {
                cache.remove(old.getId());
            }
        }
        if (!session.getId().equals(session.getDeviceId())) {
            cache.put(session.getId(), session);
        }
        if (null != old) {
            if (!old.equals(session)) {
                // 1. 可能是多个设备使用了相同的id.
                // 2. 可能是同一个设备, 注销后立即上线, 由于种种原因, 先处理了上线后处理了注销逻辑.
                log.warn("device [{}] session exists, disconnect old session 【{}】", old.getDeviceId(), session);
                scheduleJobQueue.add(old::close);
            }
        } else {
            transportCounter.computeIfAbsent(session.getTransport().getId(), transport -> new LongAdder()).increment();
        }

        Optional.ofNullable(session.getOperator())
            .orElseThrow(RuntimeException::new)
            .online(session.getServerId().orElse(serverId), session.getId(),
                session.getClientAddress().map(String::valueOf).orElse(null))
            .doFinally(s -> {
                if (onDeviceRegister.hasDownstreams()) {
                    registerListener.next(session);
                }
            })
            .subscribe();

        return old;
    }

    @Override
    public Flux<IDeviceSession> onRegister() {
        return onDeviceRegister;
    }

    @Override
    public Flux<IDeviceSession> onUnRegister() {
        return onDeviceUnRegister;
    }

    @Override
    public Flux<IDeviceSession> getAllSession() {
        return Flux.fromIterable(cache.values()).distinct(IDeviceSession::getDeviceId);
    }

    @Override
    public boolean sessionIsAlive(String deviceId) {
        return getSession(deviceId) != null || childrenCache.values().stream().anyMatch(r -> {
            IDeviceSession session = r.get(deviceId);
            return session != null && session.isAlive();
        });
    }

    @Override
    public IDeviceSession unregister(String idOrDeviceId) {
        IDeviceSession session = cache.remove(idOrDeviceId);
        if (null != session) {
            if (!session.getId().equals(session.getDeviceId())) {
                cache.remove(session.getId().equals(idOrDeviceId) ? session.getDeviceId() : session.getId());
            }
            // 本地计数
            transportCounter.computeIfAbsent(session.getTransport().getId(), transport -> new LongAdder()).decrement();
            // 通知
            unregisterSession.next(session);
            // 加入关闭连接队列
            scheduleJobQueue.add(session::close);
        }
        return session;
    }

    public void shutdown() {
        cache.values().stream().map(IDeviceSession::getId).forEach(this::unregister);
    }

    public void setGatewayServerMonitor(IGatewayServerMonitor gatewayServerMonitor) {
        this.gatewayServerMonitor = gatewayServerMonitor;
    }

    public IDeviceRegistry getRegistry() {
        return registry;
    }

    public void setRegistry(IDeviceRegistry registry) {
        this.registry = registry;
    }

    public void setTransportLimits(Map<String, Long> transportLimits) {
        this.transportLimits = transportLimits;
    }
}
