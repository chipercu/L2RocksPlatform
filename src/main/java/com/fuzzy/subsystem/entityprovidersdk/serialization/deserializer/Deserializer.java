package com.fuzzy.subsystem.entityprovidersdk.serialization.deserializer;

import java.io.InputStream;
import java.io.Serializable;

public interface Deserializer<T extends Serializable> {

    T deserialize(InputStream stream);

}