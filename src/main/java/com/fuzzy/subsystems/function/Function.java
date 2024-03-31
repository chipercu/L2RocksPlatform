package com.fuzzy.subsystems.function;

import com.fuzzy.platform.exception.PlatformException;

@FunctionalInterface
public interface Function<T, R> {

    /**
     * Applies this function to the given argument.
     *
     * @param t the function argument
     * @return the function result
     */
    R apply(T t) throws PlatformException;
}
