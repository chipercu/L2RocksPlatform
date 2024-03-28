package com.fuzzy.subsystems.accesscscheme;

import com.fuzzy.main.platform.component.frontend.context.ContextTransactionRequest;
import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.subsystems.access.AccessValidator;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Collection;

public abstract class StandardAccessValidator<O, T> implements AccessValidator<O, T> {

    public abstract @Nullable Collection<StandardAccessOperation> getAccessOperationsById(
            @NonNull O id, @NonNull ContextTransactionRequest context) throws PlatformException;

    public abstract @Nullable Collection<StandardAccessOperation> getAccessOperationsByObject(
            @NonNull T object, @NonNull ContextTransactionRequest context) throws PlatformException;

    @Override
    public boolean checkWriteAccessById(@NonNull O id,
                                        @NonNull ContextTransactionRequest context) throws PlatformException {
        return checkWriteAccess(getAccessOperationsById(id, context));
    }

    @Override
    public boolean checkWriteAccessByObject(@NonNull T object,
                                            @NonNull ContextTransactionRequest context) throws PlatformException {
        return checkWriteAccess(getAccessOperationsByObject(object, context));
    }

    @Override
    public boolean checkReadAccessById(@NonNull O id,
                                       @NonNull ContextTransactionRequest context) throws PlatformException {
        return checkReadAccess(getAccessOperationsById(id, context));
    }

    @Override
    public boolean checkReadAccessByObject(@NonNull T object,
                                           @NonNull ContextTransactionRequest context) throws PlatformException {
        return checkReadAccess(getAccessOperationsByObject(object, context));
    }

    private boolean checkReadAccess(@Nullable Collection<StandardAccessOperation> operations) {
        return operations != null
                && (operations.contains(StandardAccessOperation.WRITE) || operations.contains(StandardAccessOperation.READ));
    }

    private boolean checkWriteAccess(@Nullable Collection<StandardAccessOperation> operations) {
        return operations != null && operations.contains(StandardAccessOperation.WRITE);
    }
}
