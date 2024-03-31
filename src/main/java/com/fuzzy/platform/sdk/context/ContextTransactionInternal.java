package com.fuzzy.platform.sdk.context;

import com.fuzzy.platform.querypool.QueryTransaction;

public interface ContextTransactionInternal {

    void setTransaction(QueryTransaction transaction);
}
