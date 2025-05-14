package com.sipa.boot.java8.common.log;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import com.sipa.boot.java8.common.log.dialect.console.ConsoleLogFactory;
import com.sipa.boot.java8.common.log.dialect.jdk.JdkLogFactory;
import com.sipa.boot.java8.common.log.dialect.logback.LogBackLogFactory;

/**
 * @author feizhihao
 * @date 2019/05/07
 */
@SuppressWarnings("PMD.AbstractClassShouldStartWithAbstractNamingRule")
public abstract class LogFactory {
    private String logFrameworkName;

    private Map<Object, Log> logCache;

    public LogFactory(String logFrameworkName) {
        this.logFrameworkName = logFrameworkName;
        logCache = new ConcurrentHashMap<>();
    }

    public Log getLog(String name) {
        Log log = logCache.get(name);
        if (null == log) {
            log = createLog(name);
            logCache.put(name, log);
        }
        return log;
    }

    public Log getLog(Class<?> clazz) {
        Log log = logCache.get(clazz);
        if (null == log) {
            log = createLog(clazz);
            logCache.put(clazz, log);
        }
        return log;
    }

    /**
     * create log instance
     *
     * @param name
     *            class name
     * @return log instance
     */
    public abstract Log createLog(String name);

    /**
     * create log instance
     *
     * @param clazz
     *            class
     * @return log instance
     */
    public abstract Log createLog(Class<?> clazz);

    protected void checkLogExist(Object logClassName) {
        // no op
    }

    // ------------------------------------------------------------------------- Static start

    private static volatile LogFactory currentLogFactory;

    private static final Object LOCK = new Object();

    public static LogFactory getCurrentLogFactory() {
        if (null == currentLogFactory) {
            synchronized (LOCK) {
                if (null == currentLogFactory) {
                    currentLogFactory = detectLogFactory();
                }
            }
        }
        return currentLogFactory;
    }

    public static LogFactory setCurrentLogFactory(Class<? extends LogFactory> logFactoryClass) {
        try {
            return setCurrentLogFactory(logFactoryClass.newInstance());
        } catch (Exception e) {
            throw new IllegalArgumentException("Can not instance LogFactory class!", e);
        }
    }

    public static LogFactory setCurrentLogFactory(LogFactory logFactory) {
        logFactory.getLog(LogFactory.class).debug("Custom Use [{}] Logger.", logFactory.logFrameworkName);
        currentLogFactory = logFactory;
        return currentLogFactory;
    }

    public static Log get(String name) {
        return getCurrentLogFactory().getLog(name);
    }

    public static Log get(Class<?> clazz) {
        return getCurrentLogFactory().getLog(clazz);
    }

    /**
     * LogBack -> JDK -> Console
     */
    @SuppressWarnings("PMD.EmptyCatchBlock")
    private static LogFactory detectLogFactory() {
        // set ConsoleLogFactory as base one
        LogFactory logFactory = new ConsoleLogFactory();

        List<Supplier<LogFactory>> logFactorySupplierList =
            Arrays.asList(() -> new LogBackLogFactory(true), JdkLogFactory::new);
        for (Supplier<LogFactory> logFactorySupplier : logFactorySupplierList) {
            try {
                logFactory = logFactorySupplier.get();
                break; // get one by order
            } catch (NoClassDefFoundError e) {
                // just try next one by order
            }
        }

        logFactory.getLog(LogFactory.class).info("Use [{}] logger as default.", logFactory.logFrameworkName);

        return logFactory;
    }
    // ------------------------------------------------------------------------- Static end
}
