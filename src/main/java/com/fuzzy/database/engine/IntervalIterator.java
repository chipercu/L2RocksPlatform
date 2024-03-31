package com.fuzzy.database.engine;

import com.fuzzy.database.domainobject.filter.IntervalFilter;
import com.fuzzy.database.engine.BaseIntervalRecordIterator;
import com.fuzzy.database.exception.DatabaseException;
import com.fuzzy.database.provider.DBDataReader;
import com.fuzzy.database.provider.DBIterator;
import com.fuzzy.database.provider.KeyPattern;
import com.fuzzy.database.provider.KeyValue;
import com.fuzzy.database.schema.dbstruct.DBBaseIntervalIndex;
import com.fuzzy.database.schema.dbstruct.DBTable;
import com.fuzzy.database.utils.key.IntervalIndexKey;

public class IntervalIterator extends BaseIntervalRecordIterator<IntervalFilter> {

    public IntervalIterator(DBTable table, IntervalFilter filter, DBDataReader dataReader) {
        super(table, filter, filter.getSortDirection(), dataReader);
    }

    @Override
    DBBaseIntervalIndex getIndex(IntervalFilter filter, DBTable table) {
        return table.getIndex(filter);
    }

    @Override
    KeyValue seek(DBIterator indexIterator, KeyPattern pattern) throws DatabaseException {
        return indexIterator.seek(pattern);
    }

    @Override
    int matchKey(long id, byte[] key) {
        long indexedBeginValue = IntervalIndexKey.unpackIndexedValue(key);
        if (indexedBeginValue < filterBeginValue || indexedBeginValue > filterEndValue) {
            return KeyPattern.MATCH_RESULT_UNSUCCESS;
        }
        return KeyPattern.MATCH_RESULT_SUCCESS;
    }
}
