package com.fuzzy.subsystems.graphql.out;

import com.infomaximum.cluster.core.remote.struct.RemoteObject;
import com.infomaximum.cluster.graphql.anotation.GraphQLDescription;
import com.infomaximum.cluster.graphql.anotation.GraphQLField;
import com.infomaximum.cluster.graphql.anotation.GraphQLTypeOutObject;
import com.infomaximum.platform.component.frontend.authcontext.UnauthorizedContext;
import com.infomaximum.platform.sdk.graphql.annotation.GraphQLAuthControl;
import com.fuzzy.subsystems.remote.CauseForNonRemove;

import java.util.HashSet;

@GraphQLTypeOutObject("cause_for_non_remove")
public class GCauseForNonRemove implements RemoteObject {

    private final String cause;
    private final HashSet<Long> nonRemoved;

    public GCauseForNonRemove(String cause, HashSet<Long> nonRemoved) {
        this.cause = cause;
        this.nonRemoved = nonRemoved;
    }

    public GCauseForNonRemove(CauseForNonRemove source) {
        this(source.getCause(), source.getNonRemoved());
    }

    @GraphQLField
    @GraphQLAuthControl({ UnauthorizedContext.class })
    @GraphQLDescription("Причина отказа удаления")
    public String getCause() {
        return cause;
    }

    @GraphQLField
    @GraphQLAuthControl({ UnauthorizedContext.class })
    @GraphQLDescription("Идентификаторы неудаленных объектов")
    public HashSet<Long> getNonRemoved() {
        return nonRemoved;
    }
}
