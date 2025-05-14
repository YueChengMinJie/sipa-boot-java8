package com.sipa.boot.java8.common.log.level;

/**
 * @author feizhihao
 * @date 2019/05/07
 */
public interface DebugLog {
    /**
     * check if Debug Enabled
     *
     * @return isDebugEnabled
     */
    boolean isDebugEnabled();

    /**
     * log with debug level
     *
     * @param t
     *            ex
     */
    void debug(Throwable t);

    /**
     * log with debug level
     *
     * @param format
     *            log format
     * @param arguments
     *            log arguments
     */
    void debug(String format, Object... arguments);

    /**
     * log with debug level
     *
     * @param t
     *            ex
     * @param format
     *            log format
     * @param arguments
     *            log arguments
     */
    void debug(Throwable t, String format, Object... arguments);
}
