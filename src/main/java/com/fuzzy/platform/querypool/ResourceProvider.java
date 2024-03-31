package com.fuzzy.platform.querypool;

import com.fuzzy.database.domainobject.DomainObject;
import com.fuzzy.database.domainobject.DomainObjectEditable;
import com.fuzzy.platform.querypool.EditableResource;
import com.fuzzy.platform.querypool.QueryPool;
import com.fuzzy.platform.querypool.QueryRemoteController;
import com.fuzzy.platform.querypool.ReadableResource;
import com.fuzzy.platform.querypool.RemovableResource;
import com.fuzzy.platform.sdk.component.Component;

import java.util.Set;

public interface ResourceProvider {

    <T extends QueryRemoteController> Set<T> getQueryRemoteControllers(Class<T> remoteControllerClass);

    <T extends QueryRemoteController> T getQueryRemoteController(Class<? extends Component> classComponent, Class<T> remoteControllerClass);

    <T extends QueryRemoteController> T getQueryRemoteController(String componentUuid, Class<T> remoteControllerClass);

    <T extends QueryRemoteController> boolean isQueryRemoteController(String componentUuid, Class<T> remoteControllerClass);

    <T extends DomainObject> ReadableResource<T> getReadableResource(Class<T> resClass);

    <T extends DomainObject & DomainObjectEditable> EditableResource<T> getEditableResource(Class<T> resClass);

    <T extends DomainObject & DomainObjectEditable> RemovableResource<T> getRemovableResource(Class<T> resClass);

    void borrowAllDomainObjects(com.fuzzy.platform.querypool.QueryPool.LockType type);

    void borrowResource(Class resClass, QueryPool.LockType type);
}
