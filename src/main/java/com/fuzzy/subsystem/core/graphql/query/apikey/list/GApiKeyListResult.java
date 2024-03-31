package com.fuzzy.subsystem.core.graphql.query.apikey.list;

import com.fuzzy.cluster.graphql.anotation.GraphQLDescription;
import com.fuzzy.cluster.graphql.anotation.GraphQLField;
import com.fuzzy.cluster.graphql.anotation.GraphQLTypeOutObject;
import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.sdk.graphql.annotation.GraphQLAuthControl;
import com.fuzzy.subsystem.core.domainobject.apikey.ApiKeyReadable;
import com.fuzzy.subsystem.core.graphql.query.apikey.GApiKey;
import com.fuzzy.subsystem.frontend.authcontext.AuthorizedContext;
import com.fuzzy.subsystems.list.ListResult;
import com.fuzzy.subsystems.list.RemoteListResult;

import java.util.ArrayList;

@GraphQLTypeOutObject("api_key_list_result")
public class GApiKeyListResult extends RemoteListResult<ApiKeyReadable, GApiKey, GApiKeyListItem> {

    public GApiKeyListResult(ListResult<ApiKeyReadable> source) throws PlatformException {
        super(source, GApiKey::new, GApiKeyListItem.class);
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Элементы")
    public ArrayList<GApiKeyListItem> getItems() {
        return super.getItems();
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Есть ли элементы за границей лимита")
    public boolean hasNext() {
        return super.hasNext();
    }
}
