package com.fuzzy.subsystems.access;

import com.infomaximum.platform.component.frontend.context.ContextTransactionRequest;
import com.infomaximum.platform.exception.PlatformException;
import com.fuzzy.subsystems.exception.GeneralExceptionBuilder;
import org.checkerframework.checker.nullness.qual.NonNull;

@FunctionalInterface
public interface CheckingFunction<T> {

    boolean check(T object, @NonNull ContextTransactionRequest context) throws PlatformException;

    static <T> void validate(T object,
                             @NonNull ContextTransactionRequest context,
                             @NonNull CheckingFunction<T> checkingFunction) throws PlatformException {
        if (!checkingFunction.check(object, context)) {
            throw GeneralExceptionBuilder.buildAccessDeniedException();
        }
    }
}