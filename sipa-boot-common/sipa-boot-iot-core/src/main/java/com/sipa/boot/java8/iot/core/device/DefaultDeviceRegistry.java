package com.sipa.boot.java8.iot.core.device;

import java.time.Duration;
import java.util.*;

import org.springframework.util.StringUtils;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.sipa.boot.java8.common.archs.cache.Caches;
import com.sipa.boot.java8.iot.core.cluster.base.IClusterManager;
import com.sipa.boot.java8.iot.core.config.ClusterConfigStorageManager;
import com.sipa.boot.java8.iot.core.config.base.IConfigStorage;
import com.sipa.boot.java8.iot.core.config.base.IConfigStorageManager;
import com.sipa.boot.java8.iot.core.device.base.*;
import com.sipa.boot.java8.iot.core.enumerate.EDeviceConfigKey;
import com.sipa.boot.java8.iot.core.message.interceptor.CompositeDeviceMessageSenderInterceptor;
import com.sipa.boot.java8.iot.core.message.interceptor.base.IDeviceMessageSenderInterceptor;
import com.sipa.boot.java8.iot.core.protocol.base.IProtocolSupports;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

/**
 * @author caszhou
 * @date 2021/10/2
 */
public class DefaultDeviceRegistry implements IDeviceRegistry {
    /**
     * 全局拦截器
     */
    private final CompositeDeviceMessageSenderInterceptor interceptor = new CompositeDeviceMessageSenderInterceptor();

    /**
     * 产品
     */
    private final Map<String, IDeviceProductOperator> productOperatorMap = Caches.newCache();

    /**
     * 状态检查器
     */
    private final CompositeDeviceStateChecker stateChecker = new CompositeDeviceStateChecker();

    /**
     * 配置管理器
     */
    private final IConfigStorageManager configStorageManager;

    /**
     * 缓存
     */
    private final Cache<String, Mono<IDeviceOperator>> operatorCache;

    /**
     * 协议支持
     */
    private final IProtocolSupports protocolSupports;

    /**
     * 设备操作
     */
    private final IDeviceOperationBroker deviceOperationBroker;

    /**
     * 集群管理
     */
    private final IClusterManager clusterManager;

    public DefaultDeviceRegistry(IProtocolSupports protocolSupports, IClusterManager clusterManager,
        IDeviceOperationBroker deviceOperationBroker) {
        this(protocolSupports, clusterManager, deviceOperationBroker,
            CacheBuilder.newBuilder().softValues().expireAfterAccess(Duration.ofMinutes(30)).build());
    }

    public DefaultDeviceRegistry(IProtocolSupports protocolSupports, IConfigStorageManager configStorageManager,
        IClusterManager clusterManager, IDeviceOperationBroker deviceOperationBroker,
        Cache<String, Mono<IDeviceOperator>> cache) {
        this.protocolSupports = protocolSupports;
        this.deviceOperationBroker = deviceOperationBroker;
        this.configStorageManager = configStorageManager;
        this.operatorCache = cache;
        this.clusterManager = clusterManager;
        this.addStateChecker(DefaultDeviceOperator.DEFAULT_STATE_CHECKER);
    }

    public DefaultDeviceRegistry(IProtocolSupports protocolSupports, IClusterManager clusterManager,
        IDeviceOperationBroker deviceOperationBroker, Cache<String, Mono<IDeviceOperator>> cache) {
        this.protocolSupports = protocolSupports;
        this.deviceOperationBroker = deviceOperationBroker;
        this.configStorageManager = new ClusterConfigStorageManager(clusterManager);
        this.operatorCache = cache;
        this.clusterManager = clusterManager;
        this.addStateChecker(DefaultDeviceOperator.DEFAULT_STATE_CHECKER);
    }

    @Override
    public Flux<DeviceStateInfo> checkDeviceState(Flux<? extends Collection<String>> id) {
        return id.flatMap(list -> Flux.fromIterable(list)
            .flatMap(this::getDevice)
            .flatMap(device -> device.getConnectionServerId().defaultIfEmpty("__").zipWith(Mono.just(device)))
            .groupBy(Tuple2::getT1, Tuple2::getT2)
            .flatMap(group -> {
                if (!StringUtils.hasText(group.key()) || "__".equals(group.key())) {
                    return group.flatMap(operator -> operator.getState()
                        .map(state -> new DeviceStateInfo(operator.getDeviceId(), state)));
                }
                return group.map(IDeviceOperator::getDeviceId)
                    .collectList()
                    .flatMapMany(deviceIdList -> deviceOperationBroker.getDeviceState(group.key(), deviceIdList));
            }));
    }

    @Override
    public Mono<IDeviceOperator> getDevice(String deviceId) {
        if (StringUtils.isEmpty(deviceId)) {
            return Mono.empty();
        }
        {
            Mono<IDeviceOperator> deviceOperator = operatorCache.getIfPresent(deviceId);
            if (Objects.nonNull(deviceOperator)) {
                return deviceOperator;
            }
        }
        IDeviceOperator deviceOperator = createOperator(deviceId);
        return deviceOperator.getSelfConfig(EDeviceConfigKey.productId)
            .doOnNext(r -> operatorCache.put(deviceId,
                Mono.just(deviceOperator)
                    .filterWhen(device -> device.getSelfConfig(EDeviceConfigKey.productId).hasElement())))
            .map(ignore -> deviceOperator);
    }

    @Override
    public Mono<IDeviceProductOperator> getProduct(String productId) {
        if (StringUtils.isEmpty(productId)) {
            return Mono.empty();
        }
        {
            IDeviceProductOperator operator = productOperatorMap.get(productId);
            if (null != operator) {
                return Mono.just(operator);
            }
        }
        IDeviceProductOperator deviceOperator = createProductOperator(productId);
        return deviceOperator.getConfig(EDeviceConfigKey.protocol)
            .doOnNext(r -> productOperatorMap.put(productId, deviceOperator))
            .map((r) -> deviceOperator);
    }

    private DefaultDeviceOperator createOperator(String deviceId) {
        return new DefaultDeviceOperator(deviceId, configStorageManager, deviceOperationBroker, this, interceptor,
            stateChecker);
    }

    private DefaultDeviceProductOperator createProductOperator(String id) {
        return new DefaultDeviceProductOperator(id, protocolSupports, configStorageManager,
            () -> clusterManager.<String>getSet("device-product-bind:" + id).values().flatMap(this::getDevice));
    }

    /**
     * for auto register.
     *
     * @param deviceInfo
     *            设备基础信息
     * @return operator
     */
    @Override
    public Mono<IDeviceOperator> register(DeviceInfo deviceInfo) {
        return Mono.defer(() -> {
            DefaultDeviceOperator operator = createOperator(deviceInfo.getId());
            operatorCache.put(operator.getDeviceId(), Mono.<IDeviceOperator>just(operator)
                .filterWhen(device -> device.getSelfConfig(EDeviceConfigKey.productId).hasElement()));

            Map<String, Object> configs = new HashMap<>();
            Optional.ofNullable(deviceInfo.getMetadata())
                .ifPresent(conf -> configs.put(EDeviceConfigKey.metadata.getKey(), conf));
            Optional.ofNullable(deviceInfo.getProtocol())
                .ifPresent(conf -> configs.put(EDeviceConfigKey.protocol.getKey(), conf));
            Optional.ofNullable(deviceInfo.getProductId())
                .ifPresent(conf -> configs.put(EDeviceConfigKey.productId.getKey(), conf));

            Optional.ofNullable(deviceInfo.getConfiguration()).ifPresent(configs::putAll);

            return operator.setConfigs(configs)
                .then(operator.getProtocol())
                .flatMap(protocol -> protocol.onDeviceRegister(operator))
                // 绑定设备到产品
                .then(clusterManager.<String>getSet("device-product-bind:" + deviceInfo.getProductId())
                    .add(deviceInfo.getId()))
                .thenReturn(operator);
        });
    }

    @Override
    public Mono<IDeviceProductOperator> register(ProductInfo productInfo) {
        return Mono.defer(() -> {
            DefaultDeviceProductOperator operator = createProductOperator(productInfo.getId());
            productOperatorMap.put(operator.getId(), operator);
            Map<String, Object> configs = new HashMap<>();
            Optional.ofNullable(productInfo.getMetadata())
                .ifPresent(conf -> configs.put(EDeviceConfigKey.metadata.getKey(), conf));
            Optional.ofNullable(productInfo.getProtocol())
                .ifPresent(conf -> configs.put(EDeviceConfigKey.protocol.getKey(), conf));

            Optional.ofNullable(productInfo.getConfiguration()).ifPresent(configs::putAll);

            return operator.setConfigs(configs)
                .then(operator.getProtocol())
                .flatMap(protocol -> protocol.onProductRegister(operator))
                .thenReturn(operator);
        });
    }

    @Override
    public Mono<Void> unregisterDevice(String deviceId) {
        return this.getDevice(deviceId)
            .flatMap(device -> device.getProtocol().flatMap(protocol -> protocol.onDeviceUnRegister(device)))
            .then(configStorageManager.getStorage("device:" + deviceId).flatMap(IConfigStorage::clear))
            .doFinally(r -> operatorCache.invalidate(deviceId))
            .then();
    }

    @Override
    public Mono<Void> unregisterProduct(String productId) {
        return this.getProduct(productId)
            .flatMap(product -> product.getProtocol().flatMap(protocol -> protocol.onProductUnRegister(product)))
            .then(configStorageManager.getStorage("device-product:" + productId).flatMap(IConfigStorage::clear))
            .doFinally(s -> productOperatorMap.remove(productId))
            .then();
    }

    public void addInterceptor(IDeviceMessageSenderInterceptor interceptor) {
        this.interceptor.addInterceptor(interceptor);
    }

    public void addStateChecker(IDeviceStateChecker deviceStateChecker) {
        this.stateChecker.addDeviceStateChecker(deviceStateChecker);
    }
}
