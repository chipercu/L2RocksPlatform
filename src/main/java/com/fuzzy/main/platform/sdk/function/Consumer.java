package com.fuzzy.main.platform.sdk.function;

import com.fuzzy.main.platform.exception.PlatformException;

@FunctionalInterface
public interface Consumer<T> {

    /**
     * Performs this operation on the given argument.
     *
     * @param t the input argument
     */
    void accept(T t) throws PlatformException;
}