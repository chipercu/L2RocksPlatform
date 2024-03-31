package com.fuzzy.cluster.graphql.schema.struct.in;

import com.fuzzy.cluster.core.remote.struct.RemoteObject;

/**
 * Created by kris on 20.07.17.
 */
public class RGraphQLInputObjectTypeField implements RemoteObject {

    public final String type;

    public final String name;
    public final String externalName;

    public final boolean isNotNull;

    public RGraphQLInputObjectTypeField(String type, String name, String externalName, boolean isNotNull) {
        this.type = type;
        this.name = name;
        this.externalName = externalName;
        this.isNotNull = isNotNull;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RGraphQLInputObjectTypeField that = (RGraphQLInputObjectTypeField) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
