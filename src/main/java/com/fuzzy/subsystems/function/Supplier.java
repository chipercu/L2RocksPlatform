package com.fuzzy.subsystems.function;

import com.fuzzy.platform.exception.PlatformException;

@FunctionalInterface
public interface Supplier<T> {

    /**
     * Gets a result.
     *
     * @return a result
     */
    T get() throws PlatformException;
}
