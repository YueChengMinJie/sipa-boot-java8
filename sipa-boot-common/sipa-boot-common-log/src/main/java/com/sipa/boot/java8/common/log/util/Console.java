package com.sipa.boot.java8.common.log.util;

import static java.lang.System.err;
import static java.lang.System.out;

import com.sipa.boot.java8.common.log.text.TextFormatter;

/**
 * @author feizhihao
 * @date 2019/05/07
 */
public class Console {
    public static void log(Object obj) {
        if (obj instanceof Throwable) {
            Throwable e = (Throwable)obj;
            log(e, e.getMessage());
        } else {
            log("{}", obj);
        }
    }

    public static void log(String template, Object... values) {
        log(null, template, values);
    }

    public static void log(Throwable t, String template, Object... values) {
        out.println(TextFormatter.format(template, values));
        if (null != t) {
            t.printStackTrace();
            out.flush();
        }
    }

    public static void error(Object obj) {
        if (obj instanceof Throwable) {
            Throwable e = (Throwable)obj;
            error(e, e.getMessage());
        } else {
            error("{}", obj);
        }
    }

    public static void error(String template, Object... values) {
        error(null, template, values);
    }

    public static void error(Throwable t, String template, Object... values) {
        err.println(TextFormatter.format(template, values));
        if (null != t) {
            t.printStackTrace(err);
            err.flush();
        }
    }
}
