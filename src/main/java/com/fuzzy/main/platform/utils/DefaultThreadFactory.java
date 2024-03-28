package com.fuzzy.main.platform.utils;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class DefaultThreadFactory implements ThreadFactory {

    private final ThreadGroup group;
    private final AtomicInteger threadNumber = new AtomicInteger(1);

    public DefaultThreadFactory(String factoryName, Thread.UncaughtExceptionHandler uncaughtExceptionHandler) {
        this(new DefaultThreadGroup(factoryName, uncaughtExceptionHandler));
    }

    public DefaultThreadFactory(ThreadGroup group) {
        this.group = group;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(group, r,
                group.getName() + "-t-" + threadNumber.getAndIncrement(),
                0);
        if (t.isDaemon()) {
            t.setDaemon(false);
        }
        if (t.getPriority() != Thread.NORM_PRIORITY) {
            t.setPriority(Thread.NORM_PRIORITY);
        }
        return t;
    }

}
