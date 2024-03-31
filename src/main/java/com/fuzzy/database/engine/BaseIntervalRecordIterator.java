package com.fuzzy.database.engine;

import com.fuzzy.database.Record;
import com.fuzzy.database.domainobject.filter.BaseIntervalFilter;
import com.fuzzy.database.domainobject.filter.SortDirection;
import com.fuzzy.database.engine.BaseIndexRecordIterator;
import com.fuzzy.database.exception.DatabaseException;
import com.fuzzy.database.exception.IllegalTypeException;
import com.fuzzy.database.provider.DBDataReader;
import com.fuzzy.database.provider.DBIterator;
import com.fuzzy.database.provider.KeyPattern;
import com.fuzzy.database.provider.KeyValue;
import com.fuzzy.database.schema.dbstruct.DBBaseIntervalIndex;
import com.fuzzy.database.schema.dbstruct.DBField;
import com.fuzzy.database.schema.dbstruct.DBTable;
import com.fuzzy.database.utils.HashIndexUtils;
import com.fuzzy.database.utils.IntervalIndexUtils;
import com.fuzzy.database.utils.key.BaseIntervalIndexKey;

import java.util.*;

abstract class BaseIntervalRecordIterator<F extends BaseIntervalFilter> extends BaseIndexRecordIterator {

    private final List<DBField> checkedFilterFields;
    private final List<Object> filterValues;
    private final DBIterator.StepDirection direction;
    private final KeyPattern indexPattern;

    private KeyValue indexKeyValue;

    final long filterBeginValue, filterEndValue;

    BaseIntervalRecordIterator(DBTable table, F filter, SortDirection direction, DBDataReader dataReader) throws DatabaseException {
        super(table, dataReader);
        this.direction = direction == SortDirection.ASC ? DBIterator.StepDirection.FORWARD : DBIterator.StepDirection.BACKWARD;

        Map<Integer, Object> filters = filter.getHashedValues();
        DBBaseIntervalIndex index = getIndex(filter, table);

        List<DBField> filterFields = null;
        List<Object> filterValues = null;

        long[] values = new long[index.getHashFieldIds().length];
        for (int i = 0; i < index.getHashFieldIds().length; ++i) {
            DBField field = table.getField(index.getHashFieldIds()[i]);
            Object value = filters.get(field.getId());
            if (field.getType() != value.getClass()) {
                throw new IllegalTypeException(field.getType(), value.getClass());
            }

            values[i] = HashIndexUtils.buildHash(field.getType(), value, null);
            if (HashIndexUtils.toLongCastable(field.getType())) {
                continue;
            }
            if (filterFields == null) {
                filterFields = new ArrayList<>();
                filterValues = new ArrayList<>();
            }
            filterFields.add(field);
            filterValues.add(value);
        }

        index.checkIndexedFieldType(filter.getBeginValue().getClass(), table);
        index.checkIndexedFieldType(filter.getEndValue().getClass(), table);

        this.checkedFilterFields = filterFields != null ? filterFields : Collections.emptyList();
        this.filterValues = filterValues;
        this.filterBeginValue = IntervalIndexUtils.castToLong(filter.getBeginValue());
        this.filterEndValue = IntervalIndexUtils.castToLong(filter.getEndValue());
        IntervalIndexUtils.checkInterval(filterBeginValue, filterEndValue);

        switch (this.direction) {
            case FORWARD:
                this.indexPattern = BaseIntervalIndexKey.buildLeftBorder(values, filterBeginValue, index);
                break;
            case BACKWARD:
                this.indexPattern = BaseIntervalIndexKey.buildRightBorder(values, filterEndValue, index);
                break;
            default:
                throw new IllegalArgumentException("direction = " + direction);
        }
        this.indexKeyValue = seek(indexIterator, indexPattern);

        nextImpl();
    }

    abstract DBBaseIntervalIndex getIndex(F filter, DBTable table);
    abstract KeyValue seek(DBIterator indexIterator, KeyPattern pattern) throws DatabaseException;

    @Override
    protected void nextImpl() throws DatabaseException {
        while (indexKeyValue != null) {
            final long id = BaseIntervalIndexKey.unpackId(indexKeyValue.getKey());
            final int res = matchKey(id, indexKeyValue.getKey());
            if (res == KeyPattern.MATCH_RESULT_SUCCESS) {
                nextRecord = findRecord(id);
            } else if (res == KeyPattern.MATCH_RESULT_CONTINUE) {
                nextRecord = null;
            } else {
                break;
            }
            indexKeyValue = indexIterator.step(direction);
            if (indexKeyValue != null && indexPattern.match(indexKeyValue.getKey()) != KeyPattern.MATCH_RESULT_SUCCESS) {
                indexKeyValue = null;
            }
            if (nextRecord != null) {
                return;
            }
        }

        nextRecord = null;
        close();
    }

    /**
     * @return KeyPattern.MATCH_RESULT_*
     */
    abstract int matchKey(long id, byte[] key);

    @Override
    protected boolean checkFilter(Record record) throws DatabaseException {
        for (int i = 0; i < checkedFilterFields.size(); ++i) {
            DBField field = checkedFilterFields.get(i);
            if (!HashIndexUtils.equals(field.getType(), filterValues.get(i), record.getValues()[field.getId()])) {
                return false;
            }
        }
        return true;
    }
}

