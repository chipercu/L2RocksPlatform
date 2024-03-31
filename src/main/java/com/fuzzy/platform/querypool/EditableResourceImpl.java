package com.fuzzy.platform.querypool;

import com.fuzzy.database.domainobject.DomainObject;
import com.fuzzy.database.domainobject.DomainObjectEditable;
import com.fuzzy.database.exception.DatabaseException;
import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.EditableResource;
import com.fuzzy.platform.querypool.QueryTransaction;
import com.fuzzy.platform.querypool.ReadableResourceImpl;
import com.fuzzy.platform.sdk.exception.GeneralExceptionBuilder;

class EditableResourceImpl<T extends DomainObject & DomainObjectEditable> extends ReadableResourceImpl<T> implements EditableResource<T> {

    EditableResourceImpl(Class<T> tClass) {
        super(tClass);
    }

    @Override
    public T create(com.fuzzy.platform.querypool.QueryTransaction transaction) throws PlatformException {
        try {
            return transaction.getDBTransaction().create(tClass);
        } catch (DatabaseException e) {
            throw GeneralExceptionBuilder.buildDatabaseException(e);
        }
    }

    @Override
    public void save(T newObj, QueryTransaction transaction) throws PlatformException {
        try {
            transaction.getDBTransaction().save(newObj);
        } catch (DatabaseException e) {
            throw GeneralExceptionBuilder.buildDatabaseException(e);
        }
    }
}
