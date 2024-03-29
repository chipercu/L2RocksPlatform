package com.fuzzy.subsystems.remote;

import com.infomaximum.cluster.core.remote.struct.RemoteObject;

import java.io.Serializable;
import java.util.Objects;

public class Optional<T extends Serializable> implements RemoteObject {

    private final T value;
    private final boolean isPresent;

    private Optional(T value, boolean isPresent) {
        this.value = value;
        this.isPresent = isPresent;
    }

    public T get() {
        return value;
    }

    public boolean isPresent() {
        return isPresent;
    }

    public static <T extends Serializable> Optional<T> notPresent() {
        return new Optional<>(null, false);
    }

    public static <T extends Serializable> Optional<T> of(T value) {
        return new Optional<>(value, true);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Optional<?> optional = (Optional<?>) o;
        return isPresent == optional.isPresent &&
                Objects.equals(value, optional.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, isPresent);
    }
}