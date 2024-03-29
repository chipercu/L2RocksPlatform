package com.fuzzy.subsystem.core.graphql.query.depersonalization;

import com.fuzzy.main.cluster.core.remote.struct.RemoteObject;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLTypeOutObject;

@GraphQLTypeOutObject("depersonalization_stage")
public enum DepersonalizationStage implements RemoteObject {
    IDLE, COPY, DEPERSONALIZATION, PACK
}
