package com.fuzzy.subsystems.function;

import com.fuzzy.platform.exception.PlatformException;

@FunctionalInterface
public interface TripleFunction<T, U, S, R> {
    /**
     * Applies this function to the given arguments.
     *
     * @param t the first function argument
     * @param u the second function argument
     * @return the function result
     */
    R apply(T t, U u, S s) throws PlatformException;
}