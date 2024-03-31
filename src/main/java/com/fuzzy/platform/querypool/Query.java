package com.fuzzy.platform.querypool;

import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.QueryPool;
import com.fuzzy.platform.querypool.QueryTransaction;
import com.fuzzy.platform.querypool.ResourceProvider;

public abstract class Query<T> {

    public abstract void prepare(ResourceProvider resources) throws PlatformException;

    public QueryPool.Priority getPriority(){
        return QueryPool.Priority.HIGH;
    }

    public String getMaintenanceMarker(){
        return null;
    }

    public abstract T execute(QueryTransaction transaction) throws PlatformException;
}
