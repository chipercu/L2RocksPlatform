package com.fuzzy.platform.sdk.dbprovider.remote;

import com.fuzzy.cluster.core.remote.struct.RController;
import com.fuzzy.database.exception.DatabaseException;
import com.fuzzy.database.provider.DBIterator;
import com.fuzzy.database.provider.KeyPattern;
import com.fuzzy.database.provider.KeyValue;
import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.rocksdb.options.columnfamily.ColumnFamilyConfig;

public interface RControllerDBProvider extends RController {

    long createIterator(String columnFamily) throws DatabaseException, PlatformException;
    long beginTransaction() throws DatabaseException, PlatformException;
    byte[] getValue(String columnFamily, byte[] key) throws DatabaseException, PlatformException;
    boolean containsColumnFamily(String name) throws DatabaseException, PlatformException;
    String[] getColumnFamilies() throws DatabaseException, PlatformException;

    void createColumnFamily(String name) throws DatabaseException, PlatformException;

    void createColumnFamily(String name, ColumnFamilyConfig options) throws DatabaseException, PlatformException;

    void dropColumnFamily(String name) throws DatabaseException, PlatformException;
    boolean containsSequence(String name) throws DatabaseException, PlatformException;
    void createSequence(String name) throws DatabaseException, PlatformException;
    void dropSequence(String name) throws DatabaseException, PlatformException;

    KeyValue seekIterator(KeyPattern pattern, long iteratorId) throws DatabaseException, PlatformException;
    KeyValue nextIterator(long iteratorId) throws DatabaseException, PlatformException;
    KeyValue stepIterator(DBIterator.StepDirection direction, long iteratorId) throws DatabaseException, PlatformException;
    void closeIterator(long iteratorId) throws DatabaseException, PlatformException;

    long createIteratorTransaction(String columnFamily, long transactionId) throws DatabaseException, PlatformException;
    long nextIdTransaction(String sequenceName, long transactionId) throws DatabaseException, PlatformException;
    byte[] getValueTransaction(String columnFamily, byte[] key, long transactionId) throws DatabaseException, PlatformException;
    void putTransaction(String columnFamily, byte[] key, byte[] value, long transactionId) throws DatabaseException, PlatformException;

    void deleteTransaction(String columnFamily, byte[] key, long transactionId) throws DatabaseException, PlatformException;
    void deleteRangeTransaction(String columnFamily, byte[] beginKey, byte[] endKey, long transactionId) throws DatabaseException, PlatformException;
    void singleDeleteTransaction(String columnFamily, byte[] key, long transactionId) throws DatabaseException, PlatformException;
    void singleDeleteRangeTransaction(String columnFamily, byte[] beginKey, byte[] endKey, long transactionId) throws DatabaseException, PlatformException;
    void singleDeleteRangeTransaction(String columnFamily, KeyPattern keyPattern, long transactionId) throws DatabaseException, PlatformException;

    void compactRange() throws DatabaseException, PlatformException;

    void commitTransaction(long transactionId) throws DatabaseException, PlatformException;
    void rollbackTransaction(long transactionId) throws DatabaseException, PlatformException;
    void closeTransaction(long transactionId) throws DatabaseException, PlatformException;
}
