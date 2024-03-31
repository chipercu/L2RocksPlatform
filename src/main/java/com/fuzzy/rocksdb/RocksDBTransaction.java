package com.fuzzy.rocksdb;

import com.google.common.primitives.UnsignedBytes;
import com.fuzzy.database.exception.ColumnFamilyNotFoundException;
import com.fuzzy.database.exception.DatabaseException;
import com.fuzzy.database.exception.SequenceNotFoundException;
import com.fuzzy.database.provider.DBIterator;
import com.fuzzy.database.provider.DBTransaction;
import com.fuzzy.database.provider.KeyPattern;
import com.fuzzy.database.utils.ByteInterval;
import com.fuzzy.rocksdb.RocksDBIterator;
import com.fuzzy.rocksdb.RocksDBProvider;
import com.fuzzy.rocksdb.SequenceManager;
import org.rocksdb.ColumnFamilyHandle;
import org.rocksdb.RocksDBException;
import org.rocksdb.RocksIterator;
import org.rocksdb.Transaction;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class RocksDBTransaction implements DBTransaction {

    private static final Comparator<byte[]> KEY_COMPARATOR = UnsignedBytes.lexicographicalComparator();

    private final Transaction transaction;
    private final com.fuzzy.rocksdb.RocksDBProvider rocksDBProvider;
    private final Map<String, RangeKey> compactingKeys = new HashMap<>();

    RocksDBTransaction(Transaction transaction, RocksDBProvider rocksDBProvider) {
        this.transaction = transaction;
        this.rocksDBProvider = rocksDBProvider;
    }

    @Override
    public DBIterator createIterator(String columnFamily) throws DatabaseException {
        return buildIterator(rocksDBProvider.getColumnFamilyHandle(columnFamily));
    }

    @Override
    public long nextId(String sequenceName) throws DatabaseException {
        SequenceManager.Sequence sequence = rocksDBProvider.getSequenceManager().getSequence(sequenceName);
        if (sequence == null) {
            throw new SequenceNotFoundException(sequenceName);
        }
        return sequence.next();
    }

    @Override
    public byte[] getValue(String columnFamily, byte[] key) throws DatabaseException {
        try {
            return transaction.get(rocksDBProvider.getColumnFamilyHandle(columnFamily), rocksDBProvider.getReadOptions(), key);
        } catch (RocksDBException e) {
            throw new DatabaseException(e);
        }
    }

    @Override
    public void put(String columnFamily, byte[] key, byte[] value) throws DatabaseException {
        ColumnFamilyHandle columnFamilyHandle = rocksDBProvider.getColumnFamilyHandle(columnFamily);
        try {
            transaction.put(columnFamilyHandle, key, value);
        } catch (RocksDBException e) {
            throw new DatabaseException(e);
        }
    }

    @Override
    public void delete(String columnFamily, byte[] key) throws DatabaseException {
        delete(columnFamily, key, transaction::delete);

        compactingKeys.computeIfAbsent(columnFamily, s -> new RangeKey()).setKey(key);
    }

    @Override
    public void deleteRange(String columnFamily, byte[] beginKey, byte[] endKey) throws DatabaseException {
        deleteRange(columnFamily, beginKey, endKey, transaction::delete);

        compactingKeys.computeIfAbsent(columnFamily, s -> new RangeKey()).setRange(beginKey, endKey);
    }

    @Override
    public void singleDelete(String columnFamily, byte[] key) throws DatabaseException {
        delete(columnFamily, key, transaction::delete);

        compactingKeys.computeIfAbsent(columnFamily, s -> new RangeKey()).setKey(key);
    }

    @Override
    public void singleDeleteRange(String columnFamily, byte[] beginKey, byte[] endKey) throws DatabaseException {
        deleteRange(columnFamily, beginKey, endKey, transaction::delete);

        compactingKeys.computeIfAbsent(columnFamily, s -> new RangeKey()).setRange(beginKey, endKey);
    }

    @Override
    public void singleDeleteRange(String columnFamily, KeyPattern keyPattern) throws DatabaseException {
        ByteInterval deleteRange = deleteRange(columnFamily, keyPattern, transaction::delete);
        deleteRange.validate();
        if (deleteRange.getBegin() != null && deleteRange.getEnd() != null) {
            compactingKeys.computeIfAbsent(columnFamily, s -> new RangeKey()).setRange(deleteRange.getBegin(), deleteRange.getEnd());
        }
    }

    private void delete(String columnFamily, byte[] key, BiConsumer<ColumnFamilyHandle, byte[]> deleteFunc) throws DatabaseException {
        ColumnFamilyHandle columnFamilyHandle = rocksDBProvider.getColumnFamilyHandle(columnFamily);
        try {
            deleteFunc.accept(columnFamilyHandle, key);
        } catch (RocksDBException e) {
            throw new DatabaseException(e);
        }
    }

    private void deleteRange(String columnFamily, byte[] beginKey, byte[] endKey, BiConsumer<ColumnFamilyHandle, byte[]> deleteFunc) throws DatabaseException {
        ColumnFamilyHandle columnFamilyHandle = rocksDBProvider.getColumnFamilyHandle(columnFamily);

        try (RocksIterator i = transaction.getIterator(rocksDBProvider.getReadOptions(), columnFamilyHandle)) {
            for (i.seek(beginKey); i.isValid(); i.next()) {
                byte[] key = i.key();
                if (key == null || KEY_COMPARATOR.compare(key, endKey) >= 0) {
                    break;
                }

                deleteFunc.accept(columnFamilyHandle, key);
            }

            i.status();
        } catch (RocksDBException e) {
            throw new DatabaseException(e);
        }
    }

    private ByteInterval deleteRange(String columnFamily, KeyPattern keyPattern, BiConsumer<ColumnFamilyHandle, byte[]> deleteFunc) throws DatabaseException {
        ColumnFamilyHandle columnFamilyHandle = rocksDBProvider.getColumnFamilyHandle(columnFamily);
        ByteInterval result = new ByteInterval();
        try (RocksIterator i = transaction.getIterator(rocksDBProvider.getReadOptions(), columnFamilyHandle)) {
            for (i.seek(keyPattern.getPrefix()); i.isValid(); i.next()) {
                byte[] key = i.key();
                if (key == null || keyPattern.match(key) == KeyPattern.MATCH_RESULT_UNSUCCESS) {
                    break;
                }
                result.setBeginIfAbsent(key);
                result.setEnd(key);
                deleteFunc.accept(columnFamilyHandle, key);
            }

            i.status();
        } catch (RocksDBException e) {
            throw new DatabaseException(e);
        }
        return result;
    }

    @Override
    public void commit() throws DatabaseException {
        try {
            transaction.commit();
            compact();
        } catch (RocksDBException e) {
            throw new DatabaseException(e);
        } finally {
            compactingKeys.clear();
        }
    }

    @Override
    public void rollback() throws DatabaseException {
        try {
            transaction.rollback();
        } catch (RocksDBException e) {
            throw new DatabaseException(e);
        } finally {
            compactingKeys.clear();
        }
    }

    @Override
    public void compactRange() throws DatabaseException {
        try {
            rocksDBProvider.getRocksDB().compactRange();
        } catch (RocksDBException e) {
            throw new DatabaseException(e);
        }
    }

    @Override
    public void close() throws DatabaseException {
        transaction.close();
    }

    private com.fuzzy.rocksdb.RocksDBIterator buildIterator(ColumnFamilyHandle columnFamily) {
        return new RocksDBIterator(transaction.getIterator(rocksDBProvider.getReadOptions(), columnFamily));
    }

    //TODO ULitin V. Временно отключили компакшен - сильно бьет по производительности - необходимо другое решение
    private void compact() throws ColumnFamilyNotFoundException, RocksDBException {
//        for (Map.Entry<String, RangeKey> entry : compactingKeys.entrySet()) {
//            ColumnFamilyHandle columnFamilyHandle = rocksDBProvider.getColumnFamilyHandle(entry.getKey());
//            rocksDBProvider.getRocksDB().compactRange(
//                    columnFamilyHandle,
//                    entry.getValue().begin,
//                    entry.getValue().end,
//                    true, -1, 0);
//        }
    }

    private static class RangeKey {

        byte[] begin = null;
        byte[] end = null;

        void setBegin(byte[] key) {
            if (begin == null || KEY_COMPARATOR.compare(key, begin) < 0) {
                begin = key;
            }
        }

        void setEnd(byte[] key) {
            if (end == null || KEY_COMPARATOR.compare(key, end) > 0) {
                end = key;
            }
        }

        void setRange(byte[] begin, byte[] end) {
            setBegin(begin);
            setEnd(end);
        }

        void setKey(byte[] key) {
            if (begin == null) {
                begin = key;
                end = nextOf(Arrays.copyOf(key, key.length));
            } else {
                int res = KEY_COMPARATOR.compare(key, begin);
                if (res < 0) {
                    begin = key;
                } else if (res != 0) {
                    res = KEY_COMPARATOR.compare(key, end);
                    if (res > 0) {
                        end = nextOf(key);
                    }
                }
            }
        }

        private static byte[] nextOf(byte[] key) {
            int val = UnsignedBytes.toInt(key[key.length - 1]);
            if (val >= 0xff) {
                key = Arrays.copyOf(key, key.length + 1);
                val = 0;
            }
            key[key.length - 1] = UnsignedBytes.checkedCast(++val);
            return key;
        }
    }

    @FunctionalInterface
    private interface BiConsumer<T, U> {

        void accept(T t, U u) throws RocksDBException;
    }
}
