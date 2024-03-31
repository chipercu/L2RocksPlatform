package com.fuzzy.database.provider;

import com.fuzzy.database.exception.DatabaseException;
import com.fuzzy.database.provider.DBDataCommand;
import com.fuzzy.database.provider.KeyPattern;

public interface DBTransaction extends AutoCloseable, DBDataCommand {

    void singleDeleteRange(String columnFamily, KeyPattern keyPattern) throws DatabaseException;

    void commit() throws DatabaseException;
    void rollback() throws DatabaseException;

    void compactRange() throws DatabaseException;

    @Override
    void close() throws DatabaseException;
}
