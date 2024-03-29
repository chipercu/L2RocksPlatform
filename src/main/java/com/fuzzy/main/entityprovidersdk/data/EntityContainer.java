package com.fuzzy.main.entityprovidersdk.data;

import com.fuzzy.main.cluster.core.remote.struct.RemoteObject;
import com.fuzzy.main.rdao.database.domainobject.Value;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class EntityContainer implements RemoteObject {

    private final Value<Serializable>[] fields;

    public EntityContainer(List<Serializable> source) {
        this.fields = source.stream()
                .map(Value::of)
                .toArray(Value[]::new);
    }


    public <T extends Serializable> T get(int fieldNumber) {
        checkBound(fieldNumber);
        Value<Serializable> value = fields[fieldNumber];
        return (T) value.getValue();
    }

    public Value<Serializable>[] getFields() {
        return fields;
    }

    private void checkBound(int fieldNumber) {
        if (fieldNumber < 0 || fieldNumber >= fields.length) {
            throw new IndexOutOfBoundsException("Invalid field number: " + fieldNumber);
        }
    }

    @Override
    public String toString() {
        return "EntityContainer{" +
                "fields=" + Arrays.toString(fields) +
                '}';
    }
}
