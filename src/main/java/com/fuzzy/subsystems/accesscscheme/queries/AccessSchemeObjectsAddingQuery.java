package com.fuzzy.subsystems.accesscscheme.queries;

import com.infomaximum.cluster.core.remote.struct.RemoteObject;
import com.infomaximum.platform.component.frontend.context.ContextTransactionRequest;
import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.ResourceProvider;
import com.infomaximum.platform.sdk.graphql.customfield.graphqlquery.GraphQLQuery;
import com.fuzzy.subsystems.accesscscheme.domainobject.AccessSchemeItem;
import com.fuzzy.subsystems.accesscscheme.queries.service.AccessSchemeElementAddingService;
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
    private AccessSchemeElementAddingService<S, O, T, U> accessSchemeElementAddingService;

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
        accessSchemeElementAddingService = new AccessSchemeElementAddingService<>(resources, accessSchemeProcessor, checker);
    }

    @Override
    public Boolean execute(@Nullable RemoteObject source, @NonNull ContextTransactionRequest context) throws PlatformException {
        return accessSchemeElementAddingService.addObjects(this.subjectId, this.objectIds, operation, context);
    }
}
