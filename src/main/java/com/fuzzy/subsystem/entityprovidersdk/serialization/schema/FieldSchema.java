package com.fuzzy.subsystem.entityprovidersdk.serialization.schema;

import com.fuzzy.subsystem.entityprovidersdk.enums.DataType;

public class FieldSchema {
    private final int number;
    private final DataType dataType;
    private final int value;
    private final boolean isNullable;

    public FieldSchema(int number, DataType dataType, boolean isNullable) {
        this.number = number;
        this.dataType = dataType;
        this.isNullable = isNullable;
        this.value = toInteger(number, dataType, isNullable);
    }


    public FieldSchema(int value) {
        this.value = value;
        this.number = numFromInt(value);
        this.dataType = dataTypeFromInt(value);
        this.isNullable = isNullableFromInt(value);
    }


    public int getNumber() {
        return number;
    }

    public DataType getDataType() {
        return dataType;
    }

    public int getValue() {
        return value;
    }

    public boolean isNullable() {
        return isNullable;
    }

    private int toInteger(int number, DataType dataType, boolean isNullable) {
        int numberValue = number & 0xFF;
        int dataTypeValue = dataType.intValue() & 0xFF;
        int isNullableValue = (isNullable ? 1 : 0) & 0xFF;
        return (numberValue << 16) | (dataTypeValue << 8) | isNullableValue;
    }

    private int numFromInt(int value) {
        return value >> 16 & 0xFF;
    }

    private DataType dataTypeFromInt(int value) {
        int dataTypeValue = value >> 8 & 0xFF;
        return DataType.valueOf(dataTypeValue);
    }

    private boolean isNullableFromInt(int value) {
        int intValue = value & 0xFF;
        return intValue == 1;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FieldSchema that = (FieldSchema) o;

        if (number != that.number) return false;
        if (value != that.value) return false;
        if (isNullable != that.isNullable) return false;
        return dataType == that.dataType;
    }

    @Override
    public int hashCode() {
        int result = number;
        result = 31 * result + (dataType != null ? dataType.hashCode() : 0);
        result = 31 * result + value;
        result = 31 * result + (isNullable ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "FieldSchema{" +
                "number=" + number +
                ", dataType=" + dataType +
                ", value=" + value +
                ", isNullable=" + isNullable +
                '}';
    }
}