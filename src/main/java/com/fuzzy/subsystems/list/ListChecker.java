package com.fuzzy.subsystems.list;

import com.infomaximum.database.domainobject.DomainObject;
import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.sdk.context.ContextTransaction;

public interface ListChecker<T extends DomainObject> {

    boolean checkItem(T item, ContextTransaction<?> context) throws PlatformException;
}