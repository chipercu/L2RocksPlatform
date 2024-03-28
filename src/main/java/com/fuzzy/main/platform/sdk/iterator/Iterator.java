package com.fuzzy.main.platform.sdk.iterator;

import com.fuzzy.main.platform.exception.PlatformException;

public interface Iterator<T> extends AutoCloseable {

    boolean hasNext() throws PlatformException;

    T next() throws PlatformException;

    void close() throws PlatformException;
}
