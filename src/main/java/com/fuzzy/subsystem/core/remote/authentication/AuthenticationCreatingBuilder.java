package com.fuzzy.subsystem.core.remote.authentication;

import com.fuzzy.main.cluster.core.remote.struct.RemoteObject;

public class AuthenticationCreatingBuilder implements RemoteObject {

    private final String name;
    private final String type;

    public AuthenticationCreatingBuilder(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }
}
