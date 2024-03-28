package com.fuzzy.main.rdao.database.provider;

import com.fuzzy.main.rdao.database.exception.DatabaseException;

public interface DBDataReader extends AutoCloseable {

    DBIterator createIterator(String columnFamily) throws DatabaseException;
    byte[] getValue(String columnFamily, byte[] key) throws DatabaseException;
}
