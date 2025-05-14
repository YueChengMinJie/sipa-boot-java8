package com.sipa.boot.java8.common.archs.cost;

/**
 * @author zhouxiajie
 * @date 2021/4/12
 */
public class TraceWatchFactory {
    private TraceWatchFactory() {
    }

    public static TraceWatch newTraceWatch() {
        return new TraceWatch();
    }
}
