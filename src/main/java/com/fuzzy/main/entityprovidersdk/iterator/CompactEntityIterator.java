package com.fuzzy.main.entityprovidersdk.iterator;

import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.entityprovidersdk.serialization.deserializer.CompactDeserializer;

import java.io.InputStream;
import java.io.Serializable;

public class CompactEntityIterator<T extends Serializable> extends EntityIterator<T> {

    public CompactEntityIterator(InputStream inputStream) throws PlatformException {
        super(inputStream, new CompactDeserializer<>(inputStream));
    }
}