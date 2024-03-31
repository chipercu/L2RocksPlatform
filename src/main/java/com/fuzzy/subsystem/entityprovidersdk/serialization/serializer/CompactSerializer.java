package com.fuzzy.subsystem.entityprovidersdk.serialization.serializer;

import com.fuzzy.subsystem.entityprovidersdk.entity.DataContainer;
import com.fuzzy.subsystem.entityprovidersdk.entity.schema.SchemaField;
import com.fuzzy.subsystem.entityprovidersdk.serialization.schema.FieldSchema;
import com.fuzzy.subsystem.entityprovidersdk.serialization.serializer.CompactStreamEncoder;
import com.fuzzy.subsystem.entityprovidersdk.serialization.serializer.Serializer;
import org.springframework.lang.NonNull;
import org.springframework.util.ReflectionUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.List;

public class CompactSerializer<T extends DataContainer> implements Serializer<T> {


    @Override
    public byte[] fillSchema(List<SchemaField> fields) throws IOException {
        List<Integer> fieldsTypeArray = fields.stream()
                .map(field -> new FieldSchema(fields.indexOf(field), field.getType(), field.isNullable()))
                .map(FieldSchema::getValue)
                .toList();
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        new CompactStreamEncoder(outputStream).writeIntegerCollection(fieldsTypeArray);
        return outputStream.toByteArray();
    }

    @Override
    public byte[] serialize(@NonNull T object, List<SchemaField> fields) throws IOException {

        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        final CompactStreamEncoder binaryEncoder = new CompactStreamEncoder(outputStream);

        for (SchemaField field : fields) {
            switch (field.getType()) {
                case LONG -> {
                    if (field.isNullable()) {
                        binaryEncoder.writeNullableLong(getValue(object, field));
                    } else {
                        binaryEncoder.writeLong(getValue(object, field));
                    }
                }
                case INTEGER -> {
                    if (field.isNullable()) {
                        binaryEncoder.writeNullableInt(getValue(object, field));
                    } else {
                        binaryEncoder.writeInt(getValue(object, field));
                    }
                }
                case DOUBLE -> {
                    final Double value = getValue(object, field);
                    if (field.isNullable()) {
                        binaryEncoder.writeNullableDouble(value);
                    } else {
                        binaryEncoder.writeDouble(value);
                    }
                }
                case BOOLEAN -> {
                    final Boolean value = getValue(object, field);
                    if (field.isNullable()) {
                        binaryEncoder.writeNullableBoolean(value);
                    } else {
                        binaryEncoder.writeBoolean(value);
                    }
                }
                case STRING -> binaryEncoder.writeNullableString(getValue(object, field));
                case INSTANT -> binaryEncoder.writeNullableInstant(getValue(object, field));
                case LONG_ARRAY -> binaryEncoder.writeLongCollection(getValue(object, field));
                case INTEGER_ARRAY -> binaryEncoder.writeIntegerCollection(getValue(object, field));
                case STRING_ARRAY -> binaryEncoder.writeStringCollection(getValue(object, field));
                case DOUBLE_ARRAY -> binaryEncoder.writeDoubleCollection(getValue(object, field));
                case BOOLEAN_ARRAY -> binaryEncoder.writeBooleanCollection(getValue(object, field));
                case INSTANT_ARRAY -> binaryEncoder.writeInstantCollection(getValue(object, field));
                case BYTE_ARRAY -> binaryEncoder.writeByteArray(getValue(object, field));
                default -> throw new IllegalStateException("Unexpected value: " + field.getType());
            }
        }
        return outputStream.toByteArray();
    }

    private <R extends Serializable> R getValue(T object, SchemaField field) {
        Method domainObjectMethod = field.getDomainObjectMethod();
        ReflectionUtils.makeAccessible(domainObjectMethod);
        return (R) ReflectionUtils.invokeMethod(domainObjectMethod, object);
    }
}
