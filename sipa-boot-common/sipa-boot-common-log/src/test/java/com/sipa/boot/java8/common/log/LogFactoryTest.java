package com.sipa.boot.java8.common.log;

import org.junit.Test;

import com.sipa.boot.java8.common.log.dialect.console.ConsoleLogFactory;
import com.sipa.boot.java8.common.log.dialect.jdk.JdkLogFactory;
import com.sipa.boot.java8.common.log.util.Console;

/**
 * @author feizhihao
 * @date 2019/05/07
 */
public class LogFactoryTest {
    @Test
    public void customLogTest() {
        Log log = LogFactory.get(LogFactoryTest.class);
        log.debug("This is [{}] log", "default");
        Console.log("----------------------------------------------------------------------");

        LogFactory.setCurrentLogFactory(new JdkLogFactory());
        log.info("This is [{}] log", "custom jdk logging");
        Console.log("----------------------------------------------------------------------");

        LogFactory.setCurrentLogFactory(new ConsoleLogFactory());
        log.info("This is [{}] log", "custom Console");
        Console.log("----------------------------------------------------------------------");
    }
}
