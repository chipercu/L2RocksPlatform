package com.fuzzy.subsystems.function;

import com.infomaximum.platform.exception.PlatformException;

@FunctionalInterface
public interface Supplier<T> {

    /**
     * Gets a result.
     *
     * @return a result
     */
    T get() throws PlatformException;
}
