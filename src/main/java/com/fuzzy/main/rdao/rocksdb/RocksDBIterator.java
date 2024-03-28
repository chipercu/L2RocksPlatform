package com.fuzzy.main.rdao.rocksdb;

import com.fuzzy.main.rdao.database.exception.DatabaseException;
import com.fuzzy.main.rdao.database.provider.DBIterator;
import com.fuzzy.main.rdao.database.provider.KeyPattern;
import com.fuzzy.main.rdao.database.provider.KeyValue;
import org.rocksdb.RocksDBException;
import org.rocksdb.RocksIterator;

public class RocksDBIterator implements DBIterator {

    private final RocksIterator iterator;
    private KeyPattern pattern;

    RocksDBIterator(RocksIterator iterator) {
        this.iterator = iterator;
    }

    @Override
    public KeyValue seek(KeyPattern pattern) throws DatabaseException {
        this.pattern = pattern;

        if (pattern == null) {
            iterator.seekToFirst();
        } else if (pattern.getPrefix() == null) {
            if (pattern.isForBackward()) {
                iterator.seekToLast();
            } else {
                iterator.seekToFirst();
            }
        } else {
            if (pattern.isForBackward()) {
                iterator.seekForPrev(pattern.getPrefix());
            } else {
                iterator.seek(pattern.getPrefix());
            }
        }

        return findMatched();
    }

    @Override
    public KeyValue next() throws DatabaseException {
        iterator.next();
        return findMatched();
    }

    @Override
    public KeyValue step(StepDirection direction) throws DatabaseException {
        switch (direction) {
            case FORWARD:
                iterator.next();
                break;
            case BACKWARD:
                iterator.prev();
                break;
        }

        return getKeyValue();
    }

    @Override
    public void close() throws DatabaseException {
        iterator.close();
    }

    private KeyValue getKeyValue() throws DatabaseException {
        if (iterator.isValid()) {
            return new KeyValue(iterator.key(), iterator.value());
        }

        throwIfFail();
        return null;
    }

    private KeyValue findMatched() throws DatabaseException {
        while (iterator.isValid()) {
            byte[] key = iterator.key();
            if (pattern != null) {
                int matchResult = pattern.match(key);
                if (matchResult == KeyPattern.MATCH_RESULT_CONTINUE) {
                    iterator.next();
                    continue;
                } else if (matchResult == KeyPattern.MATCH_RESULT_UNSUCCESS) {
                    return null;
                }
            }

            return new KeyValue(key, iterator.value());
        }

        throwIfFail();
        return null;
    }

    private void throwIfFail() throws DatabaseException {
        try {
            iterator.status();
        } catch (RocksDBException e) {
            throw new DatabaseException(e);
        }
    }
}
