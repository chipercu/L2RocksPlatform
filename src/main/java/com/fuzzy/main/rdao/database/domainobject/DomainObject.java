package com.fuzzy.main.rdao.database.domainobject;

import com.fuzzy.main.rdao.database.exception.FieldValueNotFoundException;
import com.fuzzy.main.rdao.database.exception.IllegalTypeException;
import com.fuzzy.main.rdao.database.schema.Field;
import com.fuzzy.main.rdao.database.schema.Schema;
import com.fuzzy.main.rdao.database.schema.StructEntity;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.time.Instant;

public abstract class DomainObject implements Serializable {

    private final long id;
    private boolean isJustCreated = false;
    private final Value<Serializable>[] loadedFieldValues;
    private Value<Serializable>[] newFieldValues = null;

    private transient StructEntity lazyStructEntity;

    public DomainObject(long id) {
        if (id < 1) {
            throw new IllegalArgumentException("id = " + id);
        }
        this.id = id;
        this.loadedFieldValues = new Value[getStructEntity().getFields().length];
    }

    public long getId() {
        return id;
    }

    public <T extends Serializable> T get(int fieldNumber) {
        Value<Serializable> value;
        if (newFieldValues == null || (value = newFieldValues[fieldNumber]) == null) {
            value = loadedFieldValues[fieldNumber];
        }

        if (value == null) {
            throw new FieldValueNotFoundException(getStructEntity().getField(fieldNumber).getName());
        }

        return (T) value.getValue();
    }

    protected void set(int fieldNumber, Serializable value) {
        if (newFieldValues == null) {
            newFieldValues = new Value[loadedFieldValues.length];
        }

        if (value != null) {
            Field field = getStructEntity().getField(fieldNumber);
            field.throwIfNotMatch(value.getClass());
        }

        newFieldValues[fieldNumber] = Value.of(value);
    }

    /**
     * Unsafe method. Do not use in external packages!
     */
    void _setLoadedField(int fieldNumber, Serializable value) {
        loadedFieldValues[fieldNumber] = Value.of(value);
    }

    /**
     * Unsafe method. Do not use in external packages!
     */
    boolean _isJustCreated() {
        return isJustCreated;
    }

    /**
     * Unsafe method. Do not use in external packages!
     */
    void _setAsJustCreated() {
        isJustCreated = true;
    }

    /**
     * Unsafe method. Do not use in external packages!
     */
    void _flushNewValues() {
        isJustCreated = false;
        if (newFieldValues == null) {
            return;
        }

        for (int i = 0; i < newFieldValues.length; ++i) {
            Value<Serializable> value = newFieldValues[i];
            if (value != null) {
                loadedFieldValues[i] = value;
            }
        }
        newFieldValues = null;
    }

    protected String getString(int fieldNumber) {
        return get(fieldNumber);
    }

    protected Integer getInteger(int fieldNumber) {
        return get(fieldNumber);
    }

    protected Long getLong(int fieldNumber) {
        return get(fieldNumber);
    }

    protected Double getDouble(int fieldNumber){
        return get(fieldNumber);
    }

    protected Instant getInstant(int fieldNumber) {
        return get(fieldNumber);
    }

    protected Boolean getBoolean(int fieldNumber) {
        return get(fieldNumber);
    }

    StructEntity getStructEntity() {
        if (lazyStructEntity == null) {
            lazyStructEntity = Schema.getEntity(this.getClass());
        }
        return lazyStructEntity;
    }

    Value<Serializable>[] getLoadedValues() {
        return loadedFieldValues;
    }

    Value<Serializable>[] getNewValues() {
        return newFieldValues;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof DomainObject)) return false;

        DomainObject that = (DomainObject) o;

        return getStructEntity() == that.getStructEntity() &&
               id == that.id;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

    @Override
    public String toString() {
        return getClass().getSuperclass().getName() + '(' +
                "id: " + id +
                ')';
    }

    public static <T extends DomainObject> Constructor<T> getConstructor(Class<T> clazz) {
        try {
            return clazz.getConstructor(long.class);
        } catch (ReflectiveOperationException e) {
            throw new IllegalTypeException(e);
        }
    }
}
