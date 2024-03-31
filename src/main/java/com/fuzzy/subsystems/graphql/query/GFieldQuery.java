package com.fuzzy.subsystems.graphql.query;

import com.fuzzy.cluster.core.remote.struct.RemoteObject;
import com.fuzzy.database.domainobject.DomainObject;
import com.fuzzy.database.domainobject.filter.Filter;
import com.fuzzy.platform.component.frontend.context.ContextTransactionRequest;
import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.QueryTransaction;
import com.fuzzy.platform.querypool.ReadableResource;
import com.fuzzy.platform.querypool.ResourceProvider;
import com.fuzzy.platform.querypool.iterator.IteratorEntity;
import com.fuzzy.platform.sdk.graphql.customfield.graphqlquery.GraphQLQuery;
import com.fuzzy.subsystems.graphql.GDomainObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.function.Function;

public abstract class GFieldQuery <S extends RemoteObject, T extends DomainObject, Y extends GDomainObject<T>>
        extends GraphQLQuery<S, ArrayList<Y>> {

    private final Class<T> clazz;
    private final Function<T, Y> gDomainObjectConstructor;
    private ReadableResource<T> readableResource;

    public GFieldQuery(Class <T> clazz, Function<T, Y> gDomainObjectConstructor) {
        this.clazz = clazz;
        this.gDomainObjectConstructor = gDomainObjectConstructor;
    }

    @Override
    public void prepare(ResourceProvider resources) {
        readableResource = resources.getReadableResource(clazz);
    }

    @Override
    public ArrayList<Y> execute(S source, ContextTransactionRequest context) throws PlatformException {
        ArrayList<Y> result = new ArrayList<>();
        try (IteratorEntity<T> ie = readableResource.findAll(getFilter(source, context.getTransaction()), context.getTransaction())) {
            while (ie.hasNext()) {
                T object = ie.next();
                result.add(gDomainObjectConstructor.apply(object));
            }
        }
        result.sort(getComparator());
        return result;
    }

    protected Comparator<GDomainObject<T>> getComparator() {
        return Comparator.comparingLong(GDomainObject::getIdentifier);
    }

    protected abstract Filter getFilter(S source, QueryTransaction transaction) throws PlatformException;
}