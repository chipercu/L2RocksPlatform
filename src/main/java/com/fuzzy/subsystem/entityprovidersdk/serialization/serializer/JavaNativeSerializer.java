package com.fuzzy.subsystem.entityprovidersdk.serialization.serializer;

import com.fuzzy.subsystem.entityprovidersdk.data.EntityContainer;
import com.fuzzy.subsystem.entityprovidersdk.entity.DataContainer;
import com.fuzzy.subsystem.entityprovidersdk.entity.schema.SchemaField;
import com.fuzzy.subsystem.entityprovidersdk.serialization.serializer.Serializer;
import org.springframework.lang.NonNull;
import org.springframework.util.ReflectionUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.List;

public class JavaNativeSerializer<T extends DataContainer> implements Serializer<T> {


    @Override
    public byte[] fillSchema(List<SchemaField> fields) throws IOException {
        return new byte[0];
    }

    @Override
    public byte[] serialize(@NonNull T object, List<SchemaField> fields) throws IOException {

        final List<Serializable> objectValues = fields.stream()
                .map(schemaField -> getValue(object, schemaField))
                .toList();
        return nativeSerialize(new EntityContainer(objectValues));
    }

    private Serializable getValue(T object, SchemaField field) {
        Method domainObjectMethod = field.getDomainObjectMethod();
        ReflectionUtils.makeAccessible(domainObjectMethod);
        return (Serializable) ReflectionUtils.invokeMethod(domainObjectMethod, object);
    }


    private byte[] nativeSerialize(EntityContainer container) throws IOException {

        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
        objectOutputStream.writeObject(container);
        objectOutputStream.close();
        return outputStream.toByteArray();
    }
}
