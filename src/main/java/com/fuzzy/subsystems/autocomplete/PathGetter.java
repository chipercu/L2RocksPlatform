package com.fuzzy.subsystems.autocomplete;

import com.fuzzy.database.domainobject.DomainObject;
import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.QueryTransaction;

import java.util.List;

public interface PathGetter<T extends DomainObject> {

    List<String> getPath(T object, QueryTransaction transaction) throws PlatformException;

}
