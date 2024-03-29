package com.fuzzy.subsystem.core.graphql.query.authentication;

import com.fuzzy.main.cluster.core.remote.struct.RemoteObject;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLField;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLTypeOutObjectInterface;
import com.fuzzy.main.platform.sdk.graphql.annotation.GraphQLAuthControl;
import com.fuzzy.subsystem.frontend.authcontext.AuthorizedContext;

@GraphQLTypeOutObjectInterface("authentication_content")
public interface GAuthenticationContent extends RemoteObject {

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class})
    default String nothing() {
        return null;
    }
}