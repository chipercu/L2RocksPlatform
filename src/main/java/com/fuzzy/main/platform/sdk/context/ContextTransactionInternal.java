package com.fuzzy.main.platform.sdk.context;

import com.fuzzy.main.platform.querypool.QueryTransaction;

public interface ContextTransactionInternal {

    void setTransaction(QueryTransaction transaction);
}
