package com.sipa.boot.java8.common.log.dialect.logback;

import com.sipa.boot.java8.common.log.Log;
import com.sipa.boot.java8.common.log.LogFactory;
import org.slf4j.helpers.NOPLoggerFactory;

import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

/**
 * @author feizhihao
 * @date 2019/05/07
 */
public class LogBackLogFactory extends LogFactory {
    public LogBackLogFactory() {
        this(false);
    }

    public LogBackLogFactory(boolean failIfNop) {
        super("LogBack");
        checkLogExist(org.slf4j.LoggerFactory.class);
        if (!failIfNop) {
            return;
        }

        // SFL4J writes it error messages to System.err. Capture them so that the user does not see such a message on
        // the console during automatic detection.
        final StringBuilder buf = new StringBuilder();
        final PrintStream err = System.err;
        try {
            System.setErr(new PrintStream(new OutputStream() {
                @Override
                public void write(int b) {
                    buf.append((char)b);
                }
            }, true, "US-ASCII"));
        } catch (UnsupportedEncodingException e) {
            throw new Error(e);
        }

        try {
            if (org.slf4j.LoggerFactory.getILoggerFactory() instanceof NOPLoggerFactory) {
                throw new NoClassDefFoundError(buf.toString());
            } else {
                err.print(buf);
                err.flush();
            }
        } finally {
            System.setErr(err);
        }
    }

    @Override
    public Log createLog(String name) {
        return new LogBackLog(name);
    }

    @Override
    public Log createLog(Class<?> clazz) {
        return new LogBackLog(clazz);
    }
}
