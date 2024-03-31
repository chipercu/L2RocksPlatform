package com.fuzzy.subsystems.list;

import com.fuzzy.database.domainobject.DomainObject;
import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystems.function.Consumer;

public interface ListItemGetter<T extends DomainObject> {

    void foreEach(Consumer<T> handler, ContextTransaction<?> context) throws PlatformException;
}
