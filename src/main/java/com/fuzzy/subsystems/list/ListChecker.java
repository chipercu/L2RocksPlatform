package com.fuzzy.subsystems.list;

import com.fuzzy.database.domainobject.DomainObject;
import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.sdk.context.ContextTransaction;

public interface ListChecker<T extends DomainObject> {

    boolean checkItem(T item, ContextTransaction<?> context) throws PlatformException;
}