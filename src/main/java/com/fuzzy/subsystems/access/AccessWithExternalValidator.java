package com.fuzzy.subsystems.access;

import com.infomaximum.platform.component.frontend.context.ContextTransactionRequest;
import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.QueryRemoteController;
import com.infomaximum.platform.querypool.ResourceProvider;
import com.fuzzy.subsystems.remote.RCExecutor;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public abstract class AccessWithExternalValidator<O extends Serializable, T, S extends ExternalAccessGetter<O> & QueryRemoteController>
        implements AccessValidator<O, T> {

    private final AccessValidator<O, T> accessValidator;
    private final RCExecutor<S> externalAccess;
    private Set<O> externalAccessibleItems;

    public AccessWithExternalValidator(@NonNull ResourceProvider resources,
                                       @NonNull Class<S> controllerClass,
                                       @NonNull AccessValidator<O, T> accessValidator) {
        this.accessValidator = accessValidator;
        this.externalAccess = new RCExecutor<>(resources, controllerClass);
        this.externalAccessibleItems = null;
    }

    @Override
    public boolean checkWriteAccessById(@NonNull O id,
                                        @NonNull ContextTransactionRequest context) throws PlatformException {
        return accessValidator.checkWriteAccessById(id, context);
    }

    @Override
    public boolean checkWriteAccessByObject(@NonNull T object,
                                            @NonNull ContextTransactionRequest context) throws PlatformException {
        return accessValidator.checkWriteAccessByObject(object, context);
    }

    @Override
    public boolean checkReadAccessById(@NonNull O id,
                                       @NonNull ContextTransactionRequest context) throws PlatformException {
        return accessValidator.checkReadAccessById(id, context) || checkExternalAccess(id, context);
    }

    @Override
    public boolean checkReadAccessByObject(@NonNull T object,
                                           @NonNull ContextTransactionRequest context) throws PlatformException {
        return accessValidator.checkReadAccessByObject(object, context) ||
                checkExternalAccess(getId(object), context);
    }

    protected abstract @NonNull O getId(@NonNull T object);

    private boolean checkExternalAccess(@NonNull O id,
                                        @NonNull ContextTransactionRequest context) throws PlatformException {
        if (externalAccessibleItems == null) {
            externalAccessibleItems = externalAccess.apply(rc -> rc.getAccessibleItems(context), HashSet::new);
        }
        return externalAccessibleItems.contains(id);
    }
}
