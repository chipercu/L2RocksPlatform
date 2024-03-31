package com.fuzzy.cluster.graphql.schema.struct.out;

import com.fuzzy.cluster.core.remote.struct.RemoteObject;

/**
 * Created by kris on 29.12.16.
 */
public class RGraphQLObjectTypeMethodArgument implements RemoteObject {

    public final String type;
    public final String name;
    public final String externalName;
    public final boolean isNotNull;
    public final String description;

    public RGraphQLObjectTypeMethodArgument(String type, String name, String externalName, boolean isNotNull, String description) {
        this.type = type;
        this.name = name;
        this.externalName = externalName;
        this.isNotNull = isNotNull;
        this.description = description;
    }
}
