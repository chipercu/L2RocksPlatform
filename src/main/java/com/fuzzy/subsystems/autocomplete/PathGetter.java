package com.fuzzy.subsystems.autocomplete;

import com.infomaximum.database.domainobject.DomainObject;
import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.QueryTransaction;

import java.util.List;

public interface PathGetter<T extends DomainObject> {

    List<String> getPath(T object, QueryTransaction transaction) throws PlatformException;

}
