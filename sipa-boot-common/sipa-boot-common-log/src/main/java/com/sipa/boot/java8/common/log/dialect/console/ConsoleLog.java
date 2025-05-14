package com.sipa.boot.java8.common.log.dialect.console;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.sipa.boot.java8.common.log.AbstractLog;
import com.sipa.boot.java8.common.log.level.Level;
import com.sipa.boot.java8.common.log.text.TextFormatter;
import com.sipa.boot.java8.common.log.util.Console;

/**
 * @author feizhihao
 * @date 2019/05/07
 */
public class ConsoleLog extends AbstractLog {
    private static final long serialVersionUID = -6843151523380063975L;

    private static String logFormat = "[{date}] [{level}] {name}: {msg}";

    private static Level level = Level.DEBUG;

    private String name;

    // ------------------------------------------------------------------------- Constructor

    public ConsoleLog(Class<?> clazz) {
        this.name = clazz.getName();
    }

    public ConsoleLog(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    // ------------------------------------------------------------------------- Trace

    @Override
    public boolean isTraceEnabled() {
        return level.compareTo(Level.TRACE) <= 0;
    }

    @Override
    public void trace(String format, Object... arguments) {
        log(Level.TRACE, format, arguments);
    }

    @Override
    public void trace(Throwable t, String format, Object... arguments) {
        log(Level.TRACE, t, format, arguments);
    }

    // ------------------------------------------------------------------------- Debug

    @Override
    public boolean isDebugEnabled() {
        return level.compareTo(Level.DEBUG) <= 0;
    }

    @Override
    public void debug(String format, Object... arguments) {
        log(Level.DEBUG, format, arguments);
    }

    @Override
    public void debug(Throwable t, String format, Object... arguments) {
        log(Level.DEBUG, t, format, arguments);
    }

    // ------------------------------------------------------------------------- Info

    @Override
    public boolean isInfoEnabled() {
        return level.compareTo(Level.INFO) <= 0;
    }

    @Override
    public void info(String format, Object... arguments) {
        log(Level.INFO, format, arguments);
    }

    @Override
    public void info(Throwable t, String format, Object... arguments) {
        log(Level.INFO, t, format, arguments);
    }

    // ------------------------------------------------------------------------- Warn

    @Override
    public boolean isWarnEnabled() {
        return level.compareTo(Level.WARN) <= 0;
    }

    @Override
    public void warn(String format, Object... arguments) {
        log(Level.WARN, format, arguments);
    }

    @Override
    public void warn(Throwable t, String format, Object... arguments) {
        log(Level.WARN, t, format, arguments);
    }

    // ------------------------------------------------------------------------- Error

    @Override
    public boolean isErrorEnabled() {
        return level.compareTo(Level.ERROR) <= 0;
    }

    @Override
    public void error(String format, Object... arguments) {
        log(Level.ERROR, format, arguments);
    }

    @Override
    public void error(Throwable t, String format, Object... arguments) {
        log(Level.ERROR, t, format, arguments);
    }

    // ------------------------------------------------------------------------- Log

    @Override
    public void log(Level level, String format, Object... arguments) {
        this.log(level, null, format, arguments);
    }

    @Override
    public void log(Level level, Throwable t, String format, Object... arguments) {
        if (!isEnabled(level)) {
            return;
        }

        final Map<String, Object> map = new HashMap<>(4);
        map.put("date", new Date());
        map.put("level", level.toString());
        map.put("name", this.name);
        map.put("msg", TextFormatter.format(format, arguments));

        String logMsg = TextFormatter.format(logFormat, map);

        if (level.ordinal() >= Level.WARN.ordinal()) {
            Console.error(t, logMsg);
        } else {
            Console.log(t, logMsg);
        }
    }
}
