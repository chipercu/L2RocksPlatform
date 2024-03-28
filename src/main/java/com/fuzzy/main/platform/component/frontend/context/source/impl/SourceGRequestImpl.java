package com.fuzzy.main.platform.component.frontend.context.source.impl;

import com.fuzzy.main.cluster.graphql.struct.GRequest;
import com.fuzzy.main.platform.component.frontend.context.source.SourceGRequest;

public class SourceGRequestImpl implements SourceGRequest {

    private final GRequest request;

    public SourceGRequestImpl(GRequest request) {
        this.request = request;
    }

    @Override
    public GRequest getRequest() {
        return request;
    }

}
