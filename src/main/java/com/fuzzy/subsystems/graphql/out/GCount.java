package com.fuzzy.subsystems.graphql.out;

import com.fuzzy.main.cluster.core.remote.struct.RemoteObject;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLDescription;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLField;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLTypeOutObject;
import com.fuzzy.main.platform.component.frontend.authcontext.UnauthorizedContext;
import com.fuzzy.main.platform.sdk.graphql.annotation.GraphQLAuthControl;

@GraphQLTypeOutObject("count")
public class GCount implements RemoteObject {

    private final int count;
    private final int total;

    public GCount(int count, int total) {
        this.count = count;
        this.total = total;
    }

    @GraphQLField
    @GraphQLAuthControl({ UnauthorizedContext.class })
    @GraphQLDescription("Количество")
    public int getCount() {
        return count;
    }

    @GraphQLField
    @GraphQLAuthControl({ UnauthorizedContext.class })
    @GraphQLDescription("Всего")
    public int getTotal() {
        return total;
    }
}
