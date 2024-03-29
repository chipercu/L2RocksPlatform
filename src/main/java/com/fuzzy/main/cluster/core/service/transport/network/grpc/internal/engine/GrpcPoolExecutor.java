package com.fuzzy.main.cluster.core.service.transport.network.grpc.internal.engine;

import com.infomaximum.cluster.core.service.transport.network.grpc.internal.utils.thread.DefaultThreadGroup;
import com.infomaximum.cluster.core.service.transport.network.grpc.internal.utils.thread.DefaultThreadPoolExecutor;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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
