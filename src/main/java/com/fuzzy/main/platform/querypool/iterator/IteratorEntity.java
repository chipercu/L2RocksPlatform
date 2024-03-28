package com.fuzzy.main.platform.querypool.iterator;

import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.sdk.exception.GeneralExceptionBuilder;
import com.fuzzy.main.platform.sdk.iterator.Iterator;
import com.fuzzy.main.rdao.database.domainobject.DomainObject;
import com.fuzzy.main.rdao.database.exception.DatabaseException;

public class IteratorEntity<E extends DomainObject> implements Iterator<E> {

    private final com.fuzzy.main.rdao.database.domainobject.iterator.IteratorEntity<E> ie;

    public IteratorEntity(com.fuzzy.main.rdao.database.domainobject.iterator.IteratorEntity<E> ie) {
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
