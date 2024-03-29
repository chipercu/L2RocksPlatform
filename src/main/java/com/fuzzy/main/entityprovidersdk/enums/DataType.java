package com.fuzzy.main.entityprovidersdk.enums;


import com.fuzzy.main.cluster.core.remote.struct.RemoteObject;
import com.fuzzy.main.rdao.database.utils.BaseEnum;

public enum DataType implements RemoteObject, BaseEnum {

    STRING(1),
    INTEGER(2),
    LONG(3),
    BOOLEAN(4),
    DOUBLE(5),
    INSTANT(6),
    STRING_ARRAY(7),
    INTEGER_ARRAY(8),
    LONG_ARRAY(9),
    BOOLEAN_ARRAY(10),
    DOUBLE_ARRAY(11),
    INSTANT_ARRAY(12),
    BYTE_ARRAY(13);

    private final int id;

    DataType(int id) {
        this.id = id;
    }

    @Override
    public int intValue() {
        return id;
    }

    public static DataType valueOf(Integer id) {
        if (id != null) {
            for (DataType variableType : DataType.values()) {
                if (variableType.intValue() == id) {
                    return variableType;
                }
            }
        }
        throw new IllegalArgumentException("not found DataType by id:%s".formatted(id));
    }
}
