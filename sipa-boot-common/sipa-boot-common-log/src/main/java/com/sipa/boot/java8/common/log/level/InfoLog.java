package com.sipa.boot.java8.common.log.level;

/**
 * @author feizhihao
 * @date 2019/05/07
 */
public interface InfoLog {
    /**
     * check if info Enabled
     *
     * @return isInfoEnabled
     */
    boolean isInfoEnabled();

    /**
     * log with info level
     *
     * @param t
     *            ex
     */
    void info(Throwable t);

    /**
     * log with info level
     *
     * @param format
     *            log format
     * @param arguments
     *            log arguments
     */
    void info(String format, Object... arguments);

    /**
     * log with info level
     *
     * @param t
     *            ex
     * @param format
     *            log format
     * @param arguments
     *            log arguments
     */
    void info(Throwable t, String format, Object... arguments);
}
