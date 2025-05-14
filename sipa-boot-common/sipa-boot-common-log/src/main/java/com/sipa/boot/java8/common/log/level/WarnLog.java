package com.sipa.boot.java8.common.log.level;

/**
 * @author feizhihao
 * @date 2019/05/07
 */
public interface WarnLog {
    /**
     * check if warn Enabled
     *
     * @return isWarnEnabled
     */
    boolean isWarnEnabled();

    /**
     * log with warn level
     *
     * @param t
     *            ex
     */
    void warn(Throwable t);

    /**
     * log with warn level
     *
     * @param format
     *            log format
     * @param arguments
     *            log arguments
     */
    void warn(String format, Object... arguments);

    /**
     * log with warn level
     *
     * @param t
     *            ex
     * @param format
     *            log format
     * @param arguments
     *            log arguments
     */
    void warn(Throwable t, String format, Object... arguments);
}
