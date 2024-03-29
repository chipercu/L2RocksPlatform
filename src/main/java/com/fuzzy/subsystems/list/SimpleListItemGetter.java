package com.fuzzy.subsystems.list;

import com.infomaximum.database.domainobject.DomainObject;
import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.ReadableResource;
import com.infomaximum.platform.sdk.context.ContextTransaction;
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
