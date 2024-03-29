package com.fuzzy.subsystem.core.graphql.query;

import com.infomaximum.cluster.core.remote.struct.RemoteObject;
import com.infomaximum.platform.component.frontend.context.ContextTransactionRequest;
import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.ResourceProvider;
import com.infomaximum.platform.sdk.graphql.customfield.graphqlquery.GraphQLQuery;
import com.fuzzy.subsystem.core.config.CoreConfigGetter;
import com.fuzzy.subsystems.config.Config;

import java.io.Serializable;

public class GCoreObjectConfigQuery<T extends Serializable> extends GraphQLQuery<RemoteObject, T> {

    private final Config<T> configDescription;
    private CoreConfigGetter coreConfigGetter;

    public GCoreObjectConfigQuery(Config <T> configDescription) {
        this.configDescription = configDescription;
    }

    @Override
    public void prepare(ResourceProvider resources) {
        coreConfigGetter = new CoreConfigGetter(resources);
    }

    @Override
    public T execute(RemoteObject source, ContextTransactionRequest context) throws PlatformException {
        return coreConfigGetter.get(configDescription, context.getTransaction());
    }
}
