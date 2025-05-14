package com.sipa.boot.java8.common.log.dialect.logback;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.spi.LocationAwareLogger;

import com.sipa.boot.java8.common.log.AbstractLocationAwareLog;
import com.sipa.boot.java8.common.log.level.Level;
import com.sipa.boot.java8.common.log.text.TextFormatter;

/**
 * @author feizhihao
 * @date 2019/05/07
 */
public class LogBackLog extends AbstractLocationAwareLog {
    private static final String FQCN = LogBackLog.class.getName();

    private static final long serialVersionUID = -6843151523380063975L;

    private final transient Logger logger;

    // ------------------------------------------------------------------------- Constructor

    public LogBackLog(Logger logger) {
        this.logger = logger;
    }

    public LogBackLog(Class<?> clazz) {
        this(LoggerFactory.getLogger(clazz));
    }

    public LogBackLog(String name) {
        this(LoggerFactory.getLogger(name));
    }

    @Override
    public String getName() {
        return logger.getName();
    }

    // ------------------------------------------------------------------------- Trace

    @Override
    public boolean isTraceEnabled() {
        return logger.isTraceEnabled();
    }

    @Override
    public void trace(String format, Object... arguments) {
        logger.trace(format, arguments);
    }

    @Override
    public void trace(Throwable t, String format, Object... arguments) {
        if (locationNotAwareLog(LocationAwareLogger.TRACE_INT, t, format, arguments)) {
            logger.trace(TextFormatter.format(format, arguments), t);
        }
    }

    // ------------------------------------------------------------------------- Debug

    @Override
    public boolean isDebugEnabled() {
        return logger.isDebugEnabled();
    }

    @Override
    public void debug(String format, Object... arguments) {
        if (locationNotAwareLog(LocationAwareLogger.DEBUG_INT, format, arguments)) {
            if (isDebugEnabled()) {
                logger.debug(format, arguments);
            }
        }
    }

    @Override
    public void debug(Throwable t, String format, Object... arguments) {
        if (locationNotAwareLog(LocationAwareLogger.DEBUG_INT, t, format, arguments)) {
            logger.debug(TextFormatter.format(format, arguments), t);
        }
    }

    // ------------------------------------------------------------------------- Info

    @Override
    public boolean isInfoEnabled() {
        return logger.isInfoEnabled();
    }

    @Override
    public void info(String format, Object... arguments) {
        if (locationNotAwareLog(LocationAwareLogger.INFO_INT, format, arguments)) {
            logger.info(format, arguments);
        }
    }

    @Override
    public void info(Throwable t, String format, Object... arguments) {
        if (locationNotAwareLog(LocationAwareLogger.INFO_INT, t, format, arguments)) {
            logger.info(TextFormatter.format(format, arguments), t);
        }
    }

    // ------------------------------------------------------------------------- Warn

    @Override
    public boolean isWarnEnabled() {
        return logger.isWarnEnabled();
    }

    @Override
    public void warn(String format, Object... arguments) {
        if (locationNotAwareLog(LocationAwareLogger.WARN_INT, format, arguments)) {
            logger.warn(format, arguments);
        }
    }

    @Override
    public void warn(Throwable t, String format, Object... arguments) {
        if (locationNotAwareLog(LocationAwareLogger.WARN_INT, t, format, arguments)) {
            logger.warn(TextFormatter.format(format, arguments), t);
        }
    }

    // ------------------------------------------------------------------------- Error

    @Override
    public boolean isErrorEnabled() {
        return logger.isErrorEnabled();
    }

    @Override
    public void error(String format, Object... arguments) {
        logger.error(format, arguments);
    }

    @Override
    public void error(Throwable t, String format, Object... arguments) {
        if (locationNotAwareLog(LocationAwareLogger.ERROR_INT, t, format, arguments)) {
            logger.error(TextFormatter.format(format, arguments), t);
        }
    }

    // ------------------------------------------------------------------------- Log

    @Override
    public void log(Level level, String format, Object... arguments) {
        this.log(level, null, format, arguments);
    }

    @Override
    public void log(Level level, Throwable t, String format, Object... arguments) {
        this.log(FQCN, level, t, format, arguments);
    }

    @Override
    public void log(String fqcn, Level level, Throwable t, String format, Object... arguments) {
        int levelInt;
        switch (level) {
            case TRACE:
                levelInt = LocationAwareLogger.TRACE_INT;
                break;
            case DEBUG:
                levelInt = LocationAwareLogger.DEBUG_INT;
                break;
            case INFO:
                levelInt = LocationAwareLogger.INFO_INT;
                break;
            case WARN:
                levelInt = LocationAwareLogger.WARN_INT;
                break;
            case ERROR:
                levelInt = LocationAwareLogger.ERROR_INT;
                break;
            default:
                throw new IllegalArgumentException("Can not identify log level: " + level);
        }
        this.locationAwareLog(fqcn, levelInt, t, format, arguments);
    }

    // -------------------------------------------------------------------------------------------------- Private method

    private boolean locationNotAwareLog(int levelInt, String msgTemplate, Object[] arguments) {
        return locationNotAwareLog(levelInt, null, msgTemplate, arguments);
    }

    private boolean locationNotAwareLog(int levelInt, Throwable t, String msgTemplate, Object[] arguments) {
        return !locationAwareLog(FQCN, levelInt, t, msgTemplate, arguments);
    }

    /**
     * 打印日志<br>
     * 此方法用于兼容底层日志实现，通过传入当前包装类名，以解决打印日志中行号错误问题
     *
     * @param fqcn
     *            完全限定类名(Fully Qualified Class Name)，用于纠正定位错误行号
     * @param levelInt
     *            日志级别，使用LocationAwareLogger中的常量
     * @param t
     *            异常
     * @param msgTemplate
     *            消息模板
     * @param arguments
     *            参数
     * @return 是否支持 LocationAwareLogger对象，如果不支持需要日志方法调用被包装类的相应方法
     */
    private boolean locationAwareLog(String fqcn, int levelInt, Throwable t, String msgTemplate, Object[] arguments) {
        if (this.logger instanceof LocationAwareLogger) {
            // 由于slf4j-log4j12中此方法的实现存在bug，故在此拼接参数
            ((LocationAwareLogger)this.logger).log(null, fqcn, levelInt, TextFormatter.format(msgTemplate, arguments),
                null, t);
            return true;
        } else {
            return false;
        }
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
    }
}
