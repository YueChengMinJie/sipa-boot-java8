package com.sipa.boot.java8.common.log;

import com.sipa.boot.java8.common.log.level.Level;

/**
 * @author feizhihao
 * @date 2019/05/07
 */
public interface LocationAwareLog extends Log {
    /**
     * do log
     *
     * @param fqcn
     *            Fully Qualified Class Name
     * @param level
     *            level
     * @param throwable
     *            throwable
     * @param format
     *            msg template
     * @param arguments
     *            arguments
     */
    void log(String fqcn, Level level, Throwable throwable, String format, Object... arguments);
}
