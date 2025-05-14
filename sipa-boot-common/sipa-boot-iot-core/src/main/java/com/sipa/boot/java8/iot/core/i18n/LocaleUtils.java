package com.sipa.boot.java8.iot.core.i18n;

import java.util.Locale;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import org.reactivestreams.Publisher;
import org.springframework.context.MessageSource;

import com.sipa.boot.java8.iot.core.exception.I18nSupportException;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Signal;
import reactor.core.publisher.SignalType;
import reactor.util.context.Context;

/**
 * @author caszhou
 * @date 2021/9/24
 */
public final class LocaleUtils {
    public static final Locale DEFAULT_LOCALE = Locale.getDefault();

    private static final ThreadLocal<Locale> CONTEXT_THREAD_LOCAL = new ThreadLocal<>();

    static MessageSource messageSource = UnsupportedMessageSource.instance();

    public static Locale current() {
        Locale locale = CONTEXT_THREAD_LOCAL.get();
        if (locale == null) {
            locale = DEFAULT_LOCALE;
        }
        return locale;
    }

    public static <T, R> R doWith(T data, Locale locale, BiFunction<T, Locale, R> mapper) {
        try {
            CONTEXT_THREAD_LOCAL.set(locale);
            return mapper.apply(data, locale);
        } finally {
            CONTEXT_THREAD_LOCAL.remove();
        }
    }

    public static void doWith(Locale locale, Consumer<Locale> consumer) {
        try {
            CONTEXT_THREAD_LOCAL.set(locale);
            consumer.accept(locale);
        } finally {
            CONTEXT_THREAD_LOCAL.remove();
        }
    }

    public static Function<Context, Context> useLocale(Locale locale) {
        return ctx -> ctx.put(Locale.class, locale);
    }

    public static Mono<Locale> currentReactive() {
        return Mono.subscriberContext().map(ctx -> ctx.getOrDefault(Locale.class, DEFAULT_LOCALE));
    }

    public static <S extends I18nSupportException, R> Mono<R> resolveThrowable(S source,
        BiFunction<S, String, R> mapper) {
        return resolveThrowable(messageSource, source, mapper);
    }

    public static <S extends I18nSupportException, R> Mono<R> resolveThrowable(MessageSource messageSource, S source,
        BiFunction<S, String, R> mapper) {
        return doWithReactive(messageSource, source, I18nSupportException::getI18nCode, mapper, source.getArgs());
    }

    public static <S extends Throwable, R> Mono<R> resolveThrowable(S source, BiFunction<S, String, R> mapper,
        Object... args) {
        return resolveThrowable(messageSource, source, mapper, args);
    }

    public static <S extends Throwable, R> Mono<R> resolveThrowable(MessageSource messageSource, S source,
        BiFunction<S, String, R> mapper, Object... args) {
        return doWithReactive(messageSource, source, Throwable::getMessage, mapper, args);
    }

    public static <S, R> Mono<R> doWithReactive(S source, Function<S, String> message, BiFunction<S, String, R> mapper,
        Object... args) {
        return doWithReactive(messageSource, source, message, mapper, args);
    }

    public static <S, R> Mono<R> doWithReactive(MessageSource messageSource, S source, Function<S, String> message,
        BiFunction<S, String, R> mapper, Object... args) {
        return currentReactive().map(locale -> {
            String msg = message.apply(source);
            String newMsg = resolveMessage(messageSource, locale, msg, msg, args);
            return mapper.apply(source, newMsg);
        });
    }

    public static Mono<String> resolveMessageReactive(String code, Object... args) {
        return currentReactive().map(locale -> resolveMessage(messageSource, locale, code, code, args));
    }

    public static Mono<String> resolveMessageReactive(MessageSource messageSource, String code, Object... args) {
        return currentReactive().map(locale -> resolveMessage(messageSource, locale, code, code, args));
    }

    public static String resolveMessage(String code, Locale locale, String defaultMessage, Object... args) {
        return resolveMessage(messageSource, locale, code, defaultMessage, args);
    }

    public static String resolveMessage(MessageSource messageSource, Locale locale, String code, String defaultMessage,
        Object... args) {
        return messageSource.getMessage(code, args, defaultMessage, locale);
    }

    public static String resolveMessage(String code, Object... args) {
        return resolveMessage(messageSource, current(), code, code, args);
    }

    public static String resolveMessage(String code, String defaultMessage, Object... args) {
        return resolveMessage(messageSource, current(), code, defaultMessage, args);
    }

    public static String resolveMessage(MessageSource messageSource, String code, String defaultMessage,
        Object... args) {
        return resolveMessage(messageSource, current(), code, defaultMessage, args);
    }

    public static <T> Consumer<Signal<T>> on(SignalType type, BiConsumer<Signal<T>, Locale> operation) {
        return signal -> {
            if (signal.getType() != type) {
                return;
            }
            Locale locale = signal.getContext().getOrDefault(Locale.class, DEFAULT_LOCALE);

            doWith(locale, l -> operation.accept(signal, l));
        };
    }

    @SuppressWarnings("unchecked")
    public static <E, T extends Publisher<E>> Function<T, T> doOn(SignalType type,
        BiConsumer<Signal<E>, Locale> operation) {
        return publisher -> {
            if (publisher instanceof Mono) {
                return (T)Mono.from(publisher).doOnEach(on(type, operation));
            }
            return (T)Flux.from(publisher).doOnEach(on(type, operation));
        };
    }

    public static <E, T extends Publisher<E>> Function<T, T> doOnNext(Consumer<E> operation) {
        return doOn(SignalType.ON_NEXT, (s, l) -> operation.accept(s.get()));
    }

    public static <E, T extends Publisher<E>> Function<T, T> doOnNext(BiConsumer<E, Locale> operation) {
        return doOn(SignalType.ON_NEXT, (s, l) -> operation.accept(s.get(), l));
    }

    public static <E, T extends Publisher<E>> Function<T, T> doOnError(Consumer<Throwable> operation) {
        return doOn(SignalType.ON_ERROR, (s, l) -> operation.accept(s.getThrowable()));
    }

    public static <E, T extends Publisher<E>> Function<T, T> doOnError(BiConsumer<Throwable, Locale> operation) {
        return doOn(SignalType.ON_ERROR, (s, l) -> operation.accept(s.getThrowable(), l));
    }
}
