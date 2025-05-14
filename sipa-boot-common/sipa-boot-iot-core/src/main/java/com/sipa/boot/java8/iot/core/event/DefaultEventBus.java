package com.sipa.boot.java8.iot.core.event;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.validation.constraints.NotNull;

import org.reactivestreams.Publisher;
import org.springframework.stereotype.Component;

import com.sipa.boot.java8.common.log.Log;
import com.sipa.boot.java8.common.log.LogFactory;
import com.sipa.boot.java8.iot.core.codec.Codecs;
import com.sipa.boot.java8.iot.core.codec.base.IDecoder;
import com.sipa.boot.java8.iot.core.codec.base.IEncoder;
import com.sipa.boot.java8.iot.core.dict.base.IEnumDict;
import com.sipa.boot.java8.iot.core.event.base.*;
import com.sipa.boot.java8.iot.core.topic.Topic;

import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.ThreadLocalRandom;
import reactor.core.Disposable;
import reactor.core.Disposables;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

/**
 * 支持事件代理的事件总线,可通过代理来实现集群和分布式事件总线
 *
 * @author caszhou
 * @date 2021/10/3
 */
@Component
public class DefaultEventBus implements IEventBus {
    private static final Log log = LogFactory.get(DefaultEventBus.class);

    private final Topic<SubscriptionInfo> root = Topic.createRoot();

    private final Map<String, IEventBroker> brokers = new ConcurrentHashMap<>(32);

    private final Map<String, IEventConnection> connections = new ConcurrentHashMap<>(512);

    private final Scheduler publishScheduler = Schedulers.immediate();

    // ********************************************************
    // ********************** subscribe ***********************
    // ********************************************************

    @Override
    public <T> Flux<T> subscribe(@NotNull Subscription subscription, @NotNull IDecoder<T> decoder) {
        return this.subscribe(subscription).flatMap(payload -> {
            try {
                return Mono.justOrEmpty(payload.decode(decoder, false));
            } catch (Throwable e) {
                log.error("decode message [{}] error", payload.getTopic(), e);
            } finally {
                ReferenceCountUtil.safeRelease(payload);
            }
            return Mono.empty();
        }).publishOn(publishScheduler);
    }

    @Override
    public Flux<TopicPayload> subscribe(Subscription subscription) {
        return Flux.create(sink -> {
            Disposable.Composite disposable = Disposables.composite();
            String subscriberId = subscription.getSubscriber();
            for (String topic : subscription.getTopics()) {
                Topic<SubscriptionInfo> topicInfo = root.append(topic);
                SubscriptionInfo subInfo =
                    SubscriptionInfo.of(subscriberId, IEnumDict.toMask(subscription.getFeatures()), sink, false);
                topicInfo.subscribe(subInfo);
                disposable.add(() -> {
                    topicInfo.unsubscribe(subInfo);
                    subInfo.dispose();
                });
            }
            sink.onDispose(disposable);
            // 订阅代理消息
            if (subscription.hasFeature(Subscription.EFeature.broker)) {
                doSubscribeBroker(subscription).doOnSuccess(nil -> {
                    if (subscription.getDoOnSubscribe() != null) {
                        subscription.getDoOnSubscribe().run();
                    }
                }).subscribe();
                disposable.add(() -> doUnsubscribeBroker(subscription).subscribe());
            } else if (subscription.getDoOnSubscribe() != null) {
                subscription.getDoOnSubscribe().run();
            }
            if (log.isDebugEnabled()) {
                log.debug("Local subscriber [{}], features [{}], topics [{}]", subscriberId, subscription.getFeatures(),
                    subscription.getTopics());
            }
        });
    }

    private Mono<Void> doSubscribeBroker(Subscription subscription) {
        return Flux.fromIterable(connections.values())
            .filter(conn -> conn.isProducer() && conn.isAlive())
            .cast(IEventProducer.class)
            .flatMap(conn -> conn.subscribe(subscription))
            .then();
    }

    private Mono<Void> doUnsubscribeBroker(Subscription subscription) {
        return Flux.fromIterable(connections.values())
            .filter(conn -> conn.isProducer() && conn.isAlive())
            .cast(IEventProducer.class)
            .flatMap(conn -> conn.unsubscribe(subscription))
            .then();
    }

    // ********************************************************
    // *********************** publish ************************
    // ********************************************************

    @Override
    public <T> Mono<Long> publish(String topic, Publisher<T> event) {
        return publish(topic, (IEncoder<T>)body -> Codecs.lookup((Class<T>)body.getClass()).encode(body), event);
    }

    @Override
    public <T> Mono<Long> publish(String topic, IEncoder<T> encoder, Publisher<? extends T> eventStream) {
        return publish(topic, encoder, eventStream, publishScheduler);
    }

    @Override
    public <T> Mono<Long> publish(String topic, IEncoder<T> encoder, Publisher<? extends T> eventStream,
        Scheduler publisher) {
        Predicate<SubscriptionInfo> predicate = sub -> !sub.isLocal() || sub.hasFeature(Subscription.EFeature.local);

        Function<Flux<SubscriptionInfo>, Mono<Long>> function = subscribers -> {
            Flux<TopicPayload> cache =
                Flux.from(eventStream).map(payload -> TopicPayload.of(topic, IPayload.of(payload, encoder))).cache();

            return subscribers
                .flatMap(subscriber -> cache.map((payload) -> doPublish(topic, subscriber, payload)).count())
                .count()
                .flatMap((s) -> {
                    if (s > 0) {
                        return cache.map(payload -> {
                            ReferenceCountUtil.safeRelease(payload);
                            return true;
                        }).then().thenReturn(s);
                    }
                    return Mono.just(s);
                });
        };

        return this.doPublish(topic, predicate, function).as(res -> {
            if (log.isTraceEnabled()) {
                return res.doOnNext(subs -> log.trace("topic [{}] has [{}] subscriber", topic, subs));
            }
            return res;
        });
    }

    private Mono<Long> doPublish(String topic, Predicate<SubscriptionInfo> predicate,
        Function<Flux<SubscriptionInfo>, Mono<Long>> function) {
        return root.findTopic(topic).flatMapIterable(Topic::getSubscribers).filter(sub -> {
            if (sub.isBroker() && !sub.getEventConnection().isAlive()) {
                sub.dispose();
                return false;
            }
            return predicate.test(sub);
        })
            // 根据订阅者标识进行分组,以进行订阅模式判断
            .groupBy(SubscriptionInfo::getSubscriber, Integer.MAX_VALUE)
            .flatMap(group -> group.groupBy(sub -> sub.hasFeature(Subscription.EFeature.shared)).flatMap(groups -> {
                // 共享订阅
                if (Boolean.TRUE.equals(groups.key())) {
                    return selectSharedSubscription(groups);
                }
                return groups;
            }), Integer.MAX_VALUE)
            // 防止多次推送给同一个消费者,
            // 比如同一个消费者订阅了: /device/1/2 和/device/1/*/
            // 推送 /device/1/2,会获取到2个相同到订阅者
            .distinct(SubscriptionInfo::getSink)
            .as(function);
    }

    private Flux<SubscriptionInfo> selectSharedSubscription(Flux<SubscriptionInfo> subscriptionInfoFlux) {
        return subscriptionInfoFlux.collectList()
            // 随机转发订阅者
            .flatMapMany(subs -> Flux.just(subs.get(ThreadLocalRandom.current().nextInt(0, subs.size()))));
    }

    private boolean doPublish(String topic, SubscriptionInfo info, TopicPayload payload) {
        try {
            // 已经取消订阅则不推送
            if (info.sink.isCancelled()) {
                return false;
            }

            payload.retain();
            info.sink.next(payload);

            if (log.isDebugEnabled()) {
                log.debug("Publish [{}] to [{}] complete", topic, info);
            }

            return true;
        } catch (Throwable error) {
            log.error("Publish [{}] to [{}] event error", topic, info, error);
            ReferenceCountUtil.safeRelease(payload);
        }
        return false;
    }

    // ********************************************************
    // *********************** broker *************************
    // ********************************************************

    public void addBroker(IEventBroker broker) {
        brokers.put(broker.getId(), broker);
        startBroker(broker);
    }

    private void startBroker(IEventBroker broker) {
        broker.accept().subscribe(connection -> {
            String connectionId = broker.getId().concat(":").concat(connection.getId());
            IEventConnection old = connections.put(connectionId, connection);
            if (old == connection) {
                return;
            }
            if (old != null) {
                old.dispose();
            }
            connection.doOnDispose(() -> connections.remove(connectionId));
            // 从生产者订阅消息并推送到本地
            connection.asProducer().flatMap(eventProducer -> root.getAllSubscriber().doOnNext(sub -> {
                for (SubscriptionInfo subscriber : sub.getSubscribers()) {
                    if (subscriber.isLocal()) {
                        if (subscriber.hasFeature(Subscription.EFeature.broker)) {
                            eventProducer.subscribe(subscriber.toSubscription(sub.getTopic())).subscribe();
                        }
                    }
                }
            }).then(Mono.just(eventProducer)))
                .flatMapMany(IEventProducer::subscribe)
                .flatMap(payload -> this.doPublishFromBroker(payload, sub -> {
                    // 本地订阅的
                    if (sub.isLocal()) {
                        return sub.hasFeature(Subscription.EFeature.broker);
                    }
                    if (sub.isBroker()) {
                        // 消息来自同一个broker
                        if (sub.getEventBroker() == broker) {
                            if (sub.getEventConnection() == connection) {
                                return sub.hasConnectionFeature(IEventConnection.EFeature.consumeSameConnection);
                            }
                            return sub.hasConnectionFeature(IEventConnection.EFeature.consumeSameBroker);
                        }
                        return sub.hasConnectionFeature(IEventConnection.EFeature.consumeAnotherBroker);
                    }
                    return false;
                }), Integer.MAX_VALUE)
                .onErrorContinue((err, obj) -> log.error(err))
                .subscribe();
            // 从消费者订阅获取订阅消息请求
            connection.asConsumer().subscribe(subscriber -> {
                // 接收订阅请求
                subscriber.handleSubscribe()
                    .doOnNext(subscription -> handleBrokerSubscription(subscription,
                        SubscriptionInfo
                            .of(subscription.getSubscriber(), IEnumDict.toMask(subscription.getFeatures()),
                                subscriber.sink(), true)
                            .connection(broker, connection),
                        connection))
                    .onErrorContinue((err, obj) -> log.error(err))
                    .subscribe();

                // 接收取消订阅请求
                subscriber.handleUnSubscribe()
                    .doOnNext(subscription -> handleBrokerUnsubscription(subscription,
                        SubscriptionInfo.of(subscription.getSubscriber()), connection))
                    .onErrorContinue((err, obj) -> log.error(err))
                    .subscribe();
            });
        });
    }

    private Mono<Long> doPublishFromBroker(TopicPayload payload, Predicate<SubscriptionInfo> predicate) {
        return this.doPublish(payload.getTopic(), predicate, flux -> flux.doOnNext(info -> {
            try {
                payload.retain();
                info.sink.next(payload);
                log.debug("broker publish [{}] to [{}] complete", payload.getTopic(), info);
            } catch (Throwable e) {
                log.warn("broker publish [{}] to [{}] error", payload.getTopic(), info, e);
            }
        }).count()).doFinally(i -> ReferenceCountUtil.safeRelease(payload));
    }

    private void handleBrokerSubscription(Subscription subscription, SubscriptionInfo info,
        IEventConnection connection) {
        log.debug("broker [{}] subscribe [{}]", info, subscription.getTopics());
        for (String topic : subscription.getTopics()) {
            Topic<SubscriptionInfo> t = root.append(topic);
            t.subscribe(info);
            info.onDispose(() -> t.unsubscribe(info));
        }
        if (subscription.hasFeature(Subscription.EFeature.broker)
            && info.hasConnectionFeature(IEventConnection.EFeature.consumeAnotherBroker)) {
            subAnotherBroker(subscription, info, connection);
        }
    }

    private void subAnotherBroker(Subscription subscription, SubscriptionInfo info, IEventConnection connection) {
        // 从其他broker订阅时,去掉broker标识
        Subscription sub = subscription.hasFeature(Subscription.EFeature.shared)
            ? subscription.copy(Subscription.EFeature.shared, Subscription.EFeature.local)
            : subscription.copy(Subscription.EFeature.local);

        Flux.fromIterable(connections.values()).filter(conn -> {
            if (conn == connection) {
                return info.hasConnectionFeature(IEventConnection.EFeature.consumeSameConnection);
            }
            if (conn.getBroker() == connection.getBroker()) {
                return info.hasConnectionFeature(IEventConnection.EFeature.consumeSameBroker);
            }
            return true;
        }).flatMap(IEventConnection::asProducer).flatMap(eventProducer -> eventProducer.subscribe(sub)).subscribe();
    }

    private void handleBrokerUnsubscription(Subscription subscription, SubscriptionInfo info,
        IEventConnection connection) {
        log.debug("broker [{}] unsubscribe : {}", info, subscription.getTopics());
        for (String topic : subscription.getTopics()) {
            AtomicBoolean unsub = new AtomicBoolean(false);
            root.append(topic)
                .unsubscribe(sub -> sub.getEventConnection() == connection
                    && sub.getSubscriber().equals(info.getSubscriber()) && unsub.compareAndSet(false, true));
        }
    }

    public void removeBroker(IEventBroker broker) {
        brokers.remove(broker.getId());
    }

    public void removeBroker(String broker) {
        brokers.remove(broker);
    }

    public List<IEventBroker> getBrokers() {
        return new ArrayList<>(brokers.values());
    }

    // ********************************************************
    // ******************* subscribe info *********************
    // ********************************************************

    static class SubscriptionInfo implements Disposable {
        String subscriber;

        long features;

        FluxSink<TopicPayload> sink;

        boolean broker;

        Composite disposable;

        IEventBroker eventBroker;

        IEventConnection eventConnection;

        long connectionFeatures;

        boolean isLocal() {
            return !broker;
        }

        boolean hasFeature(Subscription.EFeature feature) {
            return feature.in(this.features);
        }

        boolean hasConnectionFeature(IEventConnection.EFeature feature) {
            return feature.in(this.connectionFeatures);
        }

        public Subscription toSubscription(String topic) {
            return Subscription.of(subscriber, new String[] {topic},
                IEnumDict.getByMask(Subscription.EFeature.class, features).toArray(new Subscription.EFeature[0]));
        }

        public SubscriptionInfo connection(IEventBroker broker, IEventConnection connection) {
            this.eventConnection = connection;
            this.eventBroker = broker;
            this.connectionFeatures = IEnumDict.toMask(connection.features());
            return this;
        }

        public static SubscriptionInfo of(String subscriber) {
            return of(subscriber, 0, null, false);
        }

        public static SubscriptionInfo of(Subscription subscription, FluxSink<TopicPayload> sink, boolean remote) {
            return of(subscription.getSubscriber(), IEnumDict.toMask(subscription.getFeatures()), sink, remote);
        }

        public static SubscriptionInfo of(String subscriber, long features, FluxSink<TopicPayload> sink,
            boolean remote) {
            return new SubscriptionInfo(subscriber, features, sink, remote);
        }

        public SubscriptionInfo(String subscriber, long features, FluxSink<TopicPayload> sink, boolean broker) {
            this.subscriber = subscriber;
            this.features = features;
            this.sink = sink;
            this.broker = broker;
        }

        synchronized void onDispose(Disposable disposable) {
            if (this.disposable == null) {
                this.disposable = Disposables.composite(disposable);
            } else {
                this.disposable.add(disposable);
            }
        }

        @Override
        public void dispose() {
            if (disposable != null) {
                disposable.dispose();
            }
        }

        @Override
        public String toString() {
            return isLocal() ? subscriber + "@local"
                : subscriber + "@" + eventBroker.getId() + ":" + eventConnection.getId();
        }

        public String getSubscriber() {
            return subscriber;
        }

        public void setSubscriber(String subscriber) {
            this.subscriber = subscriber;
        }

        public long getFeatures() {
            return features;
        }

        public void setFeatures(long features) {
            this.features = features;
        }

        public FluxSink<TopicPayload> getSink() {
            return sink;
        }

        public void setSink(FluxSink<TopicPayload> sink) {
            this.sink = sink;
        }

        public boolean isBroker() {
            return broker;
        }

        public void setBroker(boolean broker) {
            this.broker = broker;
        }

        public Composite getDisposable() {
            return disposable;
        }

        public void setDisposable(Composite disposable) {
            this.disposable = disposable;
        }

        public IEventBroker getEventBroker() {
            return eventBroker;
        }

        public void setEventBroker(IEventBroker eventBroker) {
            this.eventBroker = eventBroker;
        }

        public IEventConnection getEventConnection() {
            return eventConnection;
        }

        public void setEventConnection(IEventConnection eventConnection) {
            this.eventConnection = eventConnection;
        }

        public long getConnectionFeatures() {
            return connectionFeatures;
        }

        public void setConnectionFeatures(long connectionFeatures) {
            this.connectionFeatures = connectionFeatures;
        }
    }
}
