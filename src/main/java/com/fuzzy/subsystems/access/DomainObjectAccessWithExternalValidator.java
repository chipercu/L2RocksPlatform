package com.fuzzy.subsystems.access;

import com.fuzzy.database.domainobject.DomainObject;
import com.fuzzy.platform.querypool.QueryRemoteController;
import com.fuzzy.platform.querypool.ResourceProvider;
import org.checkerframework.checker.nullness.qual.NonNull;

public class DomainObjectAccessWithExternalValidator<
        T extends DomainObject,
        S extends ExternalAccessGetter<Long> & QueryRemoteController> extends AccessWithExternalValidator<Long, T, S> {

    public DomainObjectAccessWithExternalValidator(@NonNull ResourceProvider resources,
                                                   @NonNull Class<S> controllerClass,
                                                   @NonNull AccessValidator<Long, T> accessValidator) {
        super(resources, controllerClass, accessValidator);
    }

    @Override
    protected @NonNull Long getId(@NonNull T object) {
        return object.getId();
    }
}
