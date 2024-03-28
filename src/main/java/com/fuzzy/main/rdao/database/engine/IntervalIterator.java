package com.fuzzy.main.rdao.database.engine;

import com.fuzzy.main.rdao.database.domainobject.filter.IntervalFilter;
import com.fuzzy.main.rdao.database.exception.DatabaseException;
import com.fuzzy.main.rdao.database.provider.DBDataReader;
import com.fuzzy.main.rdao.database.provider.DBIterator;
import com.fuzzy.main.rdao.database.provider.KeyPattern;
import com.fuzzy.main.rdao.database.provider.KeyValue;
import com.fuzzy.main.rdao.database.schema.dbstruct.DBBaseIntervalIndex;
import com.fuzzy.main.rdao.database.schema.dbstruct.DBTable;
import com.fuzzy.main.rdao.database.utils.key.IntervalIndexKey;

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
