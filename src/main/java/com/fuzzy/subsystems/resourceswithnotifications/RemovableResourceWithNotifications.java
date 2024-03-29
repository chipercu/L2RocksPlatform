package com.fuzzy.subsystems.resourceswithnotifications;

import com.infomaximum.database.domainobject.DomainObject;
import com.infomaximum.database.domainobject.DomainObjectEditable;
import com.infomaximum.database.domainobject.filter.Filter;
import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.RemovableResource;
import com.infomaximum.platform.querypool.ResourceProvider;
import com.infomaximum.platform.querypool.iterator.IteratorEntity;
import com.infomaximum.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystems.remote.RCExecutor;

public class RemovableResourceWithNotifications<T extends DomainObject & DomainObjectEditable, Y extends RCDomainObjectNotifications>
        extends EditableResourceWithNotifications<T, Y> {

    private final RemovableResource<T> removableResource;
    private final RCExecutor<Y> rcNotifications;

    public RemovableResourceWithNotifications(ResourceProvider resources, Class<T> resClass, Class<Y> rcNotificationsClass) {
        this(resources.getRemovableResource(resClass), new RCExecutor<>(resources, rcNotificationsClass));
    }

    private RemovableResourceWithNotifications(RemovableResource<T> removableResource, RCExecutor<Y> rcNotifications) {
        super(removableResource, rcNotifications);
        this.removableResource = removableResource;
        this.rcNotifications = rcNotifications;
    }

    public void remove(T obj, ContextTransaction<?> context) throws PlatformException {
        rcNotifications.exec(y -> y.onBeforeRemoval(obj.getId(), context));
        removableResource.remove(obj, context.getTransaction());
        rcNotifications.exec(y -> y.onAfterRemoval(context));
    }

    public boolean removeAll(Filter filter, ContextTransaction<?> context) throws PlatformException {
        boolean result = false;
        try (IteratorEntity<T> i = findAll(filter, context.getTransaction())) {
            while (i.hasNext()) {
                remove(i.next(), context);
                result = true;
            }
        }
        return result;
    }
}
