package com.fuzzy.platform.sdk.struct.querypool;

import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.ResourceProvider;
import com.fuzzy.platform.sdk.context.ContextTransaction;

public abstract class QuerySystem<T> {

    public abstract void prepare(ResourceProvider resources) throws PlatformException;

    public abstract T execute(ContextTransaction context) throws PlatformException;
}
