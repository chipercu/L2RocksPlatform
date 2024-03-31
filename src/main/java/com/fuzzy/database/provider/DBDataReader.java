package com.fuzzy.database.provider;

import com.fuzzy.database.exception.DatabaseException;
import com.fuzzy.database.provider.DBIterator;

public interface DBDataReader extends AutoCloseable {

    DBIterator createIterator(String columnFamily) throws DatabaseException;
    byte[] getValue(String columnFamily, byte[] key) throws DatabaseException;
}
