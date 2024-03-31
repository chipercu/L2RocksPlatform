package com.fuzzy.subsystem.entityprovidersdk.iterator;

import com.fuzzy.subsystem.entityprovidersdk.iterator.EntityIterator;
import com.fuzzy.subsystem.entityprovidersdk.serialization.deserializer.JavaNativeDeserializer;

import java.io.InputStream;
import java.io.Serializable;

public class NativeEntityIterator<T extends Serializable> extends EntityIterator<T> {

    public NativeEntityIterator(InputStream inputStream) {
        super(inputStream, new JavaNativeDeserializer<>());
    }
}