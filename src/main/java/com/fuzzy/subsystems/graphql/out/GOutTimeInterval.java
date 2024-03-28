package com.fuzzy.subsystems.graphql.out;

import com.fuzzy.main.cluster.core.remote.struct.RemoteObject;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLDescription;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLField;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLTypeOutObject;
import com.fuzzy.main.platform.sdk.graphql.annotation.GraphQLAuthControl;
import com.fuzzy.subsystem.frontend.authcontext.AuthorizedContext;

@GraphQLTypeOutObject("out_time_interval")
public class GOutTimeInterval implements RemoteObject {

    private GOutTime begin;
    private GOutTime end;

    public GOutTimeInterval(GOutTime begin, GOutTime end) {
        this.begin = begin;
        this.end = end;
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Начало")
    public GOutTime getBegin() {
        return begin;
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Окончание")
    public GOutTime getEnd() {
        return end;
    }
}
