package com.fuzzy.subsystems.resourceswithnotifications;

import com.fuzzy.database.domainobject.DomainObject;
import com.fuzzy.database.domainobject.DomainObjectEditable;
import com.fuzzy.database.domainobject.filter.Filter;
import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.EditableResource;
import com.fuzzy.platform.querypool.QueryTransaction;
import com.fuzzy.platform.querypool.ReadableResource;
import com.fuzzy.platform.querypool.ResourceProvider;
import com.fuzzy.platform.querypool.iterator.IteratorEntity;
import com.fuzzy.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystems.remote.RCExecutor;

import java.util.Set;

public class EditableResourceWithNotifications<T extends DomainObject & DomainObjectEditable, Y extends RCDomainObjectNotifications> implements ReadableResource<T> {

    private final EditableResource<T> editableResource;
    private final RCExecutor<Y> rcNotifications;

    public EditableResourceWithNotifications(ResourceProvider resources, Class<T> resClass, Class<Y> rcNotificationsClass) {
        this(resources.getEditableResource(resClass), new RCExecutor<>(resources, rcNotificationsClass));
    }

    protected EditableResourceWithNotifications(EditableResource<T> editableResource, RCExecutor<Y> rcNotifications) {
        this.editableResource = editableResource;
        this.rcNotifications = rcNotifications;
    }

    @Override
    public Class<T> getDomainClass() {
        return editableResource.getDomainClass();
    }

    @Override
    public T get(long id, QueryTransaction transaction) throws PlatformException {
        return editableResource.get(id, transaction);
    }

    @Override
    public T get(long id, Set<Integer> loadingFields, QueryTransaction transaction) throws PlatformException {
        return editableResource.get(id, loadingFields, transaction);
    }

    @Override
    public T find(Filter filter, QueryTransaction transaction) throws PlatformException {
        return editableResource.find(filter, transaction);
    }

    @Override
    public T find(Filter filter, Set<Integer> loadingFields, QueryTransaction transaction) throws PlatformException {
        return editableResource.find(filter, loadingFields, transaction);
    }

    @Override
    public IteratorEntity<T> iterator(QueryTransaction transaction) throws PlatformException {
        return editableResource.iterator(transaction);
    }

    @Override
    public IteratorEntity<T> iterator(Set<Integer> loadingFields, QueryTransaction transaction) throws PlatformException {
        return editableResource.iterator(loadingFields, transaction);
    }

    @Override
    public IteratorEntity<T> findAll(Filter filter, QueryTransaction transaction) throws PlatformException {
        return editableResource.findAll(filter, transaction);
    }

    @Override
    public IteratorEntity<T> findAll(Filter filter, Set<Integer> loadingFields, QueryTransaction transaction) throws PlatformException {
        return editableResource.findAll(filter, loadingFields, transaction);
    }

    public T create(QueryTransaction transaction) throws PlatformException {
        return editableResource.create(transaction);
    }

    public void saveCreation(T newObj, ContextTransaction<?> context) throws PlatformException {
        editableResource.save(newObj, context.getTransaction());
        rcNotifications.exec(y -> y.onAfterCreation(newObj.getId(), context));
    }

    public void saveUpdate(T newObj, ContextTransaction<?> context) throws PlatformException {
        rcNotifications.exec(y -> y.onBeforeUpdate(newObj.getId(), context));
        editableResource.save(newObj, context.getTransaction());
        rcNotifications.exec(y -> y.onAfterUpdate(newObj.getId(), context));
    }
}
