package com.fuzzy.cluster.core.service.transport.network.grpc.internal.utils.thread;

import com.fuzzy.cluster.core.service.transport.network.grpc.internal.utils.thread.DefaultThreadFactory;
import com.fuzzy.cluster.core.service.transport.network.grpc.internal.utils.thread.DefaultThreadGroup;

import java.util.concurrent.*;

public class DefaultThreadPoolExecutor extends ThreadPoolExecutor {

    private final com.fuzzy.cluster.core.service.transport.network.grpc.internal.utils.thread.DefaultThreadGroup defaultThreadGroup;

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
