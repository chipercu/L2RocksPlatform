package com.fuzzy.database.schema;

public interface TypeConverter<T> {

    byte[] pack(T value);
    T unpack(byte[] value);
    long buildHash(T value);
}
