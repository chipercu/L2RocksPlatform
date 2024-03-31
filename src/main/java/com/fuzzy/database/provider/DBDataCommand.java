package com.fuzzy.database.provider;

import com.fuzzy.database.exception.DatabaseException;
import com.fuzzy.database.provider.DBDataReader;

public interface DBDataCommand extends DBDataReader {

    long nextId(String sequenceName) throws DatabaseException;

    void put(String columnFamily, byte[] key, byte[] value) throws DatabaseException;
    void delete(String columnFamily, byte[] key) throws DatabaseException;
    /**
     * @param beginKey inclusive
     * @param endKey exclusive
     */
    void deleteRange(String columnFamily, byte[] beginKey, byte[] endKey) throws DatabaseException;
    void singleDelete(String columnFamily, byte[] key) throws DatabaseException;
    /**
     * @param beginKey inclusive
     * @param endKey exclusive
     */
    void singleDeleteRange(String columnFamily, byte[] beginKey, byte[] endKey) throws DatabaseException;
}
