package com.sipa.boot.java8.iot.core.logger;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import org.slf4j.MDC;

import com.sipa.boot.java8.common.log.Log;
import com.sipa.boot.java8.common.log.LogFactory;

import reactor.core.publisher.Mono;
import reactor.core.publisher.Signal;
import reactor.core.publisher.SignalType;
import reactor.core.publisher.SynchronousSink;
import reactor.util.context.Context;

/**
 * @author caszhou
 * @date 2021/9/26
 */
public class ReactiveLogger {
    private static final Log log = LogFactory.get(ReactiveLogger.class);

    private static final String CONTEXT_KEY = ReactiveLogger.class.getName();

    public static Function<Context, Context> start(String key, String value) {
        return start(Collections.singletonMap(key, value));
    }

    public static Function<Context, Context> start(String... keyAndValue) {
        Map<String, String> map = new HashMap<>();
        for (int i = 0, len = keyAndValue.length / 2; i < len; i++) {
            map.put(keyAndValue[i * 2], keyAndValue[i * 2 + 1]);
        }
        return start(map);
    }

    public static Mono<Void> mdc(String key, String value) {
        return Mono.<Void>empty().subscriberContext(start(key, value));
    }

    public static Mono<Void> mdc(String... keyAndValue) {
        return Mono.<Void>empty().subscriberContext(start(keyAndValue));
    }

    public static Function<Context, Context> start(Map<String, String> context) {
        return ctx -> {
            Optional<Map<String, String>> maybeContextMap = ctx.getOrEmpty(CONTEXT_KEY);
            if (maybeContextMap.isPresent()) {
                maybeContextMap.get().putAll(context);
                return ctx;
            } else {
                return ctx.put(CONTEXT_KEY, new LinkedHashMap<>(context));
            }
        };
    }

    public static <T> void log(Context context, Consumer<Map<String, String>> logger) {
        Optional<Map<String, String>> maybeContextMap = context.getOrEmpty(CONTEXT_KEY);
        if (!maybeContextMap.isPresent()) {
            logger.accept(new HashMap<>());
        } else {
            Map<String, String> ctx = maybeContextMap.get();
            MDC.setContextMap(ctx);
            try {
                logger.accept(ctx);
            } finally {
                MDC.clear();
            }
        }
    }

    public static <T> Consumer<Signal<T>> on(SignalType type, BiConsumer<Map<String, String>, Signal<T>> logger) {
        return signal -> {
            if (signal.getType() != type) {
                return;
            }
            Optional<Map<String, String>> maybeContextMap = signal.getContext().getOrEmpty(CONTEXT_KEY);
            if (!maybeContextMap.isPresent()) {
                logger.accept(new HashMap<>(), signal);
            } else {
                Map<String, String> ctx = maybeContextMap.get();
                MDC.setContextMap(ctx);
                try {
                    logger.accept(ctx, signal);
                } finally {
                    MDC.clear();
                }
            }
        };
    }

    public static Mono<Void> mdc(Consumer<Map<String, String>> consumer) {
        return Mono.subscriberContext().doOnNext(ctx -> {
            Optional<Map<String, String>> maybeContextMap = ctx.getOrEmpty(CONTEXT_KEY);
            if (maybeContextMap.isPresent()) {
                consumer.accept(maybeContextMap.get());
            } else {
                consumer.accept(Collections.emptyMap());
                log.warn(
                    "logger context is empty,please call publisher.subscriberContext(ReactiveLogger.mdc()) first!");
            }
        }).then();
    }

    public static <T, R> BiConsumer<T, SynchronousSink<R>> handle(BiConsumer<T, SynchronousSink<R>> logger) {
        return (t, rFluxSink) -> {
            log(rFluxSink.currentContext(), context -> {
                logger.accept(t, rFluxSink);
            });
        };
    }

    public static <T> Consumer<Signal<T>> onNext(Consumer<T> logger) {
        return on(SignalType.ON_NEXT, (ctx, signal) -> {
            logger.accept(signal.get());
        });
    }

    public static <T> Consumer<Signal<T>> onComplete(Runnable logger) {
        return on(SignalType.ON_COMPLETE, (ctx, signal) -> {
            logger.run();
        });
    }

    public static <T> Consumer<Signal<T>> onError(Consumer<Throwable> logger) {
        return on(SignalType.ON_ERROR, (ctx, signal) -> {
            logger.accept(signal.getThrowable());
        });
    }
}
