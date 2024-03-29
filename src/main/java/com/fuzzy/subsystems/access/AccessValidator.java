package com.fuzzy.subsystems.access;

import com.infomaximum.platform.component.frontend.context.ContextTransactionRequest;
import com.infomaximum.platform.exception.PlatformException;
import org.checkerframework.checker.nullness.qual.NonNull;

public interface AccessValidator<O, T> {

    boolean checkWriteAccessById(@NonNull O id, @NonNull ContextTransactionRequest context) throws PlatformException;

    boolean checkWriteAccessByObject(@NonNull T object, @NonNull ContextTransactionRequest context) throws PlatformException;

    boolean checkReadAccessById(@NonNull O id, @NonNull ContextTransactionRequest context) throws PlatformException;

    boolean checkReadAccessByObject(@NonNull T object, @NonNull ContextTransactionRequest context) throws PlatformException;

    default void validateWriteAccessById(@NonNull O id, @NonNull ContextTransactionRequest context) throws PlatformException {
        CheckingFunction.validate(id, context, this::checkWriteAccessById);
    }

    default void validateWriteAccessByObject(@NonNull T object, @NonNull ContextTransactionRequest context) throws PlatformException {
        CheckingFunction.validate(object, context, this::checkWriteAccessByObject);
    }

    default void validateReadAccessById(@NonNull O id, @NonNull ContextTransactionRequest context) throws PlatformException {
        CheckingFunction.validate(id, context, this::checkReadAccessById);
    }

    default void validateReadAccessByObject(@NonNull T object, @NonNull ContextTransactionRequest context) throws PlatformException {
        CheckingFunction.validate(object, context, this::checkReadAccessByObject);
    }
}
