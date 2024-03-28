package com.fuzzy.main.platform.component.frontend.context.impl;

import com.fuzzy.main.cluster.graphql.struct.ContextRequest;
import com.fuzzy.main.cluster.graphql.struct.GRequest;
import com.fuzzy.main.platform.component.frontend.context.source.SourceGRequest;

public class ContextRequestImpl implements ContextRequest {

    private final SourceGRequest source;

    public ContextRequestImpl(SourceGRequest source) {
        this.source = source;
    }

    @Override
    public GRequest getRequest() {
        return source.getRequest();
    }
}