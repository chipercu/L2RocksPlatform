package com.fuzzy.main.rdao.database.domainobject;

import java.io.Serializable;

public class Value<T extends Serializable> implements Serializable {

    private static final Value<? extends Serializable> EMPTY = new Value<>(null);

    private final T value;

    private Value(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    public static <U extends Serializable> Value<U> empty() {
        return (Value<U>) EMPTY;
    }

    public static <U extends Serializable> Value<U> of(U value) {
        return value != null ? new Value<>(value) : empty();
    }
}
