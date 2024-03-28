package com.fuzzy.main.rdao.database.utils;

import com.fuzzy.main.rdao.database.schema.TypeConverter;
import com.google.common.primitives.UnsignedInts;

public abstract class EnumConverter<T extends Enum<?> & BaseEnum> implements TypeConverter<T> {

    private final T[] enumConstants;

    protected EnumConverter(Class<T> clazz) {
        this.enumConstants = clazz.getEnumConstants();
    }

    @Override
    public byte[] pack(T value) {
        return value != null ? TypeConvert.pack(value.intValue()) : TypeConvert.EMPTY_BYTE_ARRAY;
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
