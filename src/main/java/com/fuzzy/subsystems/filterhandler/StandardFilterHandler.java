package com.fuzzy.subsystems.filterhandler;

import com.fuzzy.database.domainobject.DomainObject;
import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.QueryTransaction;
import com.fuzzy.platform.querypool.ReadableResource;
import com.fuzzy.platform.querypool.iterator.IteratorEntity;
import com.fuzzy.subsystems.exception.GeneralExceptionBuilder;
import com.fuzzy.subsystems.graphql.input.GFilterOperation;
import com.fuzzy.subsystems.graphql.input.GStandardFilter;
import com.fuzzy.subsystems.grouping.NodeItemGrouping;

import java.util.HashSet;

public class StandardFilterHandler<Item extends DomainObject> {

    private ReadableResource<Item> itemReadableResource;
    private NodeItemGrouping grouping;

    public StandardFilterHandler(ReadableResource <Item> itemReadableResource, NodeItemGrouping grouping) {
        this.itemReadableResource = itemReadableResource;
        this.grouping = grouping;
    }

    public HashSet<Long> get(GStandardFilter filter, QueryTransaction transaction) throws PlatformException {
        if (filter == null) {
            return getAll(transaction);
        }
        switch (filter.getOperation()) {
            case EMPTY:
                return new HashSet<>();
            case NONEMPTY:
                return getAll(transaction);
            case INCLUDE:
            case EXCLUDE:
                if (filter.getNodes() != null || filter.getItems() != null) {
                    HashSet<Long> elements;
                    if (filter.getNodes() != null) {
                        elements = grouping.getChildItemsRecursively(filter.getNodes(), transaction);
                    } else {
                        elements = new HashSet<>();
                    }
                    if (filter.getItems() != null) {
                        elements.addAll(filter.getItems());
                    }
                    if (filter.getOperation() == GFilterOperation.INCLUDE) {
                        return elements;
                    } else {
                        HashSet<Long> result = getAll(transaction);
                        result.removeAll(elements);
                        return result;
                    }
                } else {
                    return getAll(transaction);
                }
            default:
                throw GeneralExceptionBuilder.buildUnexpectedBehaviourException("Undefined filter operation");
        }
    }

    private HashSet<Long> getAll(QueryTransaction transaction) throws PlatformException {
        HashSet<Long> elements = new HashSet <>();
        try (IteratorEntity<Item> ie = itemReadableResource.iterator(transaction)) {
            while (ie.hasNext()) {
                elements.add(ie.next().getId());
            }
        }
        return elements;
    }
}
