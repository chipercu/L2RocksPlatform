package com.fuzzy.platform.querypool.iterator;

import com.fuzzy.database.domainobject.DomainObject;
import com.fuzzy.database.exception.DatabaseException;
import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.sdk.exception.GeneralExceptionBuilder;
import com.fuzzy.platform.sdk.iterator.Iterator;

public class IteratorEntity<E extends DomainObject> implements Iterator<E> {

    private final com.fuzzy.database.domainobject.iterator.IteratorEntity<E> ie;

    public IteratorEntity(com.fuzzy.database.domainobject.iterator.IteratorEntity<E> ie) {
        this.ie = ie;
    }

    @Override
    public boolean hasNext() {
        return ie.hasNext();
    }

    @Override
    public E next() throws PlatformException {
        try {
            return ie.next();
        } catch (DatabaseException e) {
            throw GeneralExceptionBuilder.buildDatabaseException(e);
        }
    }

    @Override
    public void close() throws PlatformException {
        try {
            ie.close();
        } catch (DatabaseException e) {
            throw GeneralExceptionBuilder.buildDatabaseException(e);
        }
    }
}
