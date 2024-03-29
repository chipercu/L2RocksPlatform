package com.fuzzy.subsystems.list;

import com.infomaximum.database.domainobject.DomainObject;
import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystems.function.Consumer;

public interface ListItemGetter<T extends DomainObject> {

    void foreEach(Consumer<T> handler, ContextTransaction<?> context) throws PlatformException;
}
