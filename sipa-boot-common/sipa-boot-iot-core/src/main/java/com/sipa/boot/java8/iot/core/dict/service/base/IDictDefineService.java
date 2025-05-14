package com.sipa.boot.java8.iot.core.dict.service.base;

import com.sipa.boot.java8.iot.core.dict.base.IDictDefine;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author caszhou
 * @date 2021/10/5
 */
public interface IDictDefineService {
    Mono<IDictDefine> getDefine(String id);

    Flux<IDictDefine> getAllDefine();

    void addDefine(IDictDefine dictDefine);
}
