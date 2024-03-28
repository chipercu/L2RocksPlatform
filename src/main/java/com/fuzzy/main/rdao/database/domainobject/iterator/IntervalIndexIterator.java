package com.fuzzy.main.rdao.database.domainobject.iterator;

import com.fuzzy.main.rdao.database.domainobject.DataEnumerable;
import com.fuzzy.main.rdao.database.domainobject.DomainObject;
import com.fuzzy.main.rdao.database.domainobject.filter.IntervalFilter;
import com.fuzzy.main.rdao.database.exception.DatabaseException;
import com.fuzzy.main.rdao.database.provider.DBIterator;
import com.fuzzy.main.rdao.database.provider.KeyPattern;
import com.fuzzy.main.rdao.database.provider.KeyValue;
import com.fuzzy.main.rdao.database.schema.BaseIntervalIndex;
import com.fuzzy.main.rdao.database.schema.StructEntity;
import com.fuzzy.main.rdao.database.utils.key.IntervalIndexKey;

import java.util.Set;

public class IntervalIndexIterator<E extends DomainObject> extends BaseIntervalIndexIterator<E, IntervalFilter> {

    public IntervalIndexIterator(DataEnumerable dataEnumerable, Class<E> clazz, Set<Integer> loadingFields, IntervalFilter filter) throws DatabaseException {
        super(dataEnumerable, clazz, loadingFields, filter.getSortDirection(), filter);
    }

    @Override
    BaseIntervalIndex getIndex(IntervalFilter filter, StructEntity entity) {
        return entity.getIntervalIndex(filter.getHashedValues().keySet(), filter.getIndexedFieldId());
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
