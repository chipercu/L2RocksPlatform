package com.fuzzy.database.provider;

import com.fuzzy.database.exception.DatabaseException;
import com.fuzzy.database.provider.DBDataReader;
import com.fuzzy.database.provider.DBIterator;
import com.fuzzy.database.provider.DBTransaction;
import com.fuzzy.rocksdb.options.columnfamily.ColumnFamilyConfig;

public interface DBProvider extends DBDataReader {

    DBIterator createIterator(String columnFamily) throws DatabaseException;
    DBTransaction beginTransaction() throws DatabaseException;
    byte[] getValue(String columnFamily, byte[] key) throws DatabaseException;

    boolean containsColumnFamily(String name) throws DatabaseException;
    String[] getColumnFamilies() throws DatabaseException;

    void createColumnFamily(String name) throws DatabaseException;

    void createColumnFamily(String name, ColumnFamilyConfig options) throws DatabaseException;

    void dropColumnFamily(String name) throws DatabaseException;

    void compactRange() throws DatabaseException;

    boolean containsSequence(String name) throws DatabaseException;
    void createSequence(String name) throws DatabaseException;
    void dropSequence(String name) throws DatabaseException;
}
