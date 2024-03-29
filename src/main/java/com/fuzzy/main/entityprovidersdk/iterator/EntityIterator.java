package com.fuzzy.main.entityprovidersdk.iterator;

import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.sdk.exception.GeneralExceptionBuilder;
import com.fuzzy.main.entityprovidersdk.serialization.deserializer.Deserializer;
import org.springframework.lang.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

public class EntityIterator<T extends Serializable> implements BaseIterator<T> {


    private final Deserializer<T> deserializer;
    private final InputStream inputStream;
    private T currentState;

    public EntityIterator(@NonNull InputStream inputStream, Deserializer<T> deserializer) {
        this.inputStream = inputStream;
        this.deserializer = deserializer;
    }

    @Override
    public void close() throws PlatformException {
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
                throw GeneralExceptionBuilder.buildIOErrorException(e);
            }
        }
    }


    @Override
    public boolean hasNext() {
        return (currentState = deserializer.deserialize(inputStream)) != null;
    }

    @Override
    public T next() {
        return currentState;
    }
}