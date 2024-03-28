package com.fuzzy.main.platform.sdk.context.impl;

import com.fuzzy.main.platform.sdk.context.Context;
import com.fuzzy.main.platform.sdk.context.source.Source;

public class ContextImpl implements Context {

    private final Source source;

    public ContextImpl(Source source) {
        this.source = source;
    }

    @Override
    public Source getSource() {
        return source;
    }

    @Override
    public String getTrace() {
        return "s" + hashCode();
    }
}

