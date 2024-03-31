package com.fuzzy.cluster.core.service.transport.network.grpc.internal.utils.thread;

public class DefaultThreadGroup extends ThreadGroup {

    private final Thread.UncaughtExceptionHandler uncaughtExceptionHandler;

    public DefaultThreadGroup(String name, Thread.UncaughtExceptionHandler uncaughtExceptionHandler) {
        super(name);

        this.uncaughtExceptionHandler = uncaughtExceptionHandler;
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        uncaughtExceptionHandler.uncaughtException(t, e);
    }
}
