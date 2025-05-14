package com.sipa.boot.java8.common.archs.cost;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Maps;
import com.sipa.boot.java8.common.constants.SipaBootCommonConstants;
import com.sipa.boot.java8.common.utils.TimeUtils;

/**
 * @author zhouxiajie
 * @date 2021/4/12
 */
public class TraceWatch implements AutoCloseable {
    private long startMs;

    private String currentTaskName;

    private final Map<String, List<TaskInfo>> taskMap =
        Maps.newHashMapWithExpectedSize(SipaBootCommonConstants.Number.INT_4);

    private final Map<String, Object> data = Maps.newHashMapWithExpectedSize(SipaBootCommonConstants.Number.INT_4);

    TraceWatch() {
    }

    /**
     * 开始时间差类型指标记录，如果需要终止，请调用 {@link #stop()}
     *
     * @return trace watch
     * @throws IllegalStateException
     *             watch不在跑
     */
    public TraceWatch start() throws IllegalStateException {
        return start(StringUtils.EMPTY);
    }

    /**
     * 开始时间差类型指标记录，如果需要终止，请调用 {@link #stop()}
     *
     * @param taskName
     *            指标名
     * @return trace watch
     * @throws IllegalStateException
     *             watch不在跑
     */
    public TraceWatch start(String taskName) throws IllegalStateException {
        if (this.currentTaskName != null) {
            throw new IllegalStateException("Can't start TraceWatch: it's already running");
        }

        this.currentTaskName = taskName;

        this.startMs = TimeUtils.nowMs();

        return this;
    }

    /**
     * 终止时间差类型指标记录，调用前请确保已经调用
     *
     * @throws IllegalStateException
     *             watch不在跑
     */
    public void stop() throws IllegalStateException {
        if (this.currentTaskName == null) {
            throw new IllegalStateException("Can't stop TraceWatch: it's not running");
        }
        long lastTime = TimeUtils.nowMs() - this.startMs;

        TaskInfo info = new TaskInfo(this.currentTaskName, lastTime);

        this.taskMap.computeIfAbsent(this.currentTaskName, e -> new LinkedList<>()).add(info);

        this.currentTaskName = null;
    }

    /**
     * 终止时间差类型指标记录，调用前请确保已经调用
     *
     * @param metadata
     *            元数据
     * @throws IllegalStateException
     *             watch不在跑
     */
    public void stop(Map<String, String> metadata) throws IllegalStateException {
        if (this.currentTaskName == null) {
            throw new IllegalStateException("Can't stop TraceWatch: it's not running");
        }
        long lastTime = TimeUtils.nowMs() - this.startMs;

        TaskInfo info = new TaskInfo(this.currentTaskName, lastTime, metadata);

        this.taskMap.computeIfAbsent(this.currentTaskName, e -> new LinkedList<>()).add(info);

        this.currentTaskName = null;
    }

    /**
     * 直接记录指标数据，不局限于时间差类型
     *
     * @param taskName
     *            指标名
     * @param data
     *            指标数据
     */
    public void record(String taskName, Long data) {
        TaskInfo info = new TaskInfo(taskName, data);

        this.taskMap.computeIfAbsent(taskName, e -> new LinkedList<>()).add(info);
    }

    /**
     * 缓存
     *
     * @param taskName
     *            指标名
     * @param obj
     *            缓存
     */
    public void cache(String taskName, Object obj) {
        data.put(taskName, obj);
    }

    public Map<String, List<TaskInfo>> getTaskMap() {
        return taskMap;
    }

    public Map<String, Object> getData() {
        return data;
    }

    @Override
    public void close() throws Exception {
        this.stop();
    }

    public static final class TaskInfo {
        private final String taskName;

        private final Long cost;

        private final Map<String, String> metadata;

        TaskInfo(String taskName, Long cost) {
            this.taskName = taskName;
            this.cost = cost;
            this.metadata = null;
        }

        TaskInfo(String taskName, Long cost, Map<String, String> metadata) {
            this.taskName = taskName;
            this.cost = cost;
            this.metadata = metadata;
        }

        public String getTaskName() {
            return taskName;
        }

        public Long getCost() {
            return cost;
        }

        public Map<String, String> getMetadata() {
            return metadata;
        }
    }
}
