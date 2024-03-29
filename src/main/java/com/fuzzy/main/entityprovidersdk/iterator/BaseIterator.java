package com.fuzzy.main.entityprovidersdk.iterator;

import com.fuzzy.main.platform.exception.PlatformException;

import java.util.Iterator;

public interface BaseIterator<T> extends AutoCloseable, Iterator<T> {

    @Override
    boolean hasNext();

    @Override
    T next();

    @Override
    void close() throws PlatformException;
}