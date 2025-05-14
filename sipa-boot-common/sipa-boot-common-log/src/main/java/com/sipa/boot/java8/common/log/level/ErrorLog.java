package com.sipa.boot.java8.common.log.level;

/**
 * @author feizhihao
 * @date 2019/05/07
 */
public interface ErrorLog {
    /**
     * check if Error Enabled
     *
     * @return isErrorEnabled
     */
    boolean isErrorEnabled();

    /**
     * log with error level
     *
     * @param t
     *            ex
     */
    void error(Throwable t);

    /**
     * log with error level
     *
     * @param format
     *            log format
     * @param arguments
     *            log arguments
     */
    void error(String format, Object... arguments);

    /**
     * log with error level
     *
     * @param t
     *            ex
     * @param format
     *            log format
     * @param arguments
     *            log arguments
     */
    void error(Throwable t, String format, Object... arguments);
}
