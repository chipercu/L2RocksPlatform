package com.fuzzy.subsystems.accesscscheme.queries;

import com.infomaximum.platform.component.frontend.context.ContextTransactionRequest;
import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.QueryTransaction;
import com.infomaximum.platform.querypool.ResourceProvider;
import com.infomaximum.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystems.accesscscheme.domainobject.AccessSchemeItem;
import com.fuzzy.subsystems.function.Function;
import org.checkerframework.checker.nullness.qual.NonNull;

public interface AccessSchemeProcessor<S, O, T, U extends AccessSchemeItem<S, O, T>> {

    void prepare(ResourceProvider resources);

    boolean checkAccessToSubject(@NonNull S subjectId,
                                 @NonNull  ContextTransactionRequest context) throws PlatformException;

    boolean checkAccessToObject(@NonNull O objectId,
                                @NonNull ContextTransactionRequest context) throws PlatformException;

    void forEachByObject(@NonNull O objectId,
                         @NonNull Function<U, Boolean> handler,
                         @NonNull QueryTransaction transaction) throws PlatformException;

    void forEachBySubject(@NonNull S subjectId,
                          @NonNull Function<U, Boolean> handler,
                          @NonNull QueryTransaction transaction) throws PlatformException;

    void addAccess(@NonNull S subjectId,
                   @NonNull O objectId,
                   @NonNull T operation,
                   @NonNull ContextTransaction<?> context) throws PlatformException;

    void removeAccess(@NonNull S subjectId,
                      @NonNull O objectId,
                      @NonNull T operation,
                      @NonNull ContextTransaction<?> context) throws PlatformException;
}
