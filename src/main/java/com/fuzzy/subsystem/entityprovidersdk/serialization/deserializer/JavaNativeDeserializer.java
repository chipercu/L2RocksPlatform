package com.fuzzy.subsystem.entityprovidersdk.serialization.deserializer;

import com.fuzzy.subsystem.entityprovidersdk.serialization.deserializer.Deserializer;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;

public class JavaNativeDeserializer<T extends Serializable> implements Deserializer<T> {

    @Override
    public T deserialize(InputStream stream) {
        try (ObjectInputStream dataInputStream = new ObjectInputStream(stream)) {
            return (T) dataInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return null;
        }
    }
}