package com.fuzzy.cluster.graphql.struct;

/**
 * Created by kris on 06.03.17.
 */
public final class GOptional<T> {

    private static final GOptional NOT_PRESENT = new GOptional(null, false);

    private final T value;
    private final boolean isPresent;

    public GOptional(T value, boolean isPresent) {
        this.value = value;
        this.isPresent = isPresent;
    }

    public T get() {
        return value;
    }

    public boolean isPresent() {
        return isPresent;
    }

    @SuppressWarnings("unchecked")
    public static final <T> GOptional<T> notPresent() {
        return (GOptional<T>) NOT_PRESENT;
    }

    public static final <T> GOptional<T> of(T value) {
        return new GOptional(value, true);
    }
}
