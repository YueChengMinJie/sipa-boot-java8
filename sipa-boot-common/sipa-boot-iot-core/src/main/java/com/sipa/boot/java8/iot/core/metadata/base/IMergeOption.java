package com.sipa.boot.java8.iot.core.metadata.base;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.collections.MapUtils;

import com.fasterxml.jackson.databind.PropertyMetadata;
import com.sipa.boot.java8.iot.core.enumerate.EDeviceMetadataType;

/**
 * 合并选项，通过一些自定义的选项来指定合并过程中的行为,比如 忽略合并一下拓展配置等
 *
 * @author caszhou
 * @date 2021/9/28
 */
public interface IMergeOption {
    IMergeOption ignoreExists = DefaultMergeOption.ignoreExists;

    IMergeOption overwriteProperty = DefaultMergeOption.overwriteProperty;

    IMergeOption[] DEFAULT_OPTIONS = new IMergeOption[0];

    static PropertyFilter propertyFilter(PropertyFilter filter) {
        return filter;
    }

    String getId();

    enum DefaultMergeOption implements IMergeOption {
        ignoreExists, mergeExpands, overwriteProperty;

        @Override
        public String getId() {
            return name();
        }
    }

    interface PropertyFilter extends IMergeOption, Predicate<PropertyMetadata> {
        @Override
        default String getId() {
            return "PropertyFilter";
        }

        /**
         * 属性过滤校验。返回true表示合并此属性。false表示不合并
         *
         * @param metadata
         *            属性物模型
         * @return 是否合并
         */
        boolean test(PropertyMetadata metadata);

        static boolean doFilter(PropertyMetadata metadata, IMergeOption... option) {
            for (IMergeOption mergeOption : option) {
                if (mergeOption instanceof PropertyFilter) {
                    if (!((PropertyFilter)mergeOption).test(metadata)) {
                        return false;
                    }
                }
            }
            return true;
        }

        static boolean has(IMergeOption[] options) {
            for (IMergeOption option : options) {
                if (option instanceof PropertyFilter) {
                    return true;
                }
            }
            return false;
        }
    }

    class ExpandsMerge implements IMergeOption {
        private static final ExpandsMerge all = new ExpandsMerge(null, Type.ignore) {
            @Override
            public boolean isIgnore(String key) {
                return true;
            }

            @Override
            public void mergeExpands(Map<String, Object> from, BiConsumer<String, Object> to) {
            }
        };

        private final Set<String> keys;

        private final Type type;

        public ExpandsMerge(Set<String> keys, Type type) {
            this.keys = keys;
            this.type = type;
        }

        public static ExpandsMerge ignore(String... keys) {
            return new ExpandsMerge(Arrays.stream(keys).collect(Collectors.toSet()), Type.ignore);
        }

        public static ExpandsMerge remove(String... keys) {
            return new ExpandsMerge(Arrays.stream(keys).collect(Collectors.toSet()), Type.remove);
        }

        public ExpandsMerge ignoreAll() {
            return all;
        }

        @Override
        public String getId() {
            return "expandsMerge";
        }

        public boolean isIgnore(String key) {
            return keys.contains(key);
        }

        public static Optional<ExpandsMerge> from(IMergeOption option) {
            if (option instanceof ExpandsMerge) {
                return Optional.of(((ExpandsMerge)option));
            }
            return Optional.empty();
        }

        public void mergeExpands(Map<String, Object> from, BiConsumer<String, Object> to) {
            from.forEach((key, value) -> {
                if (!isIgnore(key)) {
                    to.accept(key, value);
                }
            });
        }

        public static void doWith(EDeviceMetadataType metadataType, Map<String, Object> from, Map<String, Object> to,
            IMergeOption... options) {
            if (MapUtils.isEmpty(from)) {
                return;
            }
            boolean merged = false;

            if (options != null && options.length != 0) {
                for (IMergeOption option : options) {
                    ExpandsMerge expandsOption = ExpandsMerge.from(option).orElse(null);
                    if (null != expandsOption) {
                        merged = true;
                        expandsOption.mergeExpands(from, to::put);
                        if (expandsOption.type == Type.remove) {
                            expandsOption.keys.forEach(to::remove);
                        }
                    }
                }
            }

            if (!merged) {
                from.forEach(to::put);
            }
        }

        private enum Type {
            ignore, remove;
        }
    }

    static boolean has(IMergeOption option, IMergeOption... target) {
        for (IMergeOption mergeOption : target) {
            if (Objects.equals(option, mergeOption)) {
                return true;
            }
        }
        return false;
    }
}
