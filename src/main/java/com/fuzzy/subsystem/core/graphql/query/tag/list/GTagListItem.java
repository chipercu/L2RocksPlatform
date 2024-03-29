package com.fuzzy.subsystem.core.graphql.query.tag.list;

import com.fuzzy.main.cluster.core.remote.struct.RemoteObject;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLDescription;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLField;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLTypeOutObject;
import com.fuzzy.main.platform.sdk.graphql.annotation.GraphQLAuthControl;
import com.fuzzy.subsystem.core.graphql.query.tag.GTag;
import com.fuzzy.subsystem.frontend.authcontext.AuthorizedContext;
import com.fuzzy.subsystems.list.RemoteListItem;

@GraphQLTypeOutObject("tag_list_item")
public class GTagListItem extends RemoteListItem<GTag> implements RemoteObject {

    private GTagListItem() {
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Элемент")
    public GTag getItem() {
        return super.getElement();
    }
}
