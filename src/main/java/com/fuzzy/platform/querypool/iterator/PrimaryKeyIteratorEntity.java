package com.fuzzy.platform.querypool.iterator;

import com.fuzzy.database.domainobject.DomainObject;
import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.QueryTransaction;
import com.fuzzy.platform.querypool.ReadableResource;
import com.fuzzy.platform.sdk.iterator.Iterator;

import java.util.Set;

public class PrimaryKeyIteratorEntity<E extends DomainObject> implements Iterator<E> {

    private java.util.Iterator<Long> iterator;
    private ReadableResource<E> readableResource;
    private QueryTransaction transaction;

    public PrimaryKeyIteratorEntity(Set<Long> pKeys, ReadableResource<E> readableResource, QueryTransaction transaction) {
        this.iterator = pKeys.iterator();
        this.readableResource = readableResource;
        this.transaction = transaction;
    }

    @Override
    public boolean hasNext() throws PlatformException {
        return iterator.hasNext();
    }

    @Override
    public E next() throws PlatformException {
        Long pKey = iterator.next();
        return pKey != null ? readableResource.get(pKey, transaction) : null;
    }

    @Override
    public void close() throws PlatformException {

    }
}
