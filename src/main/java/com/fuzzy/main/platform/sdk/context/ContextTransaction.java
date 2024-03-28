package com.fuzzy.main.platform.sdk.context;

import com.fuzzy.main.platform.querypool.QueryTransaction;
import com.fuzzy.main.platform.sdk.context.source.Source;

public interface ContextTransaction<S extends Source> extends Context<S> {

    QueryTransaction getTransaction();

}
