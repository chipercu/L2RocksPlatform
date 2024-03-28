package com.fuzzy.subsystems.accesscscheme;

import com.fuzzy.main.cluster.core.remote.struct.RemoteObject;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLField;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLTypeOutObjectInterface;
import com.fuzzy.main.platform.sdk.graphql.annotation.GraphQLAuthControl;
import com.fuzzy.subsystem.frontend.authcontext.AuthorizedContext;
import com.fuzzy.subsystems.remote.Localizable;

@GraphQLTypeOutObjectInterface("access_scheme_operation")
public interface GAccessSchemeOperation extends RemoteObject, Localizable {

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class})
    default String nothing() {
        return null;
    }
}
