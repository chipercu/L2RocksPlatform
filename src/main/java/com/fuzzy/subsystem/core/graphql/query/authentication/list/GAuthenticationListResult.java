package com.fuzzy.subsystem.core.graphql.query.authentication.list;

import com.fuzzy.main.cluster.graphql.anotation.GraphQLDescription;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLField;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLTypeOutObject;
import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.sdk.graphql.annotation.GraphQLAuthControl;
import com.fuzzy.subsystem.core.domainobject.authentication.AuthenticationReadable;
import com.fuzzy.subsystem.core.graphql.query.authentication.GAuthentication;
import com.fuzzy.subsystem.frontend.authcontext.AuthorizedContext;
import com.fuzzy.subsystems.list.ListResult;
import com.fuzzy.subsystems.list.RemoteListResult;

import java.util.ArrayList;

@GraphQLTypeOutObject("authentication_list_result")
public class GAuthenticationListResult extends RemoteListResult<AuthenticationReadable, GAuthentication, GAuthenticationListItem> {

    public GAuthenticationListResult(ListResult<AuthenticationReadable> source) throws PlatformException {
        super(source, GAuthentication::new, GAuthenticationListItem.class);
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Элементы")
    public ArrayList<GAuthenticationListItem> getItems() {
        return super.getItems();
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Количество элементов, соответствующих текстовому фильтру")
    public int getMatchCount() {
        return super.getMatchCount();
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Есть ли элементы за границей лимита")
    public boolean hasNext() {
        return super.hasNext();
    }
}