package com.sipa.boot.java8.iot.core.device;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import com.sipa.boot.java8.iot.core.config.base.IConfigKey;
import com.sipa.boot.java8.iot.core.config.base.IConfigStorage;
import com.sipa.boot.java8.iot.core.config.base.IConfigStorageManager;
import com.sipa.boot.java8.iot.core.config.base.IStorageConfigurable;
import com.sipa.boot.java8.iot.core.device.base.IDeviceOperator;
import com.sipa.boot.java8.iot.core.device.base.IDeviceProductOperator;
import com.sipa.boot.java8.iot.core.enumerate.EDeviceConfigKey;
import com.sipa.boot.java8.iot.core.metadata.base.IDeviceMetadata;
import com.sipa.boot.java8.iot.core.protocol.base.IProtocolSupport;
import com.sipa.boot.java8.iot.core.protocol.base.IProtocolSupports;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author caszhou
 * @date 2021/10/2
 */
public class DefaultDeviceProductOperator implements IDeviceProductOperator, IStorageConfigurable {
    private final String id;

    private volatile IDeviceMetadata metadata;

    private final Mono<IConfigStorage> storageMono;

    private final Supplier<Flux<IDeviceOperator>> devicesSupplier;

    private long lstMetadataChangeTime;

    private static final IConfigKey<Long> LAST_METADATA_TIME_KEY = IConfigKey.of("lst_metadata_time");

    private final Mono<IDeviceMetadata> inLocalMetadata;

    private final Mono<IDeviceMetadata> metadataMono;

    private final Mono<IProtocolSupport> protocolSupportMono;

    @Deprecated
    public DefaultDeviceProductOperator(String id, IProtocolSupports supports, IConfigStorageManager manager) {
        this(id, supports, manager, Flux::empty);
    }

    public DefaultDeviceProductOperator(String id, IProtocolSupports supports, IConfigStorageManager manager,
        Supplier<Flux<IDeviceOperator>> supplier) {
        this.id = id;
        // this.protocolSupports = supports;
        this.storageMono = manager.getStorage("device-product:".concat(id));
        this.devicesSupplier = supplier;
        this.inLocalMetadata = Mono.fromSupplier(() -> metadata);
        this.protocolSupportMono = this.getConfig(EDeviceConfigKey.protocol).flatMap(supports::getProtocol);

        Mono<IDeviceMetadata> loadMetadata = Mono
            .zip(this.getProtocol().map(IProtocolSupport::getMetadataCodec), this.getConfig(EDeviceConfigKey.metadata),
                this.getConfig(LAST_METADATA_TIME_KEY).switchIfEmpty(Mono.defer(() -> {
                    long now = System.currentTimeMillis();
                    return this.setConfig(LAST_METADATA_TIME_KEY, now).thenReturn(now);
                })))
            .flatMap(tp3 -> tp3.getT1().decode(tp3.getT2()).doOnNext(decode -> {
                this.metadata = decode;
                this.lstMetadataChangeTime = tp3.getT3();
            }));
        this.metadataMono = this.getConfig(LAST_METADATA_TIME_KEY).flatMap(time -> {
            if (time.equals(lstMetadataChangeTime)) {
                return inLocalMetadata;
            }
            return Mono.empty();
        }).switchIfEmpty(loadMetadata);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Mono<IDeviceMetadata> getMetadata() {
        return this.metadataMono;
    }

    @Override
    public Mono<Boolean> setConfigs(Map<String, Object> conf) {
        if (conf.containsKey(EDeviceConfigKey.metadata.getKey())) {
            conf.put(LAST_METADATA_TIME_KEY.getKey(), System.currentTimeMillis());
            return IStorageConfigurable.super.setConfigs(conf).doOnNext(s -> {
                metadata = null;
            }).then(this.getProtocol().flatMap(support -> support.onProductMetadataChanged(this))).thenReturn(true);
        }
        return IStorageConfigurable.super.setConfigs(conf);
    }

    @Override
    public Mono<Boolean> updateMetadata(String metadata) {
        Map<String, Object> configs = new HashMap<>(16);
        configs.put(EDeviceConfigKey.metadata.getKey(), metadata);
        return this.setConfigs(configs);
    }

    @Override
    public Mono<IProtocolSupport> getProtocol() {
        return protocolSupportMono;
    }

    @Override
    public Mono<IConfigStorage> getReactiveStorage() {
        return storageMono;
    }

    @Override
    public Flux<IDeviceOperator> getDevices() {
        return devicesSupplier == null ? Flux.empty() : devicesSupplier.get();
    }
}
