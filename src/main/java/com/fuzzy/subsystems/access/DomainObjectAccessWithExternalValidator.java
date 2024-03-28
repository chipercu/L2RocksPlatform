package com.fuzzy.subsystems.access;

import com.fuzzy.main.platform.querypool.QueryRemoteController;
import com.fuzzy.main.platform.querypool.ResourceProvider;
import com.fuzzy.main.rdao.database.domainobject.DomainObject;
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
