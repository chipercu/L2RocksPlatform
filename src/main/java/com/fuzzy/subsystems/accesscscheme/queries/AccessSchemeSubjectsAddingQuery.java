package com.fuzzy.subsystems.accesscscheme.queries;

import com.fuzzy.main.cluster.core.remote.struct.RemoteObject;
import com.fuzzy.main.platform.component.frontend.context.ContextTransactionRequest;
import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.QueryTransaction;
import com.fuzzy.main.platform.querypool.ResourceProvider;
import com.fuzzy.main.platform.sdk.graphql.customfield.graphqlquery.GraphQLQuery;
import com.fuzzy.subsystems.accesscscheme.domainobject.AccessSchemeItem;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.HashSet;

public class AccessSchemeSubjectsAddingQuery <
        S, O, T,
        U extends AccessSchemeItem<S, O, T>> extends GraphQLQuery<RemoteObject, Boolean> {

    private final AccessSchemeProcessor<S, O, T, U> accessSchemeProcessor;
    private final SubjectObjectChecker<S, O> checker;
    private final O objectId;
    private final HashSet<S> subjectIds;
    private final T operation;

    public AccessSchemeSubjectsAddingQuery(@NonNull AccessSchemeProcessor<S, O, T, U> accessSchemeProcessor,
                                           @NonNull SubjectObjectChecker<S, O> checker,
                                           @NonNull O objectId,
                                           @NonNull HashSet<S> subjectIds,
                                           @NonNull T operation) {
        this.accessSchemeProcessor = accessSchemeProcessor;
        this.checker = checker;
        this.objectId = objectId;
        this.subjectIds = subjectIds;
        this.operation = operation;
    }

    @Override
    public void prepare(@NonNull ResourceProvider resources) {
        accessSchemeProcessor.prepare(resources);
        checker.prepare(resources);
    }

    @Override
    public Boolean execute(@Nullable RemoteObject source, @NonNull ContextTransactionRequest context) throws PlatformException {
        QueryTransaction transaction = context.getTransaction();
        if (!checker.checkObject(objectId, transaction) || !accessSchemeProcessor.checkAccessToObject(objectId, context)) {
            return true;
        }
        for (S subjectId : subjectIds) {
            if (subjectId != null && checker.checkSubject(subjectId, transaction)
                    && accessSchemeProcessor.checkAccessToSubject(subjectId, context)) {
                accessSchemeProcessor.addAccess(subjectId, this.objectId, operation, context);
            }
        }
        return true;
    }
}
