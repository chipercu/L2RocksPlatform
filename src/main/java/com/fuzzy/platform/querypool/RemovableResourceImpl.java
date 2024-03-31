package com.fuzzy.platform.querypool;

import com.fuzzy.database.domainobject.DomainObject;
import com.fuzzy.database.domainobject.DomainObjectEditable;
import com.fuzzy.database.exception.DatabaseException;
import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.EditableResourceImpl;
import com.fuzzy.platform.querypool.QueryTransaction;
import com.fuzzy.platform.querypool.RemovableResource;
import com.fuzzy.platform.sdk.exception.GeneralExceptionBuilder;

class RemovableResourceImpl<T extends DomainObject & DomainObjectEditable> extends com.fuzzy.platform.querypool.EditableResourceImpl<T> implements RemovableResource<T> {

	RemovableResourceImpl(Class<T> tClass) {
        super(tClass);
    }

    @Override
    public void remove(T obj, com.fuzzy.platform.querypool.QueryTransaction transaction) throws PlatformException {
        try {
            transaction.getDBTransaction().remove(obj);
        } catch (DatabaseException e) {
            throw GeneralExceptionBuilder.buildDatabaseException(e);
        }
    }

    @Override
    public void clear(QueryTransaction transaction) throws PlatformException {
        try {
            transaction.getDBTransaction().removeAll(getDomainClass());
        } catch (DatabaseException e) {
            throw GeneralExceptionBuilder.buildDatabaseException(e);
        }
    }
}
