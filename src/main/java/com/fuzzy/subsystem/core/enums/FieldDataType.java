package com.fuzzy.subsystem.core.enums;

import com.infomaximum.cluster.core.remote.struct.RemoteObject;
import com.infomaximum.cluster.graphql.anotation.GraphQLTypeOutObject;
import com.infomaximum.database.utils.BaseEnum;

@GraphQLTypeOutObject("field_data_type")
public enum FieldDataType implements RemoteObject, BaseEnum {

    STRING(1),
    STRING_ARRAY(2),
    LONG(3),
    LONG_ARRAY(4),
    DATE(5),
    DATE_ARRAY(6),
    DATETIME(7),
    DATETIME_ARRAY(8),
    ID(9);

    private final int id;

    FieldDataType(int id) {
        this.id = id;
    }

    @Override
    public int intValue() {
        return id;
    }

    public static FieldDataType get(int id) {
        for (FieldDataType value : FieldDataType.values()) {
            if (value.intValue() == id) {
                return value;
            }
        }
        return null;
    }
}
