package com.fuzzy.subsystems.accesscscheme.queries.service;

import com.fuzzy.platform.component.frontend.context.ContextTransactionRequest;
import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.QueryTransaction;
import com.fuzzy.platform.querypool.ResourceProvider;
import com.fuzzy.subsystems.accesscscheme.domainobject.AccessSchemeItem;
import com.fuzzy.subsystems.accesscscheme.queries.AccessSchemeProcessor;
import com.fuzzy.subsystems.accesscscheme.queries.SubjectObjectChecker;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashSet;
import java.util.Set;

public class AccessSchemeElementAddingService<S, O, T, U extends AccessSchemeItem<S, O, T>> {

    private final AccessSchemeProcessor<S, O, T, U> accessSchemeProcessor;
    private final SubjectObjectChecker<S, O> checker;

    public AccessSchemeElementAddingService(@NonNull ResourceProvider resources,
                                            @NonNull AccessSchemeProcessor<S, O, T, U> accessSchemeProcessor,
                                            @NonNull SubjectObjectChecker<S, O> checker) {
        this.accessSchemeProcessor = accessSchemeProcessor;
        this.checker = checker;
        this.accessSchemeProcessor.prepare(resources);
        this.checker.prepare(resources);
    }

    public boolean addSubjects(@NonNull O objectId,
                               @NonNull HashSet<S> subjectIds,
                               @NonNull T operation,
                               @NonNull ContextTransactionRequest context) throws PlatformException {
        QueryTransaction transaction = context.getTransaction();
        if (!checker.checkObject(objectId, transaction) || !accessSchemeProcessor.checkAccessToObject(objectId, context)) {
            return true;
        }
        for (S subjectId : subjectIds) {
            if (subjectId != null && checker.checkSubject(subjectId, transaction)
                    && accessSchemeProcessor.checkAccessToSubject(subjectId, context)) {
                accessSchemeProcessor.addAccess(subjectId, objectId, operation, context);
            }
        }
        return true;
    }

    public boolean addObjects(@NonNull S subjectId,
                              @NonNull Set<O> objectIds,
                              @NonNull T operation,
                              @NonNull ContextTransactionRequest context) throws PlatformException {
        QueryTransaction transaction = context.getTransaction();
        if (!checker.checkSubject(subjectId, transaction) || !accessSchemeProcessor.checkAccessToSubject(subjectId, context)) {
            return true;
        }
        for (O objectId : objectIds) {
            if (objectId != null && checker.checkObject(objectId, transaction)
                    && accessSchemeProcessor.checkAccessToObject(objectId, context)) {
                accessSchemeProcessor.addAccess(subjectId, objectId, operation, context);
            }
        }
        return true;
    }
}
