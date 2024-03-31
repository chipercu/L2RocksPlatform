package com.fuzzy.platform.querypool;

import com.fuzzy.database.domainobject.DomainObject;
import com.fuzzy.database.domainobject.DomainObjectEditable;
import com.fuzzy.database.domainobject.filter.Filter;
import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.EditableResource;
import com.fuzzy.platform.querypool.QueryTransaction;
import com.fuzzy.platform.querypool.iterator.IteratorEntity;

public interface RemovableResource<T extends DomainObject & DomainObjectEditable> extends EditableResource<T> {

    void remove(T obj, com.fuzzy.platform.querypool.QueryTransaction transaction) throws PlatformException;

    void clear(com.fuzzy.platform.querypool.QueryTransaction transaction) throws PlatformException;

    default boolean removeAll(Filter filter, QueryTransaction transaction) throws PlatformException {
        boolean result = false;
        try (IteratorEntity<T> i = findAll(filter, transaction)) {
            while (i.hasNext()) {
                remove(i.next(), transaction);
                result = true;
            }
        }
        return result;
    }
}
