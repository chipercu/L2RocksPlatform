package com.fuzzy.main.entityprovidersdk.serialization.serializer;

import com.fuzzy.main.entityprovidersdk.entity.DataContainer;
import com.fuzzy.main.entityprovidersdk.entity.schema.SchemaField;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class EntityOutputStreamWriter implements AutoCloseable {

    private final ByteArrayOutputStream stream;
    private final Serializer<DataContainer> serializer;
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