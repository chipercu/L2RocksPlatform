package com.fuzzy.cluster.graphql.schema.struct;

import com.fuzzy.cluster.core.remote.struct.RemoteObject;


/**
 * Created by kris on 29.12.16.
 */
public abstract class RGraphQLType implements RemoteObject {

    private final String name;
    private final String description;

    public RGraphQLType(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
