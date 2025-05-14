package com.sipa.boot.java8.common.log;

import com.sipa.boot.java8.common.log.level.*;

/**
 * @author feizhihao
 * @date 2019/05/07
 */
public interface Log extends TraceLog, DebugLog, InfoLog, WarnLog, ErrorLog {
    /**
     * get log type name
     *
     * @return log type name
     */
    String getName();

    /**
     * check specific level is enabled
     *
     * @param level
     *            log level
     * @return is enabled
     */
    boolean isEnabled(Level level);

    /**
     * do log
     *
     * @param level
     *            log level
     * @param format
     *            log format
     * @param arguments
     *            log arguments
     */
    void log(Level level, String format, Object... arguments);

    /**
     * do log
     *
     * @param level
     *            log level
     * @param t
     *            Throwable
     * @param format
     *            log format
     * @param arguments
     *            log arguments
     */
    void log(Level level, Throwable t, String format, Object... arguments);
}
