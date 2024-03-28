package com.fuzzy.main.rdao.rocksdb;

import com.fuzzy.main.rdao.database.exception.DatabaseException;
import com.fuzzy.main.rdao.database.exception.SequenceAlreadyExistsException;
import com.fuzzy.main.rdao.database.provider.DBIterator;
import com.fuzzy.main.rdao.database.provider.KeyPattern;
import com.fuzzy.main.rdao.database.provider.KeyValue;
import com.fuzzy.main.rdao.database.utils.TypeConvert;
import org.rocksdb.ColumnFamilyHandle;
import org.rocksdb.RocksDBException;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

public class SequenceManager {

    public static final String SEQUENCE_PREFIX = "sequence.";

    private final RocksDBProvider dbProvider;
    private final ColumnFamilyHandle defaultColumnFamily;
    private final ConcurrentMap<String, Sequence> sequences = new ConcurrentHashMap<>();

    public SequenceManager(RocksDBProvider dbProvider) throws DatabaseException {
        this.dbProvider = dbProvider;
        this.defaultColumnFamily = dbProvider.getColumnFamilyHandle(RocksDBProvider.DEFAULT_COLUMN_FAMILY);
        readSequences();
    }

    public Sequence getSequence(String name) {
       return sequences.get(name);
    }

    public void createSequence(String name) throws DatabaseException {
        if (sequences.containsKey(name)) {
            throw new SequenceAlreadyExistsException(name);
        }

        final KeyValue keyValue = new KeyValue(createSequenceKey(name), TypeConvert.pack(0L));
        try {
            dbProvider.getRocksDB().put(defaultColumnFamily, keyValue.getKey(), keyValue.getValue());
            sequences.put(name, new Sequence(keyValue));
        } catch (RocksDBException e) {
            throw new DatabaseException(e);
        }
    }

    public void dropSequence(String name) throws DatabaseException {
        try {
            dbProvider.getRocksDB().delete(defaultColumnFamily, createSequenceKey(name));
            sequences.remove(name);
        } catch (RocksDBException e) {
            throw new DatabaseException(e);
        }
    }

    private static byte[] createSequenceKey(String sequenceName) {
        return TypeConvert.pack(SEQUENCE_PREFIX + sequenceName);
    }

    private void readSequences() throws DatabaseException {
        try (DBIterator i = dbProvider.createIterator(RocksDBProvider.DEFAULT_COLUMN_FAMILY)) {
            final byte[] keyPrefix = TypeConvert.pack(SEQUENCE_PREFIX);
            for (KeyValue keyValue = i.seek(new KeyPattern(keyPrefix)); keyValue != null; keyValue = i.next()) {
                String sequenceName = TypeConvert.unpackString(keyValue.getKey(), keyPrefix.length, keyValue.getKey().length - keyPrefix.length);
                sequences.put(sequenceName, new Sequence(keyValue));
            }
        }
    }

    public Map<String, Sequence> getSequences() {
        return Collections.unmodifiableMap(sequences);
    }

    public class Sequence {

        private final static int SIZE_CACHE = 10;

        private final byte[] key;
        private final AtomicLong counter;
        private long maxCacheValue;

        Sequence(KeyValue keyValue) {
            this.key = keyValue.getKey();
            this.maxCacheValue = TypeConvert.unpackLong(keyValue.getValue(), 0);
            this.counter = new AtomicLong(maxCacheValue);
        }

        public long next() throws DatabaseException {
            long value;
            do {
                value = counter.get();
                if (value >= maxCacheValue) {
                    //Кеш закончился-берем еще
                    growCache();
                }
            } while (!counter.compareAndSet(value, value + 1));
            return value + 1;
        }

        private synchronized void growCache() throws DatabaseException {
            if ((maxCacheValue - counter.get()) > SIZE_CACHE) {
                return;
            }

            try {
                dbProvider.getRocksDB().put(defaultColumnFamily, key, TypeConvert.pack(maxCacheValue + SIZE_CACHE));
                maxCacheValue += SIZE_CACHE;
            } catch (RocksDBException e) {
                throw new DatabaseException(e);
            }
        }
    }
}
