package com.sipa.boot.java8.common.log.level;

/**
 * @author feizhihao
 * @date 2019/05/07
 */
public interface TraceLog {
    /**
     * check if trace Enabled
     *
     * @return isTraceEnabled
     */
    boolean isTraceEnabled();

    /**
     * log with trace level
     *
     * @param t
     *            ex
     */
    void trace(Throwable t);

    /**
     * log with trace level
     *
     * @param format
     *            log format
     * @param arguments
     *            log arguments
     */
    void trace(String format, Object... arguments);

    /**
     * log with trace level
     *
     * @param t
     *            ex
     * @param format
     *            log format
     * @param arguments
     *            log arguments
     */
    void trace(Throwable t, String format, Object... arguments);
}
