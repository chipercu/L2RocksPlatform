package com.fuzzy.subsystems.accesscscheme;

import com.fuzzy.cluster.graphql.anotation.GraphQLDescription;
import com.fuzzy.cluster.graphql.anotation.GraphQLField;
import com.fuzzy.cluster.graphql.anotation.GraphQLTypeOutObject;
import com.fuzzy.platform.sdk.graphql.annotation.GraphQLAuthControl;
import com.fuzzy.subsystem.frontend.authcontext.AuthorizedContext;
import org.checkerframework.checker.nullness.qual.NonNull;

@GraphQLTypeOutObject("object_access_scheme_operation")
public class GAccessSchemeStandardOperation implements GAccessSchemeOperation {

    private final StandardAccessOperation operation;

    public GAccessSchemeStandardOperation(@NonNull StandardAccessOperation operation) {
        this.operation = operation;
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Операция")
    public StandardAccessOperation getOperation() {
        return operation;
    }

    @Override
    public String getLocKey() {
        return operation.getLocKey();
    }
}
