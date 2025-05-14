package com.sipa.boot.java8.common.utils;

import java.util.Objects;

/**
 * @author zhouxiajie
 * @date 2020/3/18
 */
public class MetricUtils {
    /**
     * A_[0] -> A___0__
     *
     * @param metric
     *            a2l指标
     * @return tsdb指标
     */
    public static String transform(String metric) {
        return Objects.requireNonNull(metric).replaceAll("[\\[\\]]", "__");
    }

    /**
     * A___0__ -> A_[0]
     *
     * @param metric
     *            tsdb指标
     * @return a2l指标
     */
    public static String untransform(String metric) {
        return Objects.requireNonNull(metric).replaceAll("__(\\d+)__", "[$1]");
    }
}
