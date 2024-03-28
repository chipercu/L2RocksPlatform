package com.fuzzy.main.cluster.core.service.transport.network.grpc.internal.utils.thread;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class DefaultThreadPoolExecutor extends ThreadPoolExecutor {

    private final DefaultThreadGroup defaultThreadGroup;

    public DefaultThreadPoolExecutor(
            int corePoolSize,
            int maximumPoolSize,
            long keepAliveTime,
            TimeUnit unit,
            BlockingQueue<Runnable> workQueue,
            DefaultThreadGroup defaultThreadGroup) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, new DefaultThreadFactory(defaultThreadGroup));
        this.defaultThreadGroup = defaultThreadGroup;
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        if (t != null) {
            defaultThreadGroup.uncaughtException(Thread.currentThread(), t);
        }
    }
}
