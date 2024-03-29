package com.fuzzy.subsystems.graphql.query;

import com.infomaximum.cluster.core.remote.struct.RemoteObject;
import com.infomaximum.database.domainobject.DomainObject;
import com.infomaximum.database.domainobject.filter.Filter;
import com.infomaximum.platform.component.frontend.context.ContextTransactionRequest;
import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.QueryTransaction;
import com.infomaximum.platform.querypool.ReadableResource;
import com.infomaximum.platform.querypool.ResourceProvider;
import com.infomaximum.platform.querypool.iterator.IteratorEntity;
import com.infomaximum.platform.sdk.graphql.customfield.graphqlquery.GraphQLQuery;
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