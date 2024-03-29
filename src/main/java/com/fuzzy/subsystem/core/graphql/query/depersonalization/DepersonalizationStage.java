package com.fuzzy.subsystem.core.graphql.query.depersonalization;

import com.infomaximum.cluster.core.remote.struct.RemoteObject;
import com.infomaximum.cluster.graphql.anotation.GraphQLTypeOutObject;

@GraphQLTypeOutObject("depersonalization_stage")
public enum DepersonalizationStage implements RemoteObject {
    IDLE, COPY, DEPERSONALIZATION, PACK
}
