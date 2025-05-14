package com.sipa.boot.java8.common.log.dialect.console;

import com.sipa.boot.java8.common.log.Log;
import com.sipa.boot.java8.common.log.LogFactory;

/**
 * @author feizhihao
 * @date 2019/05/07
 */
public class ConsoleLogFactory extends LogFactory {
    public ConsoleLogFactory() {
        super("Console Logging");
    }

    public ConsoleLogFactory(String name) {
        super(name);
    }

    @Override
    public Log createLog(String name) {
        return new ConsoleLog(name);
    }

    @Override
    public Log createLog(Class<?> clazz) {
        return new ConsoleLog(clazz);
    }
}
