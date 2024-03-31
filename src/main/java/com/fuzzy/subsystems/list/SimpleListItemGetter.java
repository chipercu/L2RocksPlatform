package com.fuzzy.subsystems.list;

import com.fuzzy.database.domainobject.DomainObject;
import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.ReadableResource;
import com.fuzzy.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystems.function.Consumer;

class SimpleListItemGetter<T extends DomainObject> implements ListItemGetter<T> {

    private final ReadableResource<T> readableResource;

    public SimpleListItemGetter(ReadableResource<T> readableResource) {
        this.readableResource = readableResource;
    }

    @Override
    public void foreEach(Consumer<T> handler, ContextTransaction<?> context) throws PlatformException {
        readableResource.forEach(handler::accept, context.getTransaction());
    }
}
