package com.fuzzy.subsystems.accesscscheme;

import com.infomaximum.cluster.core.remote.struct.RemoteObject;
import com.infomaximum.cluster.graphql.anotation.GraphQLTypeOutObject;
import com.infomaximum.database.utils.BaseEnum;
import com.fuzzy.subsystems.remote.Localizable;

@GraphQLTypeOutObject("object_access_operation")
public enum StandardAccessOperation implements RemoteObject, BaseEnum, Localizable {

    READ(1, "object_access_operation.read"),

    WRITE(2, "object_access_operation.write");

    private final int id;
    private final String locKey;

    StandardAccessOperation(int id, String locKey) {
        this.id = id;
        this.locKey = locKey;
    }

    @Override
    public int intValue() {
        return id;
    }

    @Override
    public String getLocKey() {
        return locKey;
    }

    public static StandardAccessOperation get(int id) {
        for (StandardAccessOperation operation : StandardAccessOperation.values()) {
            if (operation.intValue() == id) {
                return operation;
            }
        }
        return null;
    }
}