package com.fuzzy.subsystem.core.remote.authentication;

import com.fuzzy.main.cluster.core.remote.struct.RemoteObject;
import com.fuzzy.main.cluster.graphql.struct.GOptional;

public class AuthenticationUpdatingBuilder implements RemoteObject {

    private transient GOptional<String> name = GOptional.notPresent();

    public void setName(String name) {
        this.name = GOptional.of(name);
    }

    public GOptional<String> getName() {
        return name;
    }
}
