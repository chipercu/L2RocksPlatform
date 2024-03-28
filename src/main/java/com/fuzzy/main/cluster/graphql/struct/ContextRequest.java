package com.fuzzy.main.cluster.graphql.struct;

import com.fuzzy.main.cluster.core.remote.struct.RemoteObject;

public interface ContextRequest extends RemoteObject {

    GRequest getRequest();
}
