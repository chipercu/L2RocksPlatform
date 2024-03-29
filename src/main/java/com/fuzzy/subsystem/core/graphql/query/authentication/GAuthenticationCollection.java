package com.fuzzy.subsystem.core.graphql.query.authentication;

import com.infomaximum.cluster.graphql.anotation.GraphQLDescription;
import com.infomaximum.cluster.graphql.anotation.GraphQLField;
import com.infomaximum.cluster.graphql.anotation.GraphQLTypeOutObject;
import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.sdk.graphql.annotation.GraphQLAuthControl;
import com.fuzzy.subsystem.core.domainobject.authentication.AuthenticationReadable;
import com.fuzzy.subsystem.frontend.authcontext.AuthorizedContext;
import com.fuzzy.subsystems.collection.RemoteCollection;
import com.fuzzy.subsystems.sorter.Sorter;

import java.util.ArrayList;

@GraphQLTypeOutObject("authentication_collection")
public class GAuthenticationCollection extends RemoteCollection<AuthenticationReadable, GAuthentication> {

    public GAuthenticationCollection(Sorter<AuthenticationReadable> source) throws PlatformException {
        super(source, GAuthentication::new);
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Элементы")
    @Override
    public ArrayList<GAuthentication> getItems() {
        return super.getItems();
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Есть ли элементы за границей лимита")
    @Override
    public boolean hasNext() {
        return super.hasNext();
    }
}