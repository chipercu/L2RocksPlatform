package com.fuzzy.subsystems.accesscscheme.queries.accessschemeemployeelist;

import com.fuzzy.main.cluster.core.remote.struct.RemoteObject;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLDescription;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLField;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLTypeOutObject;
import com.fuzzy.main.platform.sdk.graphql.annotation.GraphQLAuthControl;
import com.fuzzy.subsystem.frontend.authcontext.AuthorizedContext;

import java.util.ArrayList;

@GraphQLTypeOutObject("access_scheme_employee_list_result")
public class GAccessSchemeEmployeeListResult implements RemoteObject {

    private final ArrayList<GAccessSchemeEmployeeListItem> items;
    private final int matchCount;
    private final int nextCount;

    public GAccessSchemeEmployeeListResult(ArrayList<GAccessSchemeEmployeeListItem> items,
                                           int matchCount,
                                           int nextCount) {
        this.items = items;
        this.matchCount = matchCount;
        this.nextCount = nextCount;
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
    @GraphQLDescription("Количество оставшихся доступных элементов")
    public int getNextCount() {
        return nextCount;
    }
}