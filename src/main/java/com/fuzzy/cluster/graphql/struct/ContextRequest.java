package com.fuzzy.cluster.graphql.struct;

import com.fuzzy.cluster.core.remote.struct.RemoteObject;
import com.fuzzy.cluster.graphql.struct.GRequest;

public interface ContextRequest extends RemoteObject {

    GRequest getRequest();
}
