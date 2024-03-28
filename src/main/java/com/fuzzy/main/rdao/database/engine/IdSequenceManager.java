package com.fuzzy.main.rdao.database.engine;

import com.fuzzy.main.rdao.database.exception.DatabaseException;
import com.fuzzy.main.rdao.database.exception.SequenceAlreadyExistsException;
import com.fuzzy.main.rdao.database.provider.DBDataCommand;
import com.fuzzy.main.rdao.database.provider.DBIterator;
import com.fuzzy.main.rdao.database.provider.KeyPattern;
import com.fuzzy.main.rdao.database.provider.KeyValue;
import com.fuzzy.main.rdao.database.schema.Schema;
import com.fuzzy.main.rdao.database.schema.dbstruct.DBTable;
import com.fuzzy.main.rdao.database.utils.TypeConvert;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

public class IdSequenceManager {

    private static final String SEQUENCE_PREFIX = "sequence.";

    private final DBDataCommand dataCommand;
    private final ConcurrentMap<Integer, Sequence> sequences = new ConcurrentHashMap<>();

    public IdSequenceManager(DBDataCommand dataCommand) throws DatabaseException {
        this.dataCommand = dataCommand;
        readSequences();
    }

    public void createSequence(DBTable table) throws DatabaseException {
        if (sequences.containsKey(table.getId())) {
            throw new SequenceAlreadyExistsException(table.getNamespace() + "." + table.getName());
        }

        KeyValue keyValue = new KeyValue(createSequenceKey(table), TypeConvert.pack(0L));
        dataCommand.put(Schema.SERVICE_COLUMN_FAMILY, keyValue.getKey(), keyValue.getValue());
        sequences.put(table.getId(), new Sequence(keyValue));
    }

    public void dropSequence(DBTable table) throws DatabaseException {
        dataCommand.delete(Schema.SERVICE_COLUMN_FAMILY, createSequenceKey(table));
        sequences.remove(table.getId());
    }

    public ConcurrentMap<Integer, Sequence> getSequences() {
        return sequences;
    }

    private static byte[] createSequenceKey(DBTable table) {
        return TypeConvert.pack(SEQUENCE_PREFIX + table.getId());
    }

    private void readSequences() throws DatabaseException {
        try (DBIterator i = dataCommand.createIterator(Schema.SERVICE_COLUMN_FAMILY)) {
            byte[] keyPrefix = TypeConvert.pack(SEQUENCE_PREFIX);
            for (KeyValue keyValue = i.seek(new KeyPattern(keyPrefix)); keyValue != null; keyValue = i.next()) {
                Integer tableId = Integer.valueOf(TypeConvert.unpackString(keyValue.getKey(), keyPrefix.length, keyValue.getKey().length - keyPrefix.length));
                sequences.put(tableId, new Sequence(keyValue));
            }
        }
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

            dataCommand.put(Schema.SERVICE_COLUMN_FAMILY, key, TypeConvert.pack(maxCacheValue + SIZE_CACHE));
            maxCacheValue += SIZE_CACHE;
        }
    }
}
