package com.sipa.boot.java8.iot.core.device;

import static com.sipa.boot.java8.iot.core.enumerate.EDeviceConfigKey.*;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

import org.springframework.util.StringUtils;

import com.sipa.boot.java8.common.log.Log;
import com.sipa.boot.java8.common.log.LogFactory;
import com.sipa.boot.java8.common.utils.UuidUtils;
import com.sipa.boot.java8.iot.core.base.IValue;
import com.sipa.boot.java8.iot.core.base.IValues;
import com.sipa.boot.java8.iot.core.config.base.IConfigKey;
import com.sipa.boot.java8.iot.core.config.base.IConfigStorage;
import com.sipa.boot.java8.iot.core.config.base.IConfigStorageManager;
import com.sipa.boot.java8.iot.core.config.base.IStorageConfigurable;
import com.sipa.boot.java8.iot.core.constant.IDeviceState;
import com.sipa.boot.java8.iot.core.device.base.*;
import com.sipa.boot.java8.iot.core.enumerate.EDeviceConfigKey;
import com.sipa.boot.java8.iot.core.message.base.IDeviceMessageReply;
import com.sipa.boot.java8.iot.core.message.base.IHeaders;
import com.sipa.boot.java8.iot.core.message.child.ChildDeviceMessage;
import com.sipa.boot.java8.iot.core.message.child.ChildDeviceMessageReply;
import com.sipa.boot.java8.iot.core.message.device.DisconnectDeviceMessage;
import com.sipa.boot.java8.iot.core.message.interceptor.base.IDeviceMessageSenderInterceptor;
import com.sipa.boot.java8.iot.core.message.status.DeviceStateCheckMessage;
import com.sipa.boot.java8.iot.core.message.status.DeviceStateCheckMessageReply;
import com.sipa.boot.java8.iot.core.metadata.base.IDeviceMetadata;
import com.sipa.boot.java8.iot.core.protocol.base.IProtocolSupport;

import reactor.core.publisher.Mono;

/**
 * @author caszhou
 * @date 2021/10/2
 */
public class DefaultDeviceOperator implements IDeviceOperator, IStorageConfigurable {
    private static final Log log = LogFactory.get(DefaultDeviceOperator.class);

    public static final IDeviceStateChecker DEFAULT_STATE_CHECKER =
        device -> checkState0(((DefaultDeviceOperator)device));

    private static final IConfigKey<Long> LAST_METADATA_TIME_KEY = IConfigKey.of("lst_metadata_time");

    private static final AtomicReferenceFieldUpdater<DefaultDeviceOperator, IDeviceMetadata> METADATA_UPDATER =
        AtomicReferenceFieldUpdater.newUpdater(DefaultDeviceOperator.class, IDeviceMetadata.class, "metadataCache");

    private static final AtomicLongFieldUpdater<DefaultDeviceOperator> METADATA_TIME_UPDATER =
        AtomicLongFieldUpdater.newUpdater(DefaultDeviceOperator.class, "lastMetadataTime");

    private final String id;

    private final IDeviceOperationBroker handler;

    private final IDeviceRegistry registry;

    private final IDeviceMessageSender messageSender;

    private final Mono<IConfigStorage> storageMono;

    private final Mono<IProtocolSupport> protocolSupportMono;

    private final Mono<IDeviceMetadata> metadataMono;

    private final IDeviceStateChecker stateChecker;

    private volatile long lastMetadataTime = -1;

    private volatile IDeviceMetadata metadataCache;

    public DefaultDeviceOperator(String id, IConfigStorageManager storageManager, IDeviceOperationBroker handler,
        IDeviceRegistry registry) {
        this(id, storageManager, handler, registry, IDeviceMessageSenderInterceptor.DO_NOTING);
    }

    public DefaultDeviceOperator(String id, IConfigStorageManager storageManager, IDeviceOperationBroker handler,
        IDeviceRegistry registry, IDeviceMessageSenderInterceptor interceptor) {
        this(id, storageManager, handler, registry, interceptor, DEFAULT_STATE_CHECKER);
    }

    public DefaultDeviceOperator(String id, IConfigStorageManager configStorageManager,
        IDeviceOperationBroker deviceOperationBroker, IDeviceRegistry deviceRegistry,
        IDeviceMessageSenderInterceptor deviceMessageSenderInterceptor, IDeviceStateChecker deviceStateChecker) {
        this.id = id;
        this.registry = deviceRegistry;
        this.handler = deviceOperationBroker;
        this.messageSender =
            new DefaultDeviceMessageSender(deviceOperationBroker, this, deviceRegistry, deviceMessageSenderInterceptor);
        this.storageMono = configStorageManager.getStorage("device:" + id);
        this.protocolSupportMono = getProduct().flatMap(IDeviceProductOperator::getProtocol);
        this.stateChecker = deviceStateChecker;
        this.metadataMono = this
            // 获取最后更新物模型的时间
            .getSelfConfig(LAST_METADATA_TIME_KEY)
            .flatMap(i -> {
                // 如果有时间,则表示设备有独立的物模型.
                // 如果时间一致,则直接返回物模型缓存.
                if (i.equals(lastMetadataTime) && metadataCache != null) {
                    return Mono.just(metadataCache);
                }
                METADATA_TIME_UPDATER.set(this, i);
                // 加载真实的物模型
                return Mono.zip(getSelfConfig(metadata), protocolSupportMono)
                    .flatMap(tp2 -> tp2.getT2()
                        .getMetadataCodec()
                        .decode(tp2.getT1())
                        .doOnNext(metadata -> METADATA_UPDATER.set(this, metadata)));
            })
            // 如果上游为空,则使用产品的物模型
            .switchIfEmpty(this.getParent().flatMap(IDeviceProductOperator::getMetadata));
    }

    @Override
    public Mono<IConfigStorage> getReactiveStorage() {
        return storageMono;
    }

    @Override
    public String getDeviceId() {
        return id;
    }

    @Override
    public Mono<String> getConnectionServerId() {
        return getSelfConfig(connectionServerId.getKey()).map(IValue::asString);
    }

    @Override
    public Mono<String> getSessionId() {
        return getSelfConfig(sessionId.getKey()).map(IValue::asString);
    }

    @Override
    public Mono<String> getAddress() {
        return getConfig("address").map(IValue::asString);
    }

    @Override
    public Mono<Void> setAddress(String address) {
        return setConfig("address", address).then();
    }

    @Override
    public Mono<Boolean> putState(byte state) {
        return setConfig("state", state);
    }

    @Override
    public Mono<Byte> getState() {
        return this.getSelfConfigs(Arrays.asList("state", parentGatewayId.getKey(), selfManageState.getKey()))
            .flatMap(values -> {
                Byte state = values.getValue("state").map(val -> val.as(Byte.class)).orElse(IDeviceState.unknown);

                boolean isSelfManageState =
                    values.getValue(selfManageState.getKey()).map(val -> val.as(Boolean.class)).orElse(false);
                String parentGatewayId = values.getValue(EDeviceConfigKey.parentGatewayId).orElse(null);

                if (getDeviceId().equals(parentGatewayId)) {
                    log.warn("设备[{}]存在循环依赖", parentGatewayId);
                    return Mono.just(state);
                }
                if (isSelfManageState) {
                    return Mono.just(state);
                }
                // 获取网关设备状态
                if (StringUtils.hasText(parentGatewayId)) {
                    return registry.getDevice(parentGatewayId).flatMap(IDeviceOperator::getState);
                }
                return Mono.just(state);
            })
            .defaultIfEmpty(IDeviceState.unknown);
    }

    private Mono<Byte> doCheckState() {
        return Mono.defer(() -> this
            .getSelfConfigs(
                Arrays.asList(connectionServerId.getKey(), parentGatewayId.getKey(), selfManageState.getKey(), "state"))
            .flatMap(values -> {
                // 当前设备连接到的服务器
                String server = values.getValue(connectionServerId).orElse(null);

                // 设备缓存的状态
                Byte state = values.getValue("state").map(val -> val.as(Byte.class)).orElse(IDeviceState.unknown);

                // 如果缓存中存储有当前设备所在服务信息则尝试发起状态检查
                if (StringUtils.hasText(server)) {
                    return handler.getDeviceState(server, Collections.singletonList(id))
                        .map(DeviceStateInfo::getState)
                        .singleOrEmpty()
                        .timeout(Duration.ofSeconds(1), Mono.just(state))
                        .defaultIfEmpty(state);
                }

                // 网关设备ID
                String parentGatewayId = values.getValue(EDeviceConfigKey.parentGatewayId).orElse(null);

                if (getDeviceId().equals(parentGatewayId)) {
                    log.warn("设备[{}]存在循环依赖", parentGatewayId);
                    return Mono.just(state);
                }
                boolean isSelfManageState = values.getValue(selfManageState).orElse(false);
                // 如果关联了上级网关设备则获取父设备状态
                if (StringUtils.hasText(parentGatewayId)) {
                    return registry.getDevice(parentGatewayId).flatMap(device -> {
                        // 不是状态自管理则直接返回网关的状态
                        if (!isSelfManageState) {
                            return device.checkState();
                        }
                        // 发送设备状态检查指令给网关设备
                        return device.messageSender()
                            .<ChildDeviceMessageReply>send(ChildDeviceMessage
                                .create(parentGatewayId, DeviceStateCheckMessage.create(getDeviceId()))
                                .addHeader(IHeaders.timeout, 5000L))
                            .singleOrEmpty()
                            .map(msg -> {
                                if (msg.getChildDeviceMessage() instanceof DeviceStateCheckMessageReply) {
                                    return ((DeviceStateCheckMessageReply)msg.getChildDeviceMessage()).getState();
                                }
                                log.warn("子设备状态检查返回消息错误{}", msg);
                                // 网关设备在线,只是返回了错误的消息,所以也认为网关设备在线
                                return IDeviceState.online;
                            })
                            .onErrorResume(err -> {
                                // 发送返回错误,但是配置了状态自管理,直接返回原始状态
                                return Mono.just(state);
                            });
                    });
                }

                // 如果是在线状态,则改为离线,否则保持状态不变
                if (state.equals(IDeviceState.online)) {
                    return Mono.just(IDeviceState.offline);
                } else {
                    return Mono.just(state);
                }
            }));
    }

    @Override
    public Mono<Byte> checkState() {
        return Mono.zip(stateChecker.checkState(this)
            .switchIfEmpty(Mono.defer(() -> DEFAULT_STATE_CHECKER.checkState(this)))
            .defaultIfEmpty(IDeviceState.online), this.getState()).flatMap(tp2 -> {
                byte newer = tp2.getT1();
                byte old = tp2.getT2();
                if (newer != old) {
                    log.info("device[{}] state changed from {} to {}", this.getDeviceId(), old, newer);
                    Map<String, Object> configs = new HashMap<>();
                    configs.put("state", newer);
                    if (newer == IDeviceState.online) {
                        configs.put("onlineTime", System.currentTimeMillis());
                    } else if (newer == IDeviceState.offline) {
                        configs.put("offlineTime", System.currentTimeMillis());
                    }
                    return this.setConfigs(configs).thenReturn(newer);
                }
                return Mono.just(newer);
            });
    }

    @Override
    public Mono<Long> getOnlineTime() {
        return this.getSelfConfig("onlineTime")
            .map(val -> val.as(Long.class))
            .switchIfEmpty(Mono.defer(() -> this.getSelfConfig(parentGatewayId)
                .flatMap(registry::getDevice)
                .flatMap(IDeviceOperator::getOnlineTime)));
    }

    @Override
    public Mono<Long> getOfflineTime() {
        return this.getSelfConfig("offlineTime")
            .map(val -> val.as(Long.class))
            .switchIfEmpty(Mono.defer(() -> this.getSelfConfig(parentGatewayId)
                .flatMap(registry::getDevice)
                .flatMap(IDeviceOperator::getOfflineTime)));
    }

    @Override
    public Mono<Boolean> offline() {
        return this
            .setConfigs(connectionServerId.value(""), sessionId.value(""),
                IConfigKey.of("offlineTime").value(System.currentTimeMillis()),
                IConfigKey.of("state").value(IDeviceState.offline))
            .doOnError(err -> log.error("offline device error", err));
    }

    @Override
    public Mono<Boolean> online(String serverId, String sessionId, String address) {
        return this
            .setConfigs(connectionServerId.value(serverId), EDeviceConfigKey.sessionId.value(sessionId),
                IConfigKey.of("address").value(address), IConfigKey.of("onlineTime").value(System.currentTimeMillis()),
                IConfigKey.of("state").value(IDeviceState.online))
            .doOnError(err -> log.error("online device error", err));
    }

    @Override
    public Mono<IValue> getSelfConfig(String key) {
        return getConfig(key, false);
    }

    @Override
    public Mono<IValues> getSelfConfigs(Collection<String> keys) {
        return getConfigs(keys, false);
    }

    @Override
    public Mono<Boolean> disconnect() {
        DisconnectDeviceMessage disconnect = new DisconnectDeviceMessage();
        disconnect.setDeviceId(getDeviceId());
        disconnect.setMessageId(UuidUtils.generator());
        return messageSender().send(Mono.just(disconnect)).next().map(IDeviceMessageReply::isSuccess);
    }

    @Override
    public Mono<AuthenticationResponse> authenticate(IAuthenticationRequest request) {
        return getProtocol().flatMap(protocolSupport -> protocolSupport.authenticate(request, this));
    }

    @Override
    public Mono<IDeviceMetadata> getMetadata() {
        return metadataMono;
    }

    @Override
    public Mono<IDeviceProductOperator> getParent() {
        return getReactiveStorage().flatMap(store -> store.getConfig(productId.getKey()))
            .map(IValue::asString)
            .flatMap(registry::getProduct);
    }

    @Override
    public Mono<IProtocolSupport> getProtocol() {
        return protocolSupportMono;
    }

    @Override
    public Mono<IDeviceProductOperator> getProduct() {
        return getParent();
    }

    @Override
    public IDeviceMessageSender messageSender() {
        return messageSender;
    }

    @Override
    public Mono<Boolean> updateMetadata(String metadata) {
        Map<String, Object> configs = new HashMap<>();
        configs.put(EDeviceConfigKey.metadata.getKey(), metadata);
        return setConfigs(configs);
    }

    @Override
    public Mono<Void> resetMetadata() {
        METADATA_UPDATER.set(this, null);
        METADATA_TIME_UPDATER.set(this, -1);
        return removeConfigs(metadata, LAST_METADATA_TIME_KEY)
            .then(this.getProtocol().flatMap(support -> support.onDeviceMetadataChanged(this)));
    }

    @Override
    public Mono<Boolean> setConfigs(Map<String, Object> conf) {
        Map<String, Object> configs = new HashMap<>(conf);
        if (conf.containsKey(metadata.getKey())) {
            configs.put(LAST_METADATA_TIME_KEY.getKey(), lastMetadataTime = System.currentTimeMillis());

            return IStorageConfigurable.super.setConfigs(configs).doOnNext(suc -> {
                this.metadataCache = null;
            }).then(this.getProtocol().flatMap(support -> support.onDeviceMetadataChanged(this))).thenReturn(true);
        }
        return IStorageConfigurable.super.setConfigs(configs);
    }

    private static Mono<Byte> checkState0(DefaultDeviceOperator operator) {
        return operator.getProtocol()
            .flatMap(IProtocolSupport::getStateChecker) // 协议自定义了状态检查逻辑
            .flatMap(deviceStateChecker -> deviceStateChecker.checkState(operator))
            .switchIfEmpty(operator.doCheckState()) // 默认的检查
        ;
    }
}
