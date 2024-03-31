package com.fuzzy.subsystems.access;

import com.fuzzy.platform.component.frontend.context.ContextTransactionRequest;
import com.fuzzy.platform.exception.PlatformException;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.Serializable;
import java.util.HashSet;

public interface ExternalAccessGetter<O extends Serializable> {

    @NonNull HashSet<O> getAccessibleItems(@NonNull ContextTransactionRequest context) throws PlatformException;
}
