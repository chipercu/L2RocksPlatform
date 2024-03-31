package com.fuzzy.platform.querypool;

import com.fuzzy.database.domainobject.DomainObject;
import com.fuzzy.database.domainobject.filter.EmptyFilter;
import com.fuzzy.database.domainobject.filter.Filter;
import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.QueryTransaction;
import com.fuzzy.platform.querypool.iterator.IteratorEntity;
import com.fuzzy.platform.sdk.function.Consumer;

import java.util.ArrayList;
import java.util.Set;

public interface ReadableResource<T extends DomainObject>  {

    Class<T> getDomainClass();

    T get(long id, com.fuzzy.platform.querypool.QueryTransaction transaction) throws PlatformException;

    T get(long id, Set<Integer> loadingFields, com.fuzzy.platform.querypool.QueryTransaction transaction) throws PlatformException;

    T find(final Filter filter, com.fuzzy.platform.querypool.QueryTransaction transaction) throws PlatformException;

    T find(final Filter filter, Set<Integer> loadingFields, com.fuzzy.platform.querypool.QueryTransaction transaction) throws PlatformException;

    IteratorEntity<T> iterator(com.fuzzy.platform.querypool.QueryTransaction transaction) throws PlatformException;

    IteratorEntity<T> iterator(Set<Integer> loadingFields, com.fuzzy.platform.querypool.QueryTransaction transaction) throws PlatformException;

    IteratorEntity<T> findAll(final Filter filter, com.fuzzy.platform.querypool.QueryTransaction transaction) throws PlatformException;

    IteratorEntity<T> findAll(final Filter filter, Set<Integer> loadingFields, com.fuzzy.platform.querypool.QueryTransaction transaction) throws PlatformException;

    default void forEach(final Filter filter, Consumer<T> action, com.fuzzy.platform.querypool.QueryTransaction transaction) throws PlatformException {
        try(IteratorEntity<T> it = findAll(filter, transaction)) {
            while (it.hasNext()) {
                action.accept(it.next());
            }
        }
    }

    default void forEach(Consumer<T> action, com.fuzzy.platform.querypool.QueryTransaction transaction) throws PlatformException {
        forEach(EmptyFilter.INSTANCE, action, transaction);
    }

    default ArrayList<T> getAll(final Filter filter, com.fuzzy.platform.querypool.QueryTransaction transaction) throws PlatformException {
        ArrayList<T> result = new ArrayList<>();
        forEach(filter, result::add, transaction);
        return result;
    }

    default ArrayList<Long> getIds(final Filter filter, QueryTransaction transaction) throws PlatformException {
        ArrayList<Long> result = new ArrayList<>();
        forEach(filter, o -> result.add(o.getId()), transaction);
        return result;
    }
}
