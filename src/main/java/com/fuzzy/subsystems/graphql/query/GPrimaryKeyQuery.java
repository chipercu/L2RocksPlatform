package com.fuzzy.subsystems.graphql.query;

import com.fuzzy.main.cluster.core.remote.struct.RemoteObject;
import com.fuzzy.main.platform.component.frontend.context.ContextTransactionRequest;
import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.QueryTransaction;
import com.fuzzy.main.platform.querypool.ReadableResource;
import com.fuzzy.main.platform.querypool.ResourceProvider;
import com.fuzzy.main.platform.sdk.graphql.customfield.graphqlquery.GraphQLQuery;
import com.fuzzy.main.rdao.database.domainobject.DomainObject;
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
