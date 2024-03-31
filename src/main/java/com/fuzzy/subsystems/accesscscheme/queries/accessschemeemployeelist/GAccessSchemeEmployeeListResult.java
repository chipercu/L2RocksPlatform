package com.fuzzy.subsystems.accesscscheme.queries.accessschemeemployeelist;

import com.fuzzy.cluster.core.remote.struct.RemoteObject;
import com.fuzzy.cluster.graphql.anotation.GraphQLDescription;
import com.fuzzy.cluster.graphql.anotation.GraphQLField;
import com.fuzzy.cluster.graphql.anotation.GraphQLTypeOutObject;
import com.fuzzy.platform.sdk.graphql.annotation.GraphQLAuthControl;
import com.fuzzy.subsystem.frontend.authcontext.AuthorizedContext;

import java.util.ArrayList;

@GraphQLTypeOutObject("access_scheme_employee_list_result")
public class GAccessSchemeEmployeeListResult implements RemoteObject {

    private final ArrayList<GAccessSchemeEmployeeListItem> items;
    private final int matchCount;
    private final boolean hasNext;

    public GAccessSchemeEmployeeListResult(ArrayList<GAccessSchemeEmployeeListItem> items,
                                           int matchCount,
                                           boolean hasNext) {
        this.items = items;
        this.matchCount = matchCount;
        this.hasNext = hasNext;
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Элементы")
    public ArrayList<GAccessSchemeEmployeeListItem> getItems() {
        return items;
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Количество элементов, соответствующих текстовому фильтру")
    public int getMatchCount() {
        return matchCount;
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Есть ли элементы за границей лимита")
    public boolean hasNext() {
        return hasNext;
    }
}