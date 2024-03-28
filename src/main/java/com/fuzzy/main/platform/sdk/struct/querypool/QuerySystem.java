package com.fuzzy.main.platform.sdk.struct.querypool;

import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.ResourceProvider;
import com.fuzzy.main.platform.sdk.context.ContextTransaction;

public abstract class QuerySystem<T> {

    public abstract void prepare(ResourceProvider resources) throws PlatformException;

    public abstract T execute(ContextTransaction context) throws PlatformException;
}
