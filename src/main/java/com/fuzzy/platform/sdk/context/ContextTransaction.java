package com.fuzzy.platform.sdk.context;

import com.fuzzy.platform.querypool.QueryTransaction;
import com.fuzzy.platform.sdk.context.Context;
import com.fuzzy.platform.sdk.context.source.Source;

public interface ContextTransaction<S extends Source> extends Context<S> {

    QueryTransaction getTransaction();

}
