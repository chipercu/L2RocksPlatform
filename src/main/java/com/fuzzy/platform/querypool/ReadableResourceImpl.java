package com.fuzzy.platform.querypool;

import com.fuzzy.database.domainobject.DomainObject;
import com.fuzzy.database.domainobject.filter.EmptyFilter;
import com.fuzzy.database.domainobject.filter.Filter;
import com.fuzzy.database.exception.DatabaseException;
import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.QueryTransaction;
import com.fuzzy.platform.querypool.ReadableResource;
import com.fuzzy.platform.querypool.iterator.IteratorEntity;
import com.fuzzy.platform.sdk.exception.GeneralExceptionBuilder;

import java.util.Set;

class ReadableResourceImpl<T extends DomainObject> implements ReadableResource<T> {

    protected final Class<T> tClass;

    ReadableResourceImpl(Class<T> tClass) {
        this.tClass = tClass;
    }

    @Override
    public Class<T> getDomainClass() {
        return tClass;
    }

    @Override
    public T get(long id, com.fuzzy.platform.querypool.QueryTransaction transaction) throws PlatformException {
        return get(id, null, transaction);
    }

    @Override
    public T get(long id, Set<Integer> loadingFields, com.fuzzy.platform.querypool.QueryTransaction transaction) throws PlatformException {
        try {
            return transaction.getDBTransaction().get(tClass, id, loadingFields);
        } catch (DatabaseException e) {
            throw GeneralExceptionBuilder.buildDatabaseException(e);
        }
    }

    @Override
    public T find(final Filter filter, com.fuzzy.platform.querypool.QueryTransaction transaction) throws PlatformException {
        return find(filter, null, transaction);
    }

    @Override
    public T find(Filter filter, Set<Integer> loadingFields, com.fuzzy.platform.querypool.QueryTransaction transaction) throws PlatformException {
        try (com.fuzzy.database.domainobject.iterator.IteratorEntity<T> iter = transaction.getDBTransaction().find(tClass, filter, loadingFields)) {
            return iter.hasNext() ? iter.next() : null;
        } catch (DatabaseException e) {
            throw GeneralExceptionBuilder.buildDatabaseException(e);
        }
    }

    @Override
    public IteratorEntity<T> iterator(com.fuzzy.platform.querypool.QueryTransaction transaction) throws PlatformException {
        return iterator(null, transaction);
    }

    @Override
    public IteratorEntity<T> iterator(Set<Integer> loadingFields, com.fuzzy.platform.querypool.QueryTransaction transaction) throws PlatformException {
        try {
            return new IteratorEntity<>(transaction.getDBTransaction().find(tClass, EmptyFilter.INSTANCE, loadingFields));
        } catch (DatabaseException e) {
            throw GeneralExceptionBuilder.buildDatabaseException(e);
        }
    }

    @Override
    public IteratorEntity<T> findAll(final Filter filter, com.fuzzy.platform.querypool.QueryTransaction transaction) throws PlatformException {
        return findAll(filter, null, transaction);
    }

    @Override
    public IteratorEntity<T> findAll(Filter filter, Set<Integer> loadingFields, QueryTransaction transaction) throws PlatformException {
        try {
            return new IteratorEntity<>(transaction.getDBTransaction().find(tClass, filter, loadingFields));
        } catch (DatabaseException e) {
            throw GeneralExceptionBuilder.buildDatabaseException(e);
        }
    }
}
