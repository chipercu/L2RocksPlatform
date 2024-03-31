package com.fuzzy.platform.utils;

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
