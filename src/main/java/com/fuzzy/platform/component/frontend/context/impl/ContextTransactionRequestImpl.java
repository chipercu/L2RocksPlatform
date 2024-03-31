package com.fuzzy.platform.component.frontend.context.impl;

import com.fuzzy.cluster.graphql.struct.ContextRequest;
import com.fuzzy.cluster.graphql.struct.GRequest;
import com.fuzzy.platform.component.frontend.context.ContextTransactionRequest;
import com.fuzzy.platform.component.frontend.context.source.SourceGRequestAuth;
import com.fuzzy.platform.component.frontend.utils.GRequestUtils;
import com.fuzzy.platform.querypool.QueryTransaction;
import com.fuzzy.platform.sdk.context.ContextTransactionInternal;

public class ContextTransactionRequestImpl implements ContextTransactionRequest, ContextRequest, ContextTransactionInternal {

    private final SourceGRequestAuth source;
    private QueryTransaction transaction;

    public ContextTransactionRequestImpl(SourceGRequestAuth source) {
        this(source, null);
    }

    public ContextTransactionRequestImpl(SourceGRequestAuth source, QueryTransaction transaction) {
        this.source = source;
        this.transaction = transaction;
    }

    @Override
    public SourceGRequestAuth getSource() {
        return source;
    }

    @Override
    public QueryTransaction getTransaction() {
        return transaction;
    }

    @Override
    public void setTransaction(QueryTransaction transaction) {
        this.transaction = transaction;
    }

    @Override
    public GRequest getRequest() {
        return getSource().getRequest();
    }

    @Override
    public String getTrace() {
        return GRequestUtils.getTraceRequest(source.getRequest());
    }
}
