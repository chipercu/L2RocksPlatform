package com.fuzzy.platform.querypool;

import com.fuzzy.database.anotation.Entity;
import com.fuzzy.database.domainobject.DomainObject;
import com.fuzzy.database.domainobject.DomainObjectEditable;
import com.fuzzy.database.schema.StructEntity;
import com.fuzzy.platform.exception.runtime.ClosedObjectException;
import com.fuzzy.platform.querypool.*;
import com.fuzzy.platform.querypool.EditableResource;
import com.fuzzy.platform.querypool.EditableResourceImpl;
import com.fuzzy.platform.querypool.QueryPool;
import com.fuzzy.platform.querypool.ReadableResource;
import com.fuzzy.platform.querypool.ReadableResourceImpl;
import com.fuzzy.platform.querypool.RemovableResource;
import com.fuzzy.platform.querypool.ResourceProvider;
import com.fuzzy.platform.sdk.component.Component;

import java.util.HashMap;
import java.util.Set;

public class ResourceProviderImpl implements ResourceProvider, AutoCloseable {

    private final Component component;
    private final HashMap<String, com.fuzzy.platform.querypool.QueryPool.LockType> resources = new HashMap<>();
    private boolean closed = false;

    protected ResourceProviderImpl(Component component) {
        this.component = component;
    }

    @Override
    public <T extends QueryRemoteController> Set<T> getQueryRemoteControllers(Class<T> remoteControllerClass) {
        check();
        return component.getQueryRemotes().getControllers(this, remoteControllerClass);
    }

    @Override
    public <T extends QueryRemoteController> T getQueryRemoteController(Class<? extends Component> componentClass, Class<T> remoteControllerClass) {
        check();
        return component.getQueryRemotes().getController(this, componentClass, remoteControllerClass);
    }

    @Override
    public <T extends QueryRemoteController> T getQueryRemoteController(String componentUuid, Class<T> remoteControllerClass) {
        check();
        return component.getQueryRemotes().getController(this, componentUuid, remoteControllerClass);
    }

    @Override
    public <T extends QueryRemoteController> boolean isQueryRemoteController(String componentUuid, Class<T> remoteControllerClass) {
        check();
        return component.getQueryRemotes().isController(componentUuid, remoteControllerClass);
    }

    @Override
    public <T extends DomainObject & DomainObjectEditable> EditableResource<T> getEditableResource(Class<T> resClass) {
        borrowResource(resolveReadClass(resClass), com.fuzzy.platform.querypool.QueryPool.LockType.EXCLUSIVE);
        return new com.fuzzy.platform.querypool.EditableResourceImpl<>(resClass);
    }

    @Override
    public <T extends DomainObject> ReadableResource<T> getReadableResource(Class<T> resClass) {
        checkReadClass(resClass);
        borrowResource(resClass, com.fuzzy.platform.querypool.QueryPool.LockType.SHARED);
        return new com.fuzzy.platform.querypool.ReadableResourceImpl<>(resClass);
    }

    @Override
    public <T extends DomainObject & DomainObjectEditable> RemovableResource<T> getRemovableResource(Class<T> resClass) {
        borrowResource(resolveReadClass(resClass), com.fuzzy.platform.querypool.QueryPool.LockType.EXCLUSIVE);
		return new RemovableResourceImpl<>(resClass);
    }

    @Override
    public void borrowAllDomainObjects(com.fuzzy.platform.querypool.QueryPool.LockType type) {
        for (StructEntity structEntity: component.getSchema().getDomains()) {
            Class<? extends DomainObject> readClass = structEntity.getObjectClass();
            checkReadClass(readClass);
            borrowResource(readClass, type);
        }
    }

    @Override
    public void borrowResource(Class resClass, com.fuzzy.platform.querypool.QueryPool.LockType type) {
        borrowResource(resClass.getName(), type);
    }

    public void borrowResource(String resource, com.fuzzy.platform.querypool.QueryPool.LockType type) {
        check();
        appendResource(resource, type, resources);
    }

    protected HashMap<String, com.fuzzy.platform.querypool.QueryPool.LockType> getResources() {
        check();
        return resources;
    }

    @Override
    public void close() {
        closed = true;
    }

    private void check() {
        if (closed) {
            throw new ClosedObjectException(this.getClass());
        }
    }

    public static void appendResource(String resource, com.fuzzy.platform.querypool.QueryPool.LockType type, HashMap<String, com.fuzzy.platform.querypool.QueryPool.LockType> destination) {
        destination.merge(resource, type, (val1, val2) -> val1 == QueryPool.LockType.EXCLUSIVE ? val1 : val2);
    }

    private static <T extends DomainObject & DomainObjectEditable> Class<?> resolveReadClass(Class<T> editClass) {
        Class<?> readClass = editClass;
        do {
            readClass = readClass.getSuperclass();
        } while (!readClass.isAnnotationPresent(Entity.class));
        return readClass;
    }

    private <T extends DomainObject> void checkReadClass(Class<T> resClass) {
        if (!resClass.isAnnotationPresent(Entity.class)) {
            throw new IllegalArgumentException("class-Readable " + resClass.getSimpleName() + " not contains Entity annotation");
        }
    }
}
