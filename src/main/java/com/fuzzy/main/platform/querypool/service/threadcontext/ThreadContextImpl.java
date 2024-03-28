package com.fuzzy.main.platform.querypool.service.threadcontext;

import com.fuzzy.main.platform.sdk.context.Context;
import com.fuzzy.main.platform.utils.DefaultThreadGroup;


public class ThreadContextImpl implements ThreadContext {

    private final DefaultThreadGroup defaultThreadGroup;
    private final ThreadLocal<Context> threadContexts;

    public ThreadContextImpl(DefaultThreadGroup defaultThreadGroup) {
        this.defaultThreadGroup = defaultThreadGroup;
        threadContexts = new ThreadLocal<Context>();
    }

    public void setContext(Context context) {
        threadContexts.set(context);
    }

    public void clearContext() {
        threadContexts.remove();
    }

    @Override
    public Context getContext() {
        Thread thread = Thread.currentThread();
        if (thread.getThreadGroup() != defaultThreadGroup) {
            return null;
        }
        return threadContexts.get();
    }
}
