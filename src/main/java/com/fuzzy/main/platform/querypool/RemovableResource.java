package com.fuzzy.main.platform.querypool;

import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.iterator.IteratorEntity;
import com.fuzzy.main.rdao.database.domainobject.DomainObject;
import com.fuzzy.main.rdao.database.domainobject.DomainObjectEditable;
import com.fuzzy.main.rdao.database.domainobject.filter.Filter;

public interface RemovableResource<T extends DomainObject & DomainObjectEditable> extends EditableResource<T> {

    void remove(T obj, QueryTransaction transaction) throws PlatformException;

    void clear(QueryTransaction transaction) throws PlatformException;

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
