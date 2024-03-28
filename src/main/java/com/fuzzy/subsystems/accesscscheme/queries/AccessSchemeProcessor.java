package com.fuzzy.subsystems.accesscscheme.queries;

import com.fuzzy.main.platform.component.frontend.context.ContextTransactionRequest;
import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.QueryTransaction;
import com.fuzzy.main.platform.querypool.ResourceProvider;
import com.fuzzy.main.platform.sdk.context.ContextTransaction;
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
