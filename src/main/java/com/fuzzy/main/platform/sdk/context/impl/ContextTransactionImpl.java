package com.fuzzy.main.platform.sdk.context.impl;

import com.fuzzy.main.platform.querypool.QueryTransaction;
import com.fuzzy.main.platform.sdk.context.ContextTransaction;
import com.fuzzy.main.platform.sdk.context.ContextTransactionInternal;
import com.fuzzy.main.platform.sdk.context.source.SourceSystem;

public class ContextTransactionImpl implements ContextTransaction, ContextTransactionInternal {

    private final SourceSystem source;
    private QueryTransaction transaction;

    public ContextTransactionImpl(SourceSystem source, QueryTransaction transaction) {
        this.source = source;
        this.transaction = transaction;
    }

    @Override
    public SourceSystem getSource() {
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
    public String getTrace() {
        return "s" + hashCode();
    }
}
