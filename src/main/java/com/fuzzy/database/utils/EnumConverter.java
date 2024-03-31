package com.fuzzy.database.utils;

import com.google.common.primitives.UnsignedInts;
import com.fuzzy.database.schema.TypeConverter;
import com.fuzzy.database.utils.BaseEnum;
import com.fuzzy.database.utils.ByteUtils;
import com.fuzzy.database.utils.TypeConvert;

public abstract class EnumConverter<T extends Enum<?> & BaseEnum> implements TypeConverter<T> {

    private final T[] enumConstants;

    protected EnumConverter(Class<T> clazz) {
        this.enumConstants = clazz.getEnumConstants();
    }

    @Override
    public byte[] pack(T value) {
        return value != null ? com.fuzzy.database.utils.TypeConvert.pack(value.intValue()) : com.fuzzy.database.utils.TypeConvert.EMPTY_BYTE_ARRAY;
    }

    @Override
    public T unpack(byte[] value) {
        if (ByteUtils.isNullOrEmpty(value)) {
            return null;
        }

        int enumValue = TypeConvert.unpackInt(value);
        for(T e : enumConstants) {
            if(enumValue == e.intValue()) {
                return e;
            }
        }
        throw new RuntimeException("Not found enum value " + enumValue + " into " + getClass());
    }

    @Override
    public long buildHash(T value) {
        return value != null ? UnsignedInts.toLong(value.intValue()) : 0;
    }
}
