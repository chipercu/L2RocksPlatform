package com.fuzzy.subsystem.entityprovidersdk.iterator;

import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.subsystem.entityprovidersdk.iterator.EntityIterator;
import com.fuzzy.subsystem.entityprovidersdk.serialization.deserializer.CompactDeserializer;

import java.io.InputStream;
import java.io.Serializable;

public class CompactEntityIterator<T extends Serializable> extends EntityIterator<T> {

    public CompactEntityIterator(InputStream inputStream) throws PlatformException {
        super(inputStream, new CompactDeserializer<>(inputStream));
    }
}