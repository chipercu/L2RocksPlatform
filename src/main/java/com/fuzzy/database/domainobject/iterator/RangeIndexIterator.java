package com.fuzzy.database.domainobject.iterator;

import com.fuzzy.database.domainobject.DataEnumerable;
import com.fuzzy.database.domainobject.DomainObject;
import com.fuzzy.database.domainobject.filter.RangeFilter;
import com.fuzzy.database.domainobject.filter.SortDirection;
import com.fuzzy.database.domainobject.iterator.BaseIntervalIndexIterator;
import com.fuzzy.database.exception.DatabaseException;
import com.fuzzy.database.provider.DBIterator;
import com.fuzzy.database.provider.KeyPattern;
import com.fuzzy.database.provider.KeyValue;
import com.fuzzy.database.schema.BaseIntervalIndex;
import com.fuzzy.database.schema.StructEntity;
import com.fuzzy.database.utils.RangeIndexUtils;
import com.fuzzy.database.utils.key.RangeIndexKey;

import java.util.*;

public class RangeIndexIterator<E extends DomainObject> extends BaseIntervalIndexIterator<E, RangeFilter> {

    private Set<Long> processedIds/* = null*/; // не нужно инициализировать, т.к. matchKey вызывается из конструктора базового класса

    public RangeIndexIterator(DataEnumerable dataEnumerable, Class<E> clazz, Set<Integer> loadingFields, RangeFilter filter) throws DatabaseException {
        super(dataEnumerable, clazz, loadingFields, SortDirection.ASC, filter);
    }

    @Override
    BaseIntervalIndex getIndex(RangeFilter filter, StructEntity entity) {
        RangeFilter.IndexedField indexedField = filter.getIndexedField();
        return entity.getRangeIndex(filter.getHashedValues().keySet(), indexedField.beginField, indexedField.endField);
    }

    @Override
    KeyValue seek(DBIterator indexIterator, KeyPattern pattern) throws DatabaseException {
        return RangeIndexUtils.seek(indexIterator, pattern, filterBeginValue);
    }

    @Override
    int matchKey(long id, byte[] key) {
        long indexedValue = RangeIndexKey.unpackIndexedValue(key);
        if (indexedValue > filterEndValue) {
            return KeyPattern.MATCH_RESULT_UNSUCCESS;
        } else if (indexedValue == filterEndValue) {
            if (filterBeginValue != filterEndValue) {
                return KeyPattern.MATCH_RESULT_UNSUCCESS;
            }

            return RangeIndexKey.unpackType(key) == RangeIndexKey.Type.DOT ? KeyPattern.MATCH_RESULT_SUCCESS : KeyPattern.MATCH_RESULT_CONTINUE;
        }

        if (processedIds != null && processedIds.contains(id)) {
            if (RangeIndexKey.unpackType(key) == RangeIndexKey.Type.END) {
                processedIds.remove(id);
            }
            return KeyPattern.MATCH_RESULT_CONTINUE;
        }

        if (RangeIndexKey.unpackType(key) == RangeIndexKey.Type.BEGIN) {
            if (processedIds == null) {
                processedIds = new HashSet<>();
            }
            processedIds.add(id);
        }
        return KeyPattern.MATCH_RESULT_SUCCESS;
    }
}
