package com.fuzzy.main.rdao.database;

import com.fuzzy.main.rdao.database.exception.DatabaseException;

import java.util.Iterator;

public interface DataIterator<T> extends AutoCloseable, Iterator<T> {

//    void reuseReturningRecord(boolean value);

    @Override
    boolean hasNext() throws DatabaseException;

    @Override
    T next() throws DatabaseException;

    @Override
    void close() throws DatabaseException;
}
