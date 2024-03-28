package com.fuzzy.main.platform.querypool;

import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.sdk.exception.GeneralExceptionBuilder;
import com.fuzzy.main.rdao.database.domainobject.DomainObject;
import com.fuzzy.main.rdao.database.domainobject.DomainObjectEditable;
import com.fuzzy.main.rdao.database.exception.DatabaseException;

class RemovableResourceImpl<T extends DomainObject & DomainObjectEditable> extends EditableResourceImpl<T> implements RemovableResource<T> {

	RemovableResourceImpl(Class<T> tClass) {
        super(tClass);
    }

    @Override
    public void remove(T obj, QueryTransaction transaction) throws PlatformException {
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
