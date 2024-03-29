package com.fuzzy.subsystem.core.graphql.query.apikey.list;

import com.fuzzy.main.cluster.core.remote.struct.RemoteObject;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLDescription;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLField;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLTypeOutObject;
import com.fuzzy.main.platform.sdk.graphql.annotation.GraphQLAuthControl;
import com.fuzzy.subsystem.core.graphql.query.apikey.GApiKey;
import com.fuzzy.subsystem.frontend.authcontext.AuthorizedContext;
import com.fuzzy.subsystems.list.RemoteListItem;

@GraphQLTypeOutObject("api_key_list_element")
public class GApiKeyListItem extends RemoteListItem<GApiKey> implements RemoteObject {

    private GApiKeyListItem() {
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Элемент")
    public GApiKey getElement() {
        return super.getElement();
    }
}
