package com.fuzzy.subsystem.core.graphql.query.depersonalization;

import com.fuzzy.cluster.core.remote.struct.RemoteObject;
import com.fuzzy.cluster.graphql.anotation.GraphQLDescription;
import com.fuzzy.cluster.graphql.anotation.GraphQLField;
import com.fuzzy.cluster.graphql.anotation.GraphQLTypeOutObject;
import com.fuzzy.platform.sdk.graphql.annotation.GraphQLAuthControl;
import com.fuzzy.subsystem.frontend.authcontext.AuthorizedContext;

@GraphQLTypeOutObject("depersonalization_status_result")
@GraphQLDescription("Статус процесса копирования обезличенной базы данных")
public class GDepersonalizationStatus implements RemoteObject {

    private final DepersonalizationStage stage;

    public GDepersonalizationStatus(DepersonalizationStage stage) {
        this.stage = stage;
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Стадия")
    public DepersonalizationStage getStage() {
        return stage;
    }
}
