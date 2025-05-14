package com.sipa.boot.java8.common.archs.cost;

import java.util.Map;
import java.util.function.IntConsumer;
import java.util.function.Supplier;

/**
 * @author zhouxiajie
 * @date 2021/4/12
 */
public class TraceHolder {
    /**
     * 有返回值调用
     */
    public static void run(TraceWatch traceWatch, String taskName, Supplier<Map<String, String>> supplier) {
        Map<String, String> metadata = null;
        try {
            traceWatch.start(taskName);

            metadata = supplier.get();
        } finally {
            traceWatch.stop(metadata);
        }
    }

    /**
     * 无返回值调用
     */
    public static void run(TraceWatch traceWatch, String taskName, IntConsumer function) {
        try {
            traceWatch.start(taskName);

            // nothing in it
            function.accept(0);
        } finally {
            traceWatch.stop();
        }
    }
}
