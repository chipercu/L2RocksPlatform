package com.fuzzy.subsystem.entityprovidersdk.serialization.serializer;

import com.fuzzy.subsystem.entityprovidersdk.entity.DataContainer;
import com.fuzzy.subsystem.entityprovidersdk.entity.schema.SchemaField;

import java.io.IOException;
import java.util.List;

public interface Serializer<T extends DataContainer> {

    byte[] fillSchema(List<SchemaField> fields) throws IOException;

    byte[] serialize(T object, List<SchemaField> fields) throws IOException;
}
