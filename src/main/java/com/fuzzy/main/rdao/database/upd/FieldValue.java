package com.fuzzy.main.rdao.database.upd;

import java.io.Serializable;
import java.util.Objects;

public class FieldValue<T extends Serializable> {

    private final String key;
    private final T value;

    public FieldValue(String key, T value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public T getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FieldValue<?> that = (FieldValue<?>) o;
        return key.equals(that.key) &&
                Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, value);
    }

    @Override
    public String toString() {
        return "FieldValue{" +
                "key='" + key + '\'' +
                ", value=" + value +
                '}';
    }
}
