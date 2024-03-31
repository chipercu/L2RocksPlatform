package com.fuzzy.platform.querypool;

import com.fuzzy.database.domainobject.DomainObject;
import com.fuzzy.database.domainobject.DomainObjectEditable;
import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.QueryTransaction;
import com.fuzzy.platform.querypool.ReadableResource;

public interface EditableResource<T extends DomainObject & DomainObjectEditable> extends ReadableResource<T> {

    T create(QueryTransaction transaction) throws PlatformException;

    void save(T newObj, QueryTransaction transaction) throws PlatformException;
}
