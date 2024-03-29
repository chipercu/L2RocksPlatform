package com.fuzzy.subsystems.graphql.query;

import com.infomaximum.cluster.core.remote.struct.RemoteObject;
import com.infomaximum.database.domainobject.DomainObject;
import com.infomaximum.platform.component.frontend.context.ContextTransactionRequest;
import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.QueryTransaction;
import com.infomaximum.platform.querypool.ReadableResource;
import com.infomaximum.platform.querypool.ResourceProvider;
import com.infomaximum.platform.sdk.graphql.customfield.graphqlquery.GraphQLQuery;
import com.fuzzy.subsystems.function.Function;
import com.fuzzy.subsystems.graphql.GDomainObject;

public abstract class GPrimaryKeyQuery<S extends RemoteObject, T extends DomainObject, Y extends GDomainObject<T>>
        extends GraphQLQuery<S, Y> {

    private Class<T> clazz;
    private Function<T, Y> gDomainObjectConstructor;
    private ReadableResource<T> readableResource;

    public GPrimaryKeyQuery(Class <T> clazz, Function<T, Y> gDomainObjectConstructor) {
        this.clazz = clazz;
        this.gDomainObjectConstructor = gDomainObjectConstructor;
    }

    @Override
    public void prepare(ResourceProvider resources) {
        readableResource = resources.getReadableResource(clazz);
    }

    @Override
    public Y execute(S source, ContextTransactionRequest context) throws PlatformException {
        Long primaryKey = getIdentificator(source, context.getTransaction());
        if (primaryKey != null) {
            T domainObject = readableResource.get(primaryKey, context.getTransaction());
            if (domainObject != null) {
                return gDomainObjectConstructor.apply(domainObject);
            }
        }
        return null;
    }

    protected abstract Long getIdentificator(S source, QueryTransaction transaction) throws PlatformException;
}
