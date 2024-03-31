package com.fuzzy.subsystems.function;

import com.fuzzy.platform.exception.PlatformException;

@FunctionalInterface
public interface BiConsumer<T, U> {

    /**
     * Performs this operation on the given arguments.
     *
     * @param t the first input argument
     * @param u the second input argument
     */
    void accept(T t, U u) throws PlatformException;
}
