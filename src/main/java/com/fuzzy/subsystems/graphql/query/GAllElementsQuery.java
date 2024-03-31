package com.fuzzy.subsystems.graphql.query;

import com.fuzzy.cluster.core.remote.struct.RemoteObject;
import com.fuzzy.database.domainobject.DomainObject;
import com.fuzzy.platform.component.frontend.context.ContextTransactionRequest;
import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.ReadableResource;
import com.fuzzy.platform.querypool.ResourceProvider;
import com.fuzzy.platform.querypool.iterator.IteratorEntity;
import com.fuzzy.platform.sdk.graphql.customfield.graphqlquery.GraphQLQuery;
import com.fuzzy.subsystems.graphql.GDomainObject;

import java.util.ArrayList;
import java.util.function.Function;

public class GAllElementsQuery<T extends DomainObject, Y extends GDomainObject<T>>
        extends GraphQLQuery<RemoteObject, ArrayList<Y>> {

    private final Class<T> domainObjectClass;
    final Function<T, Y> gDomainObjectConstructor;
    private ReadableResource<T> readableResource;

    public GAllElementsQuery(Class <T> domainObjectClass, Function<T, Y> gDomainObjectConstructor) {
        this.domainObjectClass = domainObjectClass;
        this.gDomainObjectConstructor = gDomainObjectConstructor;
    }

    @Override
    public void prepare(ResourceProvider resources) {
        readableResource = resources.getReadableResource(domainObjectClass);
    }

    @Override
    public ArrayList<Y> execute(RemoteObject source, ContextTransactionRequest context) throws PlatformException {
        ArrayList<Y> result = new ArrayList<>();
        try (IteratorEntity<T> ie = readableResource.iterator(context.getTransaction())) {
            while (ie.hasNext()) {
                Y object = gDomainObjectConstructor.apply(ie.next());
                result.add(object);
            }
        }
        return result;
    }
}