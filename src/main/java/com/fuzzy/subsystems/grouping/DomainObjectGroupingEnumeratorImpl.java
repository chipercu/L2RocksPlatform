package com.fuzzy.subsystems.grouping;

import com.fuzzy.database.domainobject.DomainObject;
import com.fuzzy.database.domainobject.filter.HashFilter;
import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.QueryTransaction;
import com.fuzzy.platform.querypool.ReadableResource;
import com.fuzzy.platform.querypool.ResourceProvider;
import com.fuzzy.platform.querypool.iterator.IteratorEntity;
import com.fuzzy.subsystems.function.Function;

public class DomainObjectGroupingEnumeratorImpl<T extends DomainObject> implements GroupingEnumerator {

    private final ReadableResource<T> readableResource;
    private final int parentFieldNumber;

    public DomainObjectGroupingEnumeratorImpl(ResourceProvider resources, Class <T> clazz, int parentFieldNumber) {
        this.readableResource = resources.getReadableResource(clazz);
        this.parentFieldNumber = parentFieldNumber;
    }

    @Override
    public boolean forEachParent(Long id, QueryTransaction transaction, Function<Long, Boolean> function)
            throws PlatformException {
        T object = readableResource.get(id, transaction);
        if (object != null) {
            Long parentId = object.get(parentFieldNumber);
            if (parentId != null) {
                return function.apply(parentId);
            }
        }
        return true;
    }

    @Override
    public boolean forEachChild(Long id, QueryTransaction transaction, Function<Long, Boolean> function)
            throws PlatformException {
        try (IteratorEntity<T> ie = readableResource.findAll(new HashFilter(parentFieldNumber, id), transaction)) {
            while (ie.hasNext()) {
                T object = ie.next();
                if (!function.apply(object.getId())) {
                    return false;
                }
            }
        }
        return true;
    }
}
