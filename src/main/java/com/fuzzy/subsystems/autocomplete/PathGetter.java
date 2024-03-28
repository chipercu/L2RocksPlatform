package com.fuzzy.subsystems.autocomplete;

import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.QueryTransaction;
import com.fuzzy.main.rdao.database.domainobject.DomainObject;

import java.util.List;

public interface PathGetter<T extends DomainObject> {

    List<String> getPath(T object, QueryTransaction transaction) throws PlatformException;

}
