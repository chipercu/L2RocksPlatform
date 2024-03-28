package com.fuzzy.subsystems.access;

import com.fuzzy.main.platform.component.frontend.context.ContextTransactionRequest;
import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystems.exception.GeneralExceptionBuilder;
import org.checkerframework.checker.nullness.qual.NonNull;

public class ContextAccessValidator<O, T> {

    private final AccessValidator<O, T> validator;

    public ContextAccessValidator(@NonNull AccessValidator<O, T> validator) {
        this.validator = validator;
    }

    public boolean checkWriteAccessById(@NonNull O id,
                                        @NonNull ContextTransaction<?> context) throws PlatformException {
        return check(id, context, validator::checkWriteAccessById);
    }

    public boolean checkWriteAccessByObject(@NonNull T object,
                                            @NonNull ContextTransaction<?> context) throws PlatformException {
        return check(object, context, validator::checkWriteAccessByObject);
    }

    public boolean checkReadAccessById(@NonNull O id,
                                       @NonNull ContextTransaction<?> context) throws PlatformException {
        return check(id, context, validator::checkReadAccessById);
    }

    public boolean checkReadAccessByObject(@NonNull T object,
                                           @NonNull ContextTransaction<?> context) throws PlatformException {
        return check(object, context, validator::checkReadAccessByObject);
    }

    public void validateWriteAccessById(@NonNull O id,
                                        @NonNull ContextTransaction<?> context) throws PlatformException {
        validate(id, context, validator::validateWriteAccessById);
    }

    public void validateWriteAccessByObject(@NonNull T object,
                                            @NonNull ContextTransaction<?> context) throws PlatformException {
        validate(object, context, validator::validateWriteAccessByObject);
    }

    public void validateReadAccessById(@NonNull O id,
                                       @NonNull ContextTransaction<?> context) throws PlatformException {
        validate(id, context, validator::validateReadAccessById);
    }

    public void validateReadAccessByObject(@NonNull T object,
                                           @NonNull ContextTransaction<?> context) throws PlatformException {
        validate(object, context, validator::validateReadAccessByObject);
    }

    private <U> boolean check(U value,
                              @NonNull ContextTransaction<?> context,
                              CheckingFunction<U> checkingFunction) throws PlatformException {
        if (context instanceof ContextTransactionRequest) {
            return checkingFunction.check(value, (ContextTransactionRequest) context);
        }
        return false;
    }

    private <U> void validate(U value,
                              @NonNull ContextTransaction<?> context,
                              ValidatingFunction<U> validatingFunction) throws PlatformException {
        if (context instanceof ContextTransactionRequest) {
            validatingFunction.validate(value, (ContextTransactionRequest) context);
        } else {
            throw GeneralExceptionBuilder.buildAccessDeniedException();
        }
    }

    @FunctionalInterface
    private interface CheckingFunction<T> {
        boolean check(T id, @NonNull ContextTransactionRequest context) throws PlatformException;
    }

    @FunctionalInterface
    private interface ValidatingFunction<T> {
        void validate(T object, @NonNull ContextTransactionRequest context) throws PlatformException;
    }
}
