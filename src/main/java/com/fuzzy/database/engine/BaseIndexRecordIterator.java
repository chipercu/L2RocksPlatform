package com.fuzzy.database.engine;

import com.fuzzy.database.Record;
import com.fuzzy.database.engine.BaseRecordIterator;
import com.fuzzy.database.exception.DatabaseException;
import com.fuzzy.database.provider.DBDataReader;
import com.fuzzy.database.provider.DBIterator;
import com.fuzzy.database.provider.KeyPattern;
import com.fuzzy.database.schema.dbstruct.DBTable;
import com.fuzzy.database.utils.key.FieldKey;

import java.util.NoSuchElementException;

public abstract class BaseIndexRecordIterator extends BaseRecordIterator {

    protected final DBTable dbTable;
    protected final DBIterator indexIterator;
    protected final DBIterator dataIterator;
    protected KeyPattern dataKeyPattern = null;
    protected Record nextRecord;

    BaseIndexRecordIterator(DBTable table, DBDataReader dataReader) throws DatabaseException {
        this.dbTable = table;
        this.indexIterator = dataReader.createIterator(table.getIndexColumnFamily());
        this.dataIterator = dataReader.createIterator(table.getDataColumnFamily());
    }


    @Override
    public boolean hasNext() {
        return nextRecord != null;
    }

    @Override
    public Record next() throws DatabaseException {
        if (nextRecord == null) {
            throw new NoSuchElementException();
        }

        Record record = nextRecord;
        nextImpl();
        return record;
    }

    @Override
    public void close() throws DatabaseException {
        indexIterator.close();
        dataIterator.close();
    }

    protected abstract void nextImpl() throws DatabaseException;

    protected Record findRecord(long id) throws DatabaseException {
        if (dataKeyPattern == null) {
            dataKeyPattern = FieldKey.buildKeyPattern(id);
        } else {
            dataKeyPattern.setPrefix(FieldKey.buildKeyPrefix(id));
        }
        Record record = seekRecord(dbTable, dataIterator, dataKeyPattern);
        return record == null ? null :
                checkFilter(record) ? record : null;
    }

    abstract boolean checkFilter(Record record) throws DatabaseException;
}
