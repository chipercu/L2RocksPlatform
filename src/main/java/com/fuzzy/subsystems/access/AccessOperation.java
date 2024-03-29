package com.fuzzy.subsystems.access;

import com.infomaximum.cluster.graphql.anotation.GraphQLTypeOutObject;
import com.infomaximum.database.utils.BaseEnum;

@GraphQLTypeOutObject("access_operation")
public enum AccessOperation implements BaseEnum {

    READ(1),
    WRITE(2),
    CREATE(4),
    DELETE(8),
    EXECUTE(16);

    private final int id;

    AccessOperation(int id) {
        this.id = id;
    }

    public int intValue() {
        return id;
    }

    public static AccessOperation valueOf(int value) {
        for (AccessOperation operation : AccessOperation.values()) {
            if (operation.intValue() == value) {
                return operation;
            }
        }
        return null;
    }
}
