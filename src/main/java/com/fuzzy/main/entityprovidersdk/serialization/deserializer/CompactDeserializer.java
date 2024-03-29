package com.fuzzy.main.entityprovidersdk.serialization.deserializer;

import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.sdk.exception.GeneralExceptionBuilder;
import com.fuzzy.main.entityprovidersdk.data.EntityContainer;
import com.fuzzy.main.entityprovidersdk.exception.runtime.DeserializerException;
import com.fuzzy.main.entityprovidersdk.serialization.schema.FieldSchema;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CompactDeserializer<T extends Serializable> implements Deserializer<T> {


    private final List<FieldSchema> fieldSchemas;
    private final CompactStreamDecoder decoder;


    public CompactDeserializer(InputStream stream) throws PlatformException {
        decoder = new CompactStreamDecoder(stream);
        fieldSchemas = fillSchema();
    }

    private List<FieldSchema> fillSchema() throws PlatformException {
        try {
            final ArrayList<Integer> header = decoder.readIntegerCollection();
            if (header != null) {
                return header.stream()
                        .map(FieldSchema::new)
                        .toList();
            }
        } catch (EOFException e) {
            return Collections.emptyList();
        } catch (IOException e) {
            throw GeneralExceptionBuilder.buildIOErrorException(e);
        }
        return Collections.emptyList();
    }

    @Override
    public T deserialize(InputStream stream) {

        List<Serializable> source;
        if (fieldSchemas.isEmpty()) {
            return null;
        }
        try {
            source = new ArrayList<>();
            for (FieldSchema field : fieldSchemas) {
                switch (field.getDataType()) {
                    case LONG -> {
                        if (field.isNullable()) {
                            source.add(decoder.readNullableLong());
                        } else {
                            source.add(decoder.readLong());
                        }
                    }
                    case INTEGER -> {
                        if (field.isNullable()) {
                            source.add(decoder.readNullableInt());
                        } else {
                            source.add(decoder.readInt());
                        }
                    }
                    case DOUBLE -> {
                        if (field.isNullable()) {
                            source.add(decoder.readNullableDouble());
                        } else {
                            source.add(decoder.readDouble());
                        }
                    }
                    case BOOLEAN -> {
                        if (field.isNullable()) {
                            source.add(decoder.readNullableBoolean());
                        } else {
                            source.add(decoder.readBoolean());
                        }
                    }
                    case STRING -> source.add(decoder.readNullableString());
                    case INSTANT -> source.add(decoder.readNullableInstant());
                    case LONG_ARRAY -> source.add(decoder.readLongCollection());
                    case INTEGER_ARRAY -> source.add(decoder.readIntegerCollection());
                    case STRING_ARRAY -> source.add(decoder.readStringCollection());
                    case DOUBLE_ARRAY -> source.add(decoder.readDoubleCollection());
                    case BOOLEAN_ARRAY -> source.add(decoder.readBooleanCollection());
                    case INSTANT_ARRAY -> source.add(decoder.readInstantCollection());
                    case BYTE_ARRAY -> source.add(decoder.readByteArray());
                    default -> throw new IllegalStateException("Unexpected value: " + field.getDataType());
                }
            }
        } catch (EOFException e) {
            return null;
        } catch (IOException e) {
            throw new DeserializerException(e);
        }
        return (T) new EntityContainer(source);
    }
}