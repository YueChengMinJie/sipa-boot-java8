package com.sipa.boot.java8.common.log.dialect.jdk;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import com.sipa.boot.java8.common.log.AbstractLocationAwareLog;
import com.sipa.boot.java8.common.log.text.TextFormatter;

/**
 * @author feizhihao
 * @date 2019/05/07
 */
public class JdkLog extends AbstractLocationAwareLog {
    private static final long serialVersionUID = -6843151523380063975L;

    /**
     * fully qualified class name
     */
    private static final String FQCN_SELF = JdkLog.class.getName();

    private final transient Logger logger;

    // ------------------------------------------------------------------------- Constructor

    public JdkLog(Logger logger) {
        this.logger = logger;
    }

    public JdkLog(Class<?> clazz) {
        this(clazz.getName());
    }

    public JdkLog(String name) {
        this(Logger.getLogger(name));
    }

    @Override
    public String getName() {
        return logger.getName();
    }

    // ------------------------------------------------------------------------- Trace

    @Override
    public boolean isTraceEnabled() {
        return logger.isLoggable(Level.FINEST);
    }

    @Override
    public void trace(String format, Object... arguments) {
        logIfEnabled(Level.FINEST, null, format, arguments);
    }

    @Override
    public void trace(Throwable t, String format, Object... arguments) {
        logIfEnabled(Level.FINEST, t, format, arguments);
    }

    // ------------------------------------------------------------------------- Debug

    @Override
    public boolean isDebugEnabled() {
        return logger.isLoggable(Level.FINE);
    }

    @Override
    public void debug(String format, Object... arguments) {
        logIfEnabled(Level.FINE, null, format, arguments);
    }

    @Override
    public void debug(Throwable t, String format, Object... arguments) {
        logIfEnabled(Level.FINE, t, format, arguments);
    }

    // ------------------------------------------------------------------------- Info

    @Override
    public boolean isInfoEnabled() {
        return logger.isLoggable(Level.INFO);
    }

    @Override
    public void info(String format, Object... arguments) {
        logIfEnabled(Level.INFO, null, format, arguments);
    }

    @Override
    public void info(Throwable t, String format, Object... arguments) {
        logIfEnabled(Level.INFO, t, format, arguments);
    }

    // ------------------------------------------------------------------------- Warn

    @Override
    public boolean isWarnEnabled() {
        return logger.isLoggable(Level.WARNING);
    }

    @Override
    public void warn(String format, Object... arguments) {
        logIfEnabled(Level.WARNING, null, format, arguments);
    }

    @Override
    public void warn(Throwable t, String format, Object... arguments) {
        logIfEnabled(Level.WARNING, t, format, arguments);
    }

    // ------------------------------------------------------------------------- Error

    @Override
    public boolean isErrorEnabled() {
        return logger.isLoggable(Level.SEVERE);
    }

    @Override
    public void error(String format, Object... arguments) {
        logIfEnabled(Level.SEVERE, null, format, arguments);
    }

    @Override
    public void error(Throwable t, String format, Object... arguments) {
        logIfEnabled(Level.SEVERE, t, format, arguments);
    }

    // ------------------------------------------------------------------------- Log

    @Override
    public void log(com.sipa.boot.java8.common.log.level.Level level, String format, Object... arguments) {
        this.log(level, null, format, arguments);
    }

    @Override
    public void log(com.sipa.boot.java8.common.log.level.Level level, Throwable t, String format, Object... arguments) {
        this.log(FQCN_SELF, level, t, format, arguments);
    }

    @Override
    public void log(String fqcn, com.sipa.boot.java8.common.log.level.Level level, Throwable t, String format,
                    Object... arguments) {
        Level jdkLevel;
        switch (level) {
            case TRACE:
                jdkLevel = Level.FINEST;
                break;
            case DEBUG:
                jdkLevel = Level.FINE;
                break;
            case INFO:
                jdkLevel = Level.INFO;
                break;
            case WARN:
                jdkLevel = Level.WARNING;
                break;
            case ERROR:
                jdkLevel = Level.SEVERE;
                break;
            default:
                throw new IllegalArgumentException("Can not identify log level: " + level);
        }
        logIfEnabled(fqcn, jdkLevel, t, format, arguments);
    }

    // ------------------------------------------------------------------------- Private method

    private void logIfEnabled(Level level, Throwable throwable, String format, Object[] arguments) {
        this.logIfEnabled(FQCN_SELF, level, throwable, format, arguments);
    }

    private void logIfEnabled(String callerFqcn, Level level, Throwable throwable, String format, Object[] arguments) {
        if (logger.isLoggable(level)) {
            LogRecord record = new LogRecord(level, TextFormatter.format(format, arguments));
            record.setLoggerName(getName());
            record.setThrown(throwable);
            fillCallerData(callerFqcn, record);
            logger.log(record);
        }
    }

    private static void fillCallerData(String callerFqcn, LogRecord record) {
        StackTraceElement[] steArray = new Throwable().getStackTrace();

        int found = -1;
        String className;
        for (int i = 0; i < steArray.length; i++) {
            className = steArray[i].getClassName();
            if (className.equals(callerFqcn)) {
                found = i;
                break;
            }
        }

        if (found > -1 && found < steArray.length - 1) {
            StackTraceElement ste = steArray[found + 1];
            record.setSourceClassName(ste.getClassName());
            record.setSourceMethodName(ste.getMethodName());
        }
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
    }
}
