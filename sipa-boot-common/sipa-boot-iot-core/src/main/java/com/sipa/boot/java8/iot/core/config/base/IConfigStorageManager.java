package com.sipa.boot.java8.iot.core.config.base;

import reactor.core.publisher.Mono;

/**
 * @author caszhou
 * @date 2021/10/2
 */
public interface IConfigStorageManager {
    Mono<IConfigStorage> getStorage(String id);
}
