package com.sipa.boot.java8.common.hystrix.executor;

import java.util.concurrent.*;

import com.alibaba.ttl.TtlRunnable;

public class ThreadLocalThreadPoolExecutor extends ThreadPoolExecutor {
    private static final RejectedExecutionHandler defaultHandler = new AbortPolicy();

    public ThreadLocalThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
        BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    public ThreadLocalThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
        BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, defaultHandler);
    }

    @Override
    public void execute(Runnable command) {
        super.execute(TtlRunnable.get(command));
    }
}
