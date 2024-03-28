package com.fuzzy.subsystems.resourceswithnotifications;

import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.RemovableResource;
import com.fuzzy.main.platform.querypool.ResourceProvider;
import com.fuzzy.main.platform.querypool.iterator.IteratorEntity;
import com.fuzzy.main.platform.sdk.context.ContextTransaction;
import com.fuzzy.main.rdao.database.domainobject.DomainObject;
import com.fuzzy.main.rdao.database.domainobject.DomainObjectEditable;
import com.fuzzy.main.rdao.database.domainobject.filter.Filter;
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
