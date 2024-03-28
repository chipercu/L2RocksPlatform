package com.fuzzy.subsystems.accesscscheme.queries;

import com.fuzzy.main.cluster.core.remote.struct.RemoteObject;
import com.fuzzy.main.platform.component.frontend.context.ContextTransactionRequest;
import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.QueryTransaction;
import com.fuzzy.main.platform.querypool.ResourceProvider;
import com.fuzzy.main.platform.sdk.graphql.customfield.graphqlquery.GraphQLQuery;
import com.fuzzy.main.rdao.database.domainobject.DomainObject;
import com.fuzzy.subsystems.accesscscheme.domainobject.AccessSchemeItem;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;

public class AccessSchemeRemovingQuery<
        S, O, T,
        U extends DomainObject & AccessSchemeItem<S, O, T>> extends GraphQLQuery<RemoteObject, Boolean> {

    private final AccessSchemeProcessor<S, O, T, U> accessSchemeProcessor;
    private final SubjectObjectChecker<S, O> checker;
    private final ArrayList<? extends AccessSchemeItem<S, O, T>> accessItems;

    public AccessSchemeRemovingQuery(@NonNull AccessSchemeProcessor<S, O, T, U> accessSchemeProcessor,
                                     @NonNull SubjectObjectChecker<S, O> checker,
                                     @NonNull ArrayList<? extends AccessSchemeItem<S, O, T>> accessItems) {
        this.accessSchemeProcessor = accessSchemeProcessor;
        this.checker = checker;
        this.accessItems = accessItems;
    }

    @Override
    public void prepare(@NonNull ResourceProvider resources) {
        accessSchemeProcessor.prepare(resources);
        checker.prepare(resources);
    }

    @Override
    public Boolean execute(@Nullable RemoteObject source, @NonNull ContextTransactionRequest context) throws PlatformException {
        QueryTransaction transaction = context.getTransaction();
        for (AccessSchemeItem<S, O, T> accessItem : accessItems) {
            if (accessItem != null
                    && checker.checkSubject(accessItem.getSubjectId(), transaction)
                    && accessSchemeProcessor.checkAccessToSubject(accessItem.getSubjectId(), context)
                    && checker.checkObject(accessItem.getObjectId(), transaction)
                    && accessSchemeProcessor.checkAccessToObject(accessItem.getObjectId(), context)) {
                accessSchemeProcessor.removeAccess(
                        accessItem.getSubjectId(), accessItem.getObjectId(), accessItem.getOperation(), context);
            }
        }
        return true;
    }
}