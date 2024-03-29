package com.fuzzy.subsystems.graphql.query;

import com.infomaximum.cluster.core.remote.struct.RemoteObject;
import com.infomaximum.database.domainobject.DomainObject;
import com.infomaximum.platform.component.frontend.context.ContextTransactionRequest;
import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.QueryTransaction;
import com.infomaximum.platform.querypool.ReadableResource;
import com.infomaximum.platform.querypool.ResourceProvider;
import com.infomaximum.platform.sdk.graphql.customfield.graphqlquery.GraphQLQuery;
import com.fuzzy.subsystems.graphql.GDomainObject;
import com.fuzzy.subsystems.grouping.GroupingEnumerator;
import com.fuzzy.subsystems.grouping.NodeGrouping;

import java.util.ArrayList;
import java.util.Collections;
import java.util.function.Function;

public abstract class GParentsQuery <S extends RemoteObject, T extends DomainObject, Y extends GDomainObject<T>>
        extends GraphQLQuery<S, ArrayList<Y>> {

    private Class<T> clazz;
    private Function<T, Y> gDomainObjectConstructor;
    private ReadableResource<T> readableResource;
    private NodeGrouping lightGrouping;

    public GParentsQuery(Class <T> clazz, Function<T, Y> gDomainObjectConstructor) {
        this.clazz = clazz;
        this.gDomainObjectConstructor = gDomainObjectConstructor;
    }

    @Override
    public void prepare(ResourceProvider resources) {
        readableResource = resources.getReadableResource(clazz);
        lightGrouping = new NodeGrouping(getGroupingEnumerator(resources));
    }

    @Override
    public ArrayList<Y> execute(S source, ContextTransactionRequest context) throws PlatformException {
        ArrayList<Y> result = new ArrayList<>();
        Long parentId = getParentId(source, context.getTransaction());
        if (parentId != null) {
            result.add(gDomainObjectConstructor.apply(readableResource.get(parentId, context.getTransaction())));
            lightGrouping.forEachParentRecursively(parentId, context.getTransaction(), currentParentId -> {
                result.add(gDomainObjectConstructor.apply(readableResource.get(currentParentId, context.getTransaction())));
                return true;
            });
            Collections.reverse(result);
        }
        return result;
    }

    protected abstract GroupingEnumerator getGroupingEnumerator(ResourceProvider resources);

    protected abstract Long getParentId(S source, QueryTransaction transaction) throws PlatformException;
}
