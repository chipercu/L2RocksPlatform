package com.fuzzy.main.platform.querypool;

import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.rdao.database.domainobject.DomainObject;
import com.fuzzy.main.rdao.database.domainobject.DomainObjectEditable;

public interface EditableResource<T extends DomainObject & DomainObjectEditable> extends ReadableResource<T> {

    T create(QueryTransaction transaction) throws PlatformException;

    void save(T newObj, QueryTransaction transaction) throws PlatformException;
}
