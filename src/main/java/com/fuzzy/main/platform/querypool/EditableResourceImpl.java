package com.fuzzy.main.platform.querypool;

import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.sdk.exception.GeneralExceptionBuilder;
import com.fuzzy.main.rdao.database.domainobject.DomainObject;
import com.fuzzy.main.rdao.database.domainobject.DomainObjectEditable;
import com.fuzzy.main.rdao.database.exception.DatabaseException;

class EditableResourceImpl<T extends DomainObject & DomainObjectEditable> extends ReadableResourceImpl<T> implements EditableResource<T> {

    EditableResourceImpl(Class<T> tClass) {
        super(tClass);
    }

    @Override
    public T create(QueryTransaction transaction) throws PlatformException {
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
