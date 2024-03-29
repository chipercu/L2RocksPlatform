package com.fuzzy.subsystems.graphql.query;

import com.infomaximum.cluster.core.remote.struct.RemoteObject;
import com.infomaximum.platform.component.frontend.authcontext.UnauthorizedContext;
import com.infomaximum.platform.component.frontend.context.ContextTransactionRequest;
import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.ResourceProvider;
import com.infomaximum.platform.sdk.graphql.customfield.graphqlquery.GraphQLQuery;
import com.fuzzy.subsystem.frontend.authcontext.AuthorizedContext;
import com.fuzzy.subsystems.access.AccessOperation;
import com.fuzzy.subsystems.access.AccessOperationCollection;
import com.fuzzy.subsystems.access.PrivilegeEnum;
import com.fuzzy.subsystems.exception.GeneralExceptionBuilder;
import com.fuzzy.subsystems.function.Function;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.Serializable;
import java.util.ArrayList;

public class GAccessQuery <S extends RemoteObject, T extends Serializable> extends GraphQLQuery<S, T> {

    public enum Operator {
        AND, OR
    }

    private final GraphQLQuery<S, T> query;
    private final Operator operator;
    private final ArrayList<Pair> privileges;

    public GAccessQuery(@NonNull GraphQLQuery<S, T> query, @NonNull Operator operator) {
        this.query = query;
        this.operator = operator;
        this.privileges = new ArrayList<>();
    }

    public GAccessQuery(@NonNull Function<S, T> function, @NonNull Operator operator) {
        this(new GraphQLQuery<>() {
                 @Override
                 public void prepare(@NonNull ResourceProvider resources) {}

                 @Override
                 public @Nullable T execute(@Nullable S source,
                                            @NonNull ContextTransactionRequest context) throws PlatformException {
                     return function.apply(source);
                 }
             }, operator);
    }

    public GAccessQuery(@NonNull GraphQLQuery<S, T> query,
                        @NonNull PrivilegeEnum privilege,
                        @NonNull AccessOperation... operations) {
        this(query, Operator.AND);
        this.with(privilege, operations);
    }

    public GAccessQuery(@NonNull Function<S, T> function,
                        @NonNull PrivilegeEnum privilege,
                        @NonNull AccessOperation... operations) {
        this(new GraphQLQuery<>() {
                 @Override
                 public void prepare(ResourceProvider resources) {}

                 @Override
                 public @Nullable T execute(@Nullable S source,
                                            @NonNull ContextTransactionRequest context) throws PlatformException {
                     return function.apply(source);
                 }
             }, Operator.AND);
        this.with(privilege, operations);
    }

    public @NonNull GAccessQuery<S, T> with(@NonNull PrivilegeEnum privilege, @NonNull AccessOperation... operations) {
        this.privileges.add(new Pair(privilege.getUniqueKey(), new AccessOperationCollection(operations)));
        return this;
    }

    public @NonNull GraphQLQuery<S, T> getInnerQuery() {
        return query;
    }

    @Override
    public void prepare(@NonNull ResourceProvider resources) {
        this.query.prepare(resources);
    }

    @Override
    public @Nullable T execute(@Nullable S source, @NonNull ContextTransactionRequest context) throws PlatformException {
        UnauthorizedContext authContext = context.getSource().getAuthContext();
        if (authContext instanceof AuthorizedContext authorizedContext) {
            switch (operator) {
                case OR -> {
                    for (Pair privilege : privileges) {
                        if (authorizedContext
                                .getOperations(privilege.privilegeUniqueKey())
                                .contains(privilege.operations())) {
                            return this.query.execute(source, context);
                        }
                    }
                    throw GeneralExceptionBuilder.buildAccessDeniedException();
                }
                case AND -> {
                    for (Pair privilege : privileges) {
                        if (!authorizedContext
                                .getOperations(privilege.privilegeUniqueKey())
                                .contains(privilege.operations())) {
                            throw GeneralExceptionBuilder.buildAccessDeniedException();
                        }
                    }
                    return this.query.execute(source, context);
                }
            }
        }
        throw GeneralExceptionBuilder.buildAccessDeniedException();
    }

    private record Pair(@NonNull String privilegeUniqueKey, @NonNull AccessOperationCollection operations) { }
}
