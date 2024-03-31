package com.fuzzy.subsystem.entityprovidersdk.iterator;

import com.fuzzy.platform.exception.PlatformException;

import java.util.Iterator;

public interface BaseIterator<T> extends AutoCloseable, Iterator<T> {

    @Override
    boolean hasNext();

    @Override
    T next();

    @Override
    void close() throws PlatformException;
}