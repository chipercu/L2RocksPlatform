package com.fuzzy.database.provider;

import com.fuzzy.database.exception.DatabaseException;
import com.fuzzy.database.provider.KeyPattern;
import com.fuzzy.database.provider.KeyValue;

public interface DBIterator extends AutoCloseable {

    enum StepDirection {
        FORWARD, BACKWARD
    }

    com.fuzzy.database.provider.KeyValue seek(KeyPattern pattern) throws DatabaseException;
    com.fuzzy.database.provider.KeyValue next() throws DatabaseException;
    KeyValue step(StepDirection direction) throws DatabaseException;

    @Override
    void close() throws DatabaseException;
}
