package com.sipa.boot.java8.common.archs.canary.context;

import com.alibaba.ttl.TransmittableThreadLocal;

/**
 * The Ribbon filter context holder.
 *
 * @author Xiajie Zhou
 */
public class RibbonFilterContextHolder {
    /**
     * Stores the {@link RibbonFilterContext} for current thread.
     */
    private static final TransmittableThreadLocal<RibbonFilterContext> contextHolder =
        new TransmittableThreadLocal<RibbonFilterContext>() {
            @Override
            protected RibbonFilterContext initialValue() {
                return new DefaultRibbonFilterContext();
            }
        };

    /**
     * Retrieves the current thread bound instance of {@link RibbonFilterContext}.
     *
     * @return the current context
     */
    public static RibbonFilterContext getCurrentContext() {
        return contextHolder.get();
    }

    /**
     * Clears the current context.
     */
    public static void clearCurrentContext() {
        contextHolder.remove();
    }
}
