package com.sipa.boot.java8.common.archs.threadpool.factory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

/**
 * @author sunyukun
 * @since 2019/8/6 14:57
 */
public class ThreadPoolFactory {
    private static final Map<String, ExecutorService> THREAD_POOLS = new HashMap<>();

    public static final String THREAD_POOL_TYPE = "sipa-boot";

    private static final String THREAD_POOL_NAME_FORMAT = "sipa-boot-thread-pool-%d";

    private static class ThreadPoolFactoryHolder {
        private static final ThreadFactory NAMED_THREAD_FACTORY =
            new ThreadFactoryBuilder().setNameFormat(THREAD_POOL_NAME_FORMAT).build();

        private static final ExecutorService SIPA_BOOT_THREAD_POOL =
            new ThreadPoolExecutor(8, 20, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(200), NAMED_THREAD_FACTORY);

        private static final ThreadPoolFactory INSTANCE = new ThreadPoolFactory();
    }

    private ThreadPoolFactory() {
        THREAD_POOLS.put(THREAD_POOL_TYPE, ThreadPoolFactoryHolder.SIPA_BOOT_THREAD_POOL);
    }

    public static ThreadPoolFactory getInstance() {
        return ThreadPoolFactoryHolder.INSTANCE;
    }

    /**
     * Get thread pool by name.
     *
     * @param threadPoolName
     *            thread pool name.
     * @return Executor Service.
     */
    public ExecutorService getThreadPool(String threadPoolName) {
        return THREAD_POOLS.get(threadPoolName);
    }
}
