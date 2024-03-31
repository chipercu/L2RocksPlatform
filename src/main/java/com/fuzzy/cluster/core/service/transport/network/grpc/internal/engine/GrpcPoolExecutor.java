package com.fuzzy.cluster.core.service.transport.network.grpc.internal.engine;

import com.fuzzy.cluster.core.service.transport.network.grpc.internal.utils.thread.DefaultThreadGroup;
import com.fuzzy.cluster.core.service.transport.network.grpc.internal.utils.thread.DefaultThreadPoolExecutor;

import java.util.concurrent.*;

public class GrpcPoolExecutor {

    private final ThreadPoolExecutor threadPool;

    public GrpcPoolExecutor(Thread.UncaughtExceptionHandler uncaughtExceptionHandler) {
        DefaultThreadGroup defaultThreadGroup = new DefaultThreadGroup("GrpcPoolExecutor", uncaughtExceptionHandler);

        this.threadPool = new DefaultThreadPoolExecutor(
                10,
                Integer.MAX_VALUE,
                0L,
                TimeUnit.MILLISECONDS,
                new SynchronousQueue<Runnable>(),
                defaultThreadGroup
        );
    }

    public void execute(Runnable command) {
        threadPool.execute(command);
    }
}
