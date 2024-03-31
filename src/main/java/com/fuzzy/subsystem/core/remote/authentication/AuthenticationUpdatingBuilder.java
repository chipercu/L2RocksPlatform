package com.fuzzy.subsystem.core.remote.authentication;

import com.fuzzy.cluster.core.remote.struct.RemoteObject;
import com.fuzzy.cluster.graphql.struct.GOptional;

public class AuthenticationUpdatingBuilder implements RemoteObject {

    private transient GOptional<String> name = GOptional.notPresent();

    public void setName(String name) {
        this.name = GOptional.of(name);
    }

    public GOptional<String> getName() {
        return name;
    }
}
