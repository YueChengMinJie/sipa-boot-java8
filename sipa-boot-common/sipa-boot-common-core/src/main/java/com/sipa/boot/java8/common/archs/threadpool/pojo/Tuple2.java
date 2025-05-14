package com.sipa.boot.java8.common.archs.threadpool.pojo;

/**
 * @author sunyukun
 * @since 2019/8/6 15:02
 */
public class Tuple2<T1, T2> extends Tuple {
    public Tuple2(T1 first, T2 second) {
        super(first, second);
    }

    public T1 getFirst() {
        return (T1)get(0);
    }

    public T2 getSecond() {
        return (T2)get(1);
    }
}
