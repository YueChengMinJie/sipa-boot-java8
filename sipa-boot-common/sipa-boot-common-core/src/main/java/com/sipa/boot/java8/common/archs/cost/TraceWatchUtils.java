package com.sipa.boot.java8.common.archs.cost;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * @author caszhou
 * @date 2021/7/5
 */
public class TraceWatchUtils {
    /**
     * Get cost of start empty task trace watch by index.
     *
     * @param traceWatch
     *            empty task trace watch
     * @return cost
     */
    public static long getCost(TraceWatch traceWatch, int index) {
        if (Objects.nonNull(traceWatch)) {
            Map<String, List<TraceWatch.TaskInfo>> map = traceWatch.getTaskMap();
            if (MapUtils.isNotEmpty(map)) {
                List<TraceWatch.TaskInfo> list = map.get(StringUtils.EMPTY);
                if (CollectionUtils.isNotEmpty(list) && list.size() > index) {
                    TraceWatch.TaskInfo taskInfo = list.get(index);
                    if (Objects.nonNull(taskInfo)) {
                        return taskInfo.getCost();
                    }
                }
            }
        }
        return 0;
    }

    /**
     * Get cost of start empty task trace watch at index 0.
     *
     * @param traceWatch
     *            empty task trace watch
     * @return cost
     */
    public static long getCost(TraceWatch traceWatch) {
        return getCost(traceWatch, 0);
    }
}
