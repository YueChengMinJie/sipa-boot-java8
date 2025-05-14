package com.sipa.boot.java8.common.log.dialect.jdk;

import java.io.Closeable;
import java.io.InputStream;
import java.util.logging.LogManager;

import com.sipa.boot.java8.common.log.Log;
import com.sipa.boot.java8.common.log.LogFactory;
import com.sipa.boot.java8.common.log.util.Console;

/**
 * @author feizhihao
 * @date 2019/05/07
 */
public class JdkLogFactory extends LogFactory {
    public JdkLogFactory() {
        super("JDK Logging");
        readConfig();
    }

    private void readConfig() {
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("logging.properties");
        if (null == in) {
            Console
                .error("[WARN] Can not find [logging.properties], use [%JRE_HOME%/lib/logging.properties] as default!");
            return;
        }

        try {
            LogManager.getLogManager().readConfiguration(in);
        } catch (Exception e) {
            Console.error(e, "Read [logging.properties] from classpath error!");

            try {
                LogManager.getLogManager().readConfiguration();
            } catch (Exception e1) {
                Console.error(e1, "Read [logging.properties] from [%JRE_HOME%/lib/logging.properties] error!");
            }
        } finally {
            close(in);
        }
    }

    private void close(Closeable closeable) {
        if (null != closeable) {
            try {
                closeable.close();
            } catch (Exception e) {
                Console.log("close silently");
            }
        }
    }

    @Override
    public Log createLog(String name) {
        return new JdkLog(name);
    }

    @Override
    public Log createLog(Class<?> clazz) {
        return new JdkLog(clazz);
    }
}
