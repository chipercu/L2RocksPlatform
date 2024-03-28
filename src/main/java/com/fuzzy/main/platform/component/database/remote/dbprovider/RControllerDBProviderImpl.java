package com.fuzzy.main.platform.component.database.remote.dbprovider;

import com.fuzzy.main.cluster.core.remote.AbstractRController;
import com.fuzzy.main.platform.component.database.DatabaseComponent;
import com.fuzzy.main.platform.sdk.dbprovider.remote.RControllerDBProvider;
import com.fuzzy.main.rdao.database.exception.DatabaseException;
import com.fuzzy.main.rdao.database.provider.DBIterator;
import com.fuzzy.main.rdao.database.provider.DBTransaction;
import com.fuzzy.main.rdao.database.provider.KeyPattern;
import com.fuzzy.main.rdao.database.provider.KeyValue;
import com.fuzzy.main.rdao.rocksdb.options.columnfamily.ColumnFamilyConfig;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

public class RControllerDBProviderImpl extends AbstractRController<DatabaseComponent> implements RControllerDBProvider {

    private final AtomicLong objectCounter = new AtomicLong(1);
    private final ConcurrentMap<Long, DBTransaction> transactions = new ConcurrentHashMap<>();
    private final ConcurrentMap<Long, DBIterator> iterators = new ConcurrentHashMap<>();

    public RControllerDBProviderImpl(DatabaseComponent subSystem) {
        super(subSystem);
    }

    @Override
    public long createIterator(String columnFamily) throws DatabaseException {
        DBIterator iterator = component.getRocksDBProvider().createIterator(columnFamily);

        long iteratorId = objectCounter.getAndIncrement();
        iterators.put(iteratorId, iterator);
        return iteratorId;
    }

    @Override
    public long beginTransaction() throws DatabaseException {
        DBTransaction transaction = component.getRocksDBProvider().beginTransaction();

        long transactionId = objectCounter.getAndIncrement();
        transactions.put(transactionId, transaction);
        return transactionId;
    }

    @Override
    public byte[] getValue(String columnFamily, byte[] key) throws DatabaseException {
        return component.getRocksDBProvider().getValue(columnFamily, key);
    }

    @Override
    public boolean containsColumnFamily(String name) throws DatabaseException {
        return component.getRocksDBProvider().containsColumnFamily(name);
    }

    @Override
    public String[] getColumnFamilies() throws DatabaseException {
        return component.getRocksDBProvider().getColumnFamilies();
    }

    @Override
    public void createColumnFamily(String name) throws DatabaseException {
        component.getRocksDBProvider().createColumnFamily(name);
    }

    @Override
    public void createColumnFamily(String name, ColumnFamilyConfig options) throws DatabaseException {
        component.getRocksDBProvider().createColumnFamily(name, options);
    }

    @Override
    public void dropColumnFamily(String name) throws DatabaseException {
        component.getRocksDBProvider().dropColumnFamily(name);
    }

    @Override
    public boolean containsSequence(String name) throws DatabaseException {
        return component.getRocksDBProvider().containsSequence(name);
    }

    @Override
    public void createSequence(String name) throws DatabaseException {
        component.getRocksDBProvider().createSequence(name);
    }

    @Override
    public void dropSequence(String name) throws DatabaseException {
        component.getRocksDBProvider().dropSequence(name);
    }

    @Override
    public KeyValue seekIterator(KeyPattern pattern, long iteratorId) throws DatabaseException {
        return getIterator(iteratorId).seek(pattern);
    }

    @Override
    public KeyValue nextIterator(long iteratorId) throws DatabaseException {
        return getIterator(iteratorId).next();
    }

    @Override
    public KeyValue stepIterator(DBIterator.StepDirection direction, long iteratorId) throws DatabaseException {
        return getIterator(iteratorId).step(direction);
    }

    @Override
    public void closeIterator(long iteratorId) throws DatabaseException {
        getIterator(iteratorId).close();
    }

    @Override
    public long createIteratorTransaction(String columnFamily, long transactionId) throws DatabaseException {
        DBIterator iterator = getTransaction(transactionId).createIterator(columnFamily);

        long iteratorId = objectCounter.getAndIncrement();
        iterators.put(iteratorId, iterator);
        return iteratorId;
    }

    @Override
    public long nextIdTransaction(String sequenceName, long transactionId) throws DatabaseException {
        return getTransaction(transactionId).nextId(sequenceName);
    }

    @Override
    public byte[] getValueTransaction(String columnFamily, byte[] key, long transactionId) throws DatabaseException {
        return getTransaction(transactionId).getValue(columnFamily, key);
    }

    @Override
    public void putTransaction(String columnFamily, byte[] key, byte[] value, long transactionId) throws DatabaseException {
        getTransaction(transactionId).put(columnFamily, key, value);
    }

    @Override
    public void deleteTransaction(String columnFamily, byte[] key, long transactionId) throws DatabaseException {
        getTransaction(transactionId).delete(columnFamily, key);
    }

    @Override
    public void deleteRangeTransaction(String columnFamily, byte[] beginKey, byte[] endKey, long transactionId) throws DatabaseException {
        getTransaction(transactionId).deleteRange(columnFamily, beginKey, endKey);
    }

    @Override
    public void singleDeleteTransaction(String columnFamily, byte[] key, long transactionId) throws DatabaseException {
        getTransaction(transactionId).singleDelete(columnFamily, key);
    }

    @Override
    public void singleDeleteRangeTransaction(String columnFamily, byte[] beginKey, byte[] endKey, long transactionId) throws DatabaseException {
        getTransaction(transactionId).singleDeleteRange(columnFamily, beginKey, endKey);
    }

    @Override
    public void singleDeleteRangeTransaction(String columnFamily, KeyPattern keyPattern, long transactionId) throws DatabaseException {
        getTransaction(transactionId).singleDeleteRange(columnFamily, keyPattern);
    }

    @Override
    public void compactRange() throws DatabaseException {
        component.getRocksDBProvider().compactRange();
    }

    @Override
    public void commitTransaction(long transactionId) throws DatabaseException {
        getTransaction(transactionId).commit();
    }

    @Override
    public void rollbackTransaction(long transactionId) throws DatabaseException {
        getTransaction(transactionId).rollback();
    }

    @Override
    public void closeTransaction(long transactionId) throws DatabaseException {
        getTransaction(transactionId).close();
    }

    private DBIterator getIterator(long iteratorId) throws DatabaseException {
        DBIterator iterator = iterators.get(iteratorId);
        if (iterator != null) {
            return iterator;
        }
        throw new DatabaseException("Iterator №" + iteratorId + " not found.");
    }

    private DBTransaction getTransaction(long transactionId) throws DatabaseException {
        DBTransaction transaction = transactions.get(transactionId);
        if (transaction != null) {
            return transaction;
        }
        throw new DatabaseException("Transaction №" + transactionId + " not found.");
    }
}
