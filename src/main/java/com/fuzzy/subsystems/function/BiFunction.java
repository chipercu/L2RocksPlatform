package com.fuzzy.subsystems.function;

import com.fuzzy.main.platform.exception.PlatformException;

@FunctionalInterface
public interface BiFunction<T, U, R> {
    /**
     * Applies this function to the given arguments.
     *
     * @param t the first function argument
     * @param u the second function argument
     * @return the function result
     */
    R apply(T t, U u) throws PlatformException;
}