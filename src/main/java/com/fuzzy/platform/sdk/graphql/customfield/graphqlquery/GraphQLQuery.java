package com.fuzzy.platform.sdk.graphql.customfield.graphqlquery;

import com.fuzzy.cluster.core.remote.struct.RemoteObject;
import com.fuzzy.platform.component.frontend.context.ContextTransactionRequest;
import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.ResourceProvider;

import java.io.Serializable;

public abstract class GraphQLQuery<S extends RemoteObject, T extends Serializable> {

    public abstract void prepare(ResourceProvider resources);

    public abstract T execute(
            S source,
            ContextTransactionRequest context
    ) throws PlatformException;

}
