package com.fuzzy.subsystem.entityprovidersdk.serialization.serializer;

import com.fuzzy.subsystem.entityprovidersdk.entity.DataContainer;
import com.fuzzy.subsystem.entityprovidersdk.entity.schema.SchemaField;
import com.fuzzy.subsystem.entityprovidersdk.serialization.serializer.Serializer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class EntityOutputStreamWriter implements AutoCloseable {

    private final ByteArrayOutputStream stream;
    private final com.fuzzy.subsystem.entityprovidersdk.serialization.serializer.Serializer<DataContainer> serializer;
    private final List<SchemaField> requiredFields;

    public EntityOutputStreamWriter(Serializer<DataContainer> serializer, List<SchemaField> requiredFields) {
        this.stream = new ByteArrayOutputStream();
        this.serializer = serializer;
        this.requiredFields = requiredFields;
    }

    public void fillSchema() throws IOException {
        this.stream.write(serializer.fillSchema(requiredFields));
    }

    public void write(DataContainer container) throws IOException {
        this.stream.write(serializer.serialize(container, requiredFields));
    }

    @Override
    public void close() throws IOException {
        stream.flush();
        stream.close();
    }

    public byte[] getByteArray() {
        return stream.toByteArray();
    }
}