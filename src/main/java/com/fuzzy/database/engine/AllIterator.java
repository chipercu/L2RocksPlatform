package com.fuzzy.database.engine;

import com.fuzzy.database.Record;
import com.fuzzy.database.engine.BaseRecordIterator;
import com.fuzzy.database.exception.DatabaseException;
import com.fuzzy.database.provider.DBDataReader;
import com.fuzzy.database.provider.DBIterator;
import com.fuzzy.database.schema.dbstruct.DBTable;

public class AllIterator extends BaseRecordIterator {

    private final DBIterator iterator;
    private final DBTable table;
    private final NextState state;

    public AllIterator(DBTable table, DBDataReader dataReader) throws DatabaseException {
        this.iterator = dataReader.createIterator(table.getDataColumnFamily());
        this.table = table;
        state = initializeState();
    }

    @Override
    public boolean hasNext() throws DatabaseException {
        return !state.isEmpty();
    }

    @Override
    public Record next() throws DatabaseException {
        return nextRecord(table, state, iterator);
    }

    @Override
    public void close() throws DatabaseException {
        iterator.close();
    }

    private NextState initializeState() throws DatabaseException {
        return seek(null, iterator);
    }
}
