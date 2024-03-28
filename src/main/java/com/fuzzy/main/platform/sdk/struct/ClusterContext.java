package com.fuzzy.main.platform.sdk.struct;

import com.fuzzy.main.platform.Platform;

public class ClusterContext {

    public final Platform platform;
    private final Object context;

    public ClusterContext(Platform platform, Object context) {
        this.platform = platform;
        this.context = context;
    }

    public <T> T getContext() {
        return (T) context;
    }
}
