package com.fuzzy.subsystem.core.graphql.query.authentication;

import com.fuzzy.cluster.core.remote.struct.RemoteObject;
import com.fuzzy.cluster.graphql.anotation.GraphQLField;
import com.fuzzy.cluster.graphql.anotation.GraphQLTypeOutObjectInterface;
import com.fuzzy.platform.sdk.graphql.annotation.GraphQLAuthControl;
import com.fuzzy.subsystem.frontend.authcontext.AuthorizedContext;

@GraphQLTypeOutObjectInterface("authentication_content")
public interface GAuthenticationContent extends RemoteObject {

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class})
    default String nothing() {
        return null;
    }
}