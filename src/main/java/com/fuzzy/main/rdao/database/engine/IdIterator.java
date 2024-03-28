package com.fuzzy.main.rdao.database.engine;

import com.fuzzy.main.rdao.database.Record;
import com.fuzzy.main.rdao.database.domainobject.filter.IdFilter;
import com.fuzzy.main.rdao.database.exception.DatabaseException;
import com.fuzzy.main.rdao.database.provider.DBDataReader;
import com.fuzzy.main.rdao.database.provider.DBIterator;
import com.fuzzy.main.rdao.database.provider.KeyPattern;
import com.fuzzy.main.rdao.database.schema.dbstruct.DBTable;
import com.fuzzy.main.rdao.database.utils.key.FieldKey;

import java.util.NoSuchElementException;

public class IdIterator extends BaseRecordIterator {

    private final DBIterator iterator;
    private final DBTable table;
    private final NextState state;
    private final long endId;

    public IdIterator(DBTable table, IdFilter filter, DBDataReader dataReader) {
        this.iterator = dataReader.createIterator(table.getDataColumnFamily());
        this.table = table;
        this.endId = filter.getToId();
        KeyPattern dataKeyPattern = new KeyPattern(FieldKey.buildKeyPrefix(filter.getFromId()), 0);
        state = initializeState(dataKeyPattern);
        if (endReached()) {
            state.reset();
            close();
        }
    }

    @Override
    public boolean hasNext() throws DatabaseException {
        return !state.isEmpty();
    }

    @Override
    public Record next() throws DatabaseException {
        if (state.isEmpty()) {
            throw new NoSuchElementException();
        }

        Record result = nextRecord(table, state, iterator);
        if (endReached()) {
            state.reset();
            close();
        }
        return result;
    }

    @Override
    public void close() throws DatabaseException {
        iterator.close();
    }

    private NextState initializeState(KeyPattern keyPattern) throws DatabaseException {
        return seek(keyPattern, iterator);
    }

    private boolean endReached() {
        return state.isEmpty() || state.getNextId() > endId;
    }
}
