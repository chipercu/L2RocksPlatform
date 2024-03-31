package com.fuzzy.platform.utils;

import com.fuzzy.platform.utils.DefaultThreadFactory;
import com.fuzzy.platform.utils.DefaultThreadGroup;

import java.util.concurrent.*;

public class DefaultThreadPoolExecutor extends ThreadPoolExecutor {

    private final com.fuzzy.platform.utils.DefaultThreadGroup defaultThreadGroup;

    public DefaultThreadPoolExecutor(
            int corePoolSize,
            int maximumPoolSize,
            long keepAliveTime,
            TimeUnit unit,
            BlockingQueue<Runnable> workQueue,
            String threadFactoryName,
            Thread.UncaughtExceptionHandler uncaughtExceptionHandler) {
        this(
                corePoolSize,
                maximumPoolSize,
                keepAliveTime,
                unit,
                workQueue,
                new com.fuzzy.platform.utils.DefaultThreadGroup(threadFactoryName, uncaughtExceptionHandler)
        );
    }

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

        if (t == null) {
            try {
                Future<?> future = (Future<?>) r;
                if (future.isDone()) {
                    future.get();
                }
            } catch (ExecutionException e) {
                t = e.getCause();
            } catch (Throwable e) {
                t = e;
            }
        }

        if (t != null) {
            defaultThreadGroup.uncaughtException(Thread.currentThread(), t);
        }
    }
}
