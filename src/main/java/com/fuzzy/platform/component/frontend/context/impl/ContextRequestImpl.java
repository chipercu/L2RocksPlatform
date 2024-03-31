package com.fuzzy.platform.component.frontend.context.impl;

import com.fuzzy.cluster.graphql.struct.ContextRequest;
import com.fuzzy.cluster.graphql.struct.GRequest;
import com.fuzzy.platform.component.frontend.context.source.SourceGRequest;

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