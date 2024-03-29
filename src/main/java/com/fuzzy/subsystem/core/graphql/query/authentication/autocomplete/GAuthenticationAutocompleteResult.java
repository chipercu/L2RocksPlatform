package com.fuzzy.subsystem.core.graphql.query.authentication.autocomplete;

import com.fuzzy.main.cluster.graphql.anotation.GraphQLDescription;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLField;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLTypeOutObject;
import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.sdk.graphql.annotation.GraphQLAuthControl;
import com.fuzzy.subsystem.core.domainobject.authentication.AuthenticationReadable;
import com.fuzzy.subsystem.core.graphql.query.authentication.GAuthentication;
import com.fuzzy.subsystem.frontend.authcontext.AuthorizedContext;
import com.fuzzy.subsystems.autocomplete.LightAutocompleteResult;
import com.fuzzy.subsystems.struct.RemoteRDomainObjectCollectionResult;

import java.util.ArrayList;

@GraphQLTypeOutObject("authentication_autocomplete_result")
public class GAuthenticationAutocompleteResult
        extends RemoteRDomainObjectCollectionResult<AuthenticationReadable, GAuthentication> {

    public GAuthenticationAutocompleteResult(LightAutocompleteResult<AuthenticationReadable> source) throws PlatformException {
        super(GAuthentication::new, source);
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Элементы")
    public ArrayList<GAuthentication> getItems() {
        return super.getItems();
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Есть ли элементы за границей лимита")
    public boolean hasNext() {
        return super.hasNext();
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Количество элементов, соответствующих текстовому фильтру")
    public int getMatchCount() {
        return super.getMatchCount();
    }
}