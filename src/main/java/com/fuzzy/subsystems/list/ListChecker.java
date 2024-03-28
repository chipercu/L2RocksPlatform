package com.fuzzy.subsystems.list;

import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.sdk.context.ContextTransaction;
import com.fuzzy.main.rdao.database.domainobject.DomainObject;

public interface ListChecker<T extends DomainObject> {

    boolean checkItem(T item, ContextTransaction<?> context) throws PlatformException;
}