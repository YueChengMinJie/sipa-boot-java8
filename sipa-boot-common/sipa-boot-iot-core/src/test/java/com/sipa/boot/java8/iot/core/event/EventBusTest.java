package com.sipa.boot.java8.iot.core.event;

import org.junit.Test;

import com.sipa.boot.java8.common.log.util.Console;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author caszhou
 * @date 2022/3/13
 */
public class EventBusTest {
    @Test
    public void testCount() {
        Flux.just("1", "2", "3").flatMap(Mono::just).count().subscribe(Console::log);
    }
}
