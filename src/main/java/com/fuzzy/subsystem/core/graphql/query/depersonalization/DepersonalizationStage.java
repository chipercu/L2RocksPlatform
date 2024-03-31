package com.fuzzy.subsystem.core.graphql.query.depersonalization;

import com.fuzzy.cluster.core.remote.struct.RemoteObject;
import com.fuzzy.cluster.graphql.anotation.GraphQLTypeOutObject;

@GraphQLTypeOutObject("depersonalization_stage")
public enum DepersonalizationStage implements RemoteObject {
    IDLE, COPY, DEPERSONALIZATION, PACK
}
