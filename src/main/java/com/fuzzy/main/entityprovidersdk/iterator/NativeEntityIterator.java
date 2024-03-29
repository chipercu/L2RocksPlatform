package com.fuzzy.main.entityprovidersdk.iterator;

import com.fuzzy.main.entityprovidersdk.serialization.deserializer.JavaNativeDeserializer;

import java.io.InputStream;
import java.io.Serializable;

public class NativeEntityIterator<T extends Serializable> extends EntityIterator<T> {

    public NativeEntityIterator(InputStream inputStream) {
        super(inputStream, new JavaNativeDeserializer<>());
    }
}