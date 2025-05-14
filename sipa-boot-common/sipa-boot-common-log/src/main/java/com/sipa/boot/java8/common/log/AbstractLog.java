package com.sipa.boot.java8.common.log;

import java.io.Serializable;

import com.sipa.boot.java8.common.log.level.Level;

/**
 * @author feizhihao
 * @date 2019/05/07
 */
public abstract class AbstractLog implements Log, Serializable {
    private static final long serialVersionUID = -3211115409504005616L;

    @Override
    public boolean isEnabled(Level level) {
        switch (level) {
            case TRACE:
                return isTraceEnabled();
            case DEBUG:
                return isDebugEnabled();
            case INFO:
                return isInfoEnabled();
            case WARN:
                return isWarnEnabled();
            case ERROR:
                return isErrorEnabled();
            default:
                throw new IllegalArgumentException("Can not identify log level: " + level);
        }
    }

    @Override
    public void trace(Throwable t) {
        this.trace(t, t.getMessage());
    }

    @Override
    public void debug(Throwable t) {
        this.debug(t, t.getMessage());
    }

    @Override
    public void info(Throwable t) {
        this.info(t, t.getMessage());
    }

    @Override
    public void warn(Throwable t) {
        this.warn(t, t.getMessage());
    }

    @Override
    public void error(Throwable t) {
        this.error(t, t.getMessage());
    }
}
