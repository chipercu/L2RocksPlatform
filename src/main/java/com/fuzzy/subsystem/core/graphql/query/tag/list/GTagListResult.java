package com.fuzzy.subsystem.core.graphql.query.tag.list;

import com.fuzzy.cluster.graphql.anotation.GraphQLDescription;
import com.fuzzy.cluster.graphql.anotation.GraphQLField;
import com.fuzzy.cluster.graphql.anotation.GraphQLTypeOutObject;
import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.sdk.graphql.annotation.GraphQLAuthControl;
import com.fuzzy.subsystem.core.domainobject.tag.TagReadable;
import com.fuzzy.subsystem.core.graphql.query.tag.GTag;
import com.fuzzy.subsystem.frontend.authcontext.AuthorizedContext;
import com.fuzzy.subsystems.list.ListResult;
import com.fuzzy.subsystems.list.RemoteListResult;

import java.util.ArrayList;

@GraphQLTypeOutObject("tag_list_result")
public class GTagListResult extends RemoteListResult<TagReadable, GTag, GTagListItem> {

    public GTagListResult(ListResult<TagReadable> source) throws PlatformException {
        super(source, GTag::new, GTagListItem.class);
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Элементы")
    public ArrayList<GTagListItem> getItems() {
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
