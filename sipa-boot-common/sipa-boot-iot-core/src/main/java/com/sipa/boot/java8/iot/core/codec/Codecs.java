package com.sipa.boot.java8.iot.core.codec;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.annotation.Nonnull;

import org.reactivestreams.Publisher;
import org.springframework.core.ResolvableType;

import com.sipa.boot.java8.common.archs.cache.Caches;
import com.sipa.boot.java8.common.log.Log;
import com.sipa.boot.java8.common.log.LogFactory;
import com.sipa.boot.java8.iot.core.codec.base.ICodec;
import com.sipa.boot.java8.iot.core.codec.base.ICodecsSupport;

/**
 * @author caszhou
 * @date 2021/9/26
 */
public final class Codecs {
    private static final Log log = LogFactory.get(Codecs.class);

    private static final Map<ResolvableType, ICodec<?>> MAPPING = Caches.newCache();

    private static final List<ICodecsSupport> ALL_CODEC = new CopyOnWriteArrayList<>();

    static {
        ServiceLoader.load(ICodecsSupport.class).forEach(ALL_CODEC::add);

        ALL_CODEC.sort(Comparator.comparingInt(ICodecsSupport::getOrder));
    }

    public static void register(ICodecsSupport support) {
        ALL_CODEC.add(support);
        ALL_CODEC.sort(Comparator.comparingInt(ICodecsSupport::getOrder));
    }

    @Nonnull
    private static ICodec<?> resolve(ResolvableType target) {
        for (ICodecsSupport support : ALL_CODEC) {
            Optional<ICodec<?>> lookup = (Optional)support.lookup(target);

            if (lookup.isPresent()) {
                log.debug("lookup codec [{}] for [{}]", lookup.get(), target);
                return lookup.get();
            }
        }
        throw new UnsupportedOperationException("unsupported codec for " + target);
    }

    public static <T> ICodec<T> lookup(@Nonnull Class<? extends T> target) {
        return lookup(ResolvableType.forType(target));
    }

    public static <T> ICodec<T> lookup(ResolvableType type) {
        if (Publisher.class.isAssignableFrom(type.toClass())) {
            type = type.getGeneric(0);
        }
        return (ICodec<T>)MAPPING.computeIfAbsent(type, Codecs::resolve);
    }
}
