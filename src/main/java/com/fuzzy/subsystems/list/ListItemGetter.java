package com.fuzzy.subsystems.list;

import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.sdk.context.ContextTransaction;
import com.fuzzy.main.rdao.database.domainobject.DomainObject;
import com.fuzzy.subsystems.function.Consumer;

public interface ListItemGetter<T extends DomainObject> {

    void foreEach(Consumer<T> handler, ContextTransaction<?> context) throws PlatformException;
}
