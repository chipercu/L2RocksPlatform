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

import java.util.Set;

public class AccessSchemeObjectsAddingQuery<
        S, O, T,
        U extends AccessSchemeItem<S, O, T>> extends GraphQLQuery<RemoteObject, Boolean> {

    private final AccessSchemeProcessor<S, O, T, U> accessSchemeProcessor;
    private final SubjectObjectChecker<S, O> checker;
    private final S subjectId;
    private final Set<O> objectIds;
    private final T operation;

    public AccessSchemeObjectsAddingQuery(@NonNull AccessSchemeProcessor<S, O, T, U> accessSchemeProcessor,
                                          @NonNull SubjectObjectChecker<S, O> checker,
                                          @NonNull S subjectId,
                                          @NonNull Set<O> objectIds,
                                          @NonNull T operation) {
        this.accessSchemeProcessor = accessSchemeProcessor;
        this.checker = checker;
        this.subjectId = subjectId;
        this.objectIds = objectIds;
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
