package com.fuzzy.subsystems.graphql.out;

import com.infomaximum.cluster.core.remote.struct.RemoteObject;
import com.infomaximum.cluster.graphql.anotation.GraphQLDescription;
import com.infomaximum.cluster.graphql.anotation.GraphQLField;
import com.infomaximum.cluster.graphql.anotation.GraphQLTypeOutObject;
import com.infomaximum.platform.component.frontend.authcontext.UnauthorizedContext;
import com.infomaximum.platform.sdk.graphql.annotation.GraphQLAuthControl;
import com.fuzzy.subsystems.remote.RemovalData;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.stream.Collectors;

@GraphQLTypeOutObject("removal_data")
public class GRemovalData implements RemoteObject {

    private final HashSet<Long> removed;
    private final HashSet<Long> nonRemoved;
    private final ArrayList<GCauseForNonRemove> causesForNonRemove;

    public GRemovalData(HashSet<Long> removed, HashSet<Long> nonRemoved, ArrayList<GCauseForNonRemove> causesForNonRemove) {
        this.removed = removed;
        this.nonRemoved = nonRemoved;
        this.causesForNonRemove = causesForNonRemove;
    }

    public GRemovalData(RemovalData source) {
        this(source.getRemoved(), source.getNonRemoved(), source.getCausesForNonRemove().stream()
                .map(GCauseForNonRemove::new).collect(Collectors.toCollection(ArrayList::new)));
    }

    @GraphQLField
    @GraphQLAuthControl({ UnauthorizedContext.class })
    @GraphQLDescription("Идентификаторы удаленных объектов")
    public HashSet<Long> getRemoved() {
        return removed;
    }

    @GraphQLField
    @GraphQLAuthControl({ UnauthorizedContext.class })
    @GraphQLDescription("Идентификаторы неудаленных объектов")
    public HashSet<Long> getNonRemoved() {
        return nonRemoved;
    }

    @GraphQLField
    @GraphQLAuthControl({ UnauthorizedContext.class })
    @GraphQLDescription("Причины отказа удаления")
    public ArrayList<GCauseForNonRemove> getCausesForNonRemove() {
        return causesForNonRemove;
    }
}
