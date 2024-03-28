package com.fuzzy.main.rdao.database.provider;

import com.fuzzy.main.rdao.database.exception.DatabaseException;

public interface DBIterator extends AutoCloseable {

    enum StepDirection {
        FORWARD, BACKWARD
    }

    KeyValue seek(KeyPattern pattern) throws DatabaseException;
    KeyValue next() throws DatabaseException;
    KeyValue step(StepDirection direction) throws DatabaseException;

    @Override
    void close() throws DatabaseException;
}
