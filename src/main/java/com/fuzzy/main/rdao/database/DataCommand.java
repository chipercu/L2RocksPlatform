package com.fuzzy.main.rdao.database;

import com.fuzzy.main.rdao.database.exception.DatabaseException;
import com.fuzzy.main.rdao.database.exception.ForeignDependencyException;
import com.fuzzy.main.rdao.database.exception.InvalidValueException;
import com.fuzzy.main.rdao.database.exception.UnexpectedFieldValueException;
import com.fuzzy.main.rdao.database.provider.DBDataCommand;
import com.fuzzy.main.rdao.database.provider.DBIterator;
import com.fuzzy.main.rdao.database.provider.KeyPattern;
import com.fuzzy.main.rdao.database.provider.KeyValue;
import com.fuzzy.main.rdao.database.schema.dbstruct.*;
import com.fuzzy.main.rdao.database.schema.table.FieldReference;
import com.fuzzy.main.rdao.database.utils.*;
import com.fuzzy.main.rdao.database.utils.key.FieldKey;
import com.fuzzy.main.rdao.database.utils.key.HashIndexKey;
import com.fuzzy.main.rdao.database.utils.key.IntervalIndexKey;
import com.fuzzy.main.rdao.database.utils.key.RangeIndexKey;

import java.util.*;

public class DataCommand extends DataReadCommand {

    private final DBDataCommand dataCommand;

    public DataCommand(DBDataCommand dataCommand, DBSchema schema) {
        super(dataCommand, schema);
        this.dataCommand = dataCommand;
    }

    public DBDataCommand getDBCommand() {
        return dataCommand;
    }

    public long insertRecord(String tableName, String namespace, String[] fields, Object[] values) throws DatabaseException {
        return insertRecord(tableName,
                namespace,
                TableUtils.sortValuesByFieldOrder(tableName, namespace, fields, values, schema));
    }

    /**
     *
     * @param values должен совпадать с количеством столбцов таблицы RocksDB.
     *               Значения записываются в поля с идентифкатором соответствующего элемента массива values[]
     * @return id созданного объекта или -1, если объект не был создан
     */
    public long insertRecord(String tableName, String namespace, Object[] values) throws DatabaseException {
        if (values == null) {
            return -1;
        }
        DBTable table = schema.getTable(tableName, namespace);
        if (values.length != table.getSortedFields().size()) {
            throw new UnexpectedFieldValueException("Size of inserting values " + values.length + " doesn't equal table field size " + table.getSortedFields().size());
        }
        long id = dataCommand.nextId(table.getDataColumnFamily());

        Record record = new Record(id, values);
        // update hash-indexed values
        for (DBHashIndex index : table.getHashIndexes()) {
            createIndexedValue(index, record, table);
        }

        // update prefix-indexed values
        for (DBPrefixIndex index : table.getPrefixIndexes()) {
            createIndexedValue(index, record, table);
        }

        // update interval-indexed values
        for (DBIntervalIndex index : table.getIntervalIndexes()) {
            createIndexedValue(index, record, table);
        }

        // update range-indexed values
        for (DBRangeIndex index: table.getRangeIndexes()) {
            createIndexedValue(index, record, table);
        }

        // update self-object
        dataCommand.put(table.getDataColumnFamily(), new FieldKey(record.getId()).pack(), TypeConvert.EMPTY_BYTE_ARRAY);
        for (int i = 0; i < values.length; ++i) {
            Object newValue = values[i];
            if (newValue == null) {
                continue;
            }

            DBField field = table.getField(i);

            validateUpdatingValue(record, field, newValue, table);

            byte[] key = new FieldKey(record.getId(), TypeConvert.pack(field.getName())).pack();
            byte[] bValue = TypeConvert.pack(field.getType(), newValue, null);
            dataCommand.put(table.getDataColumnFamily(), key, bValue);
        }

        return id;
    }

    public long updateRecord(String tableName, String namespace, Record record) throws DatabaseException {
        Record prevRecord = getById(tableName, namespace, record.getId());
        return updateRecordSortedValues(tableName, namespace, record.getId(), record.getValues(), prevRecord);
    }

    public long updateRecord(String tableName, String namespace, long id, String[] fields, Object[] values) throws DatabaseException {
        if (fields == null) {
            return -1;
        }
        if (id <= 0) {
            throw new InvalidValueException("Invalid id=" + id);
        }
        Record prevRecord = getById(tableName, namespace, id);
        Object[] newValues = TableUtils.sortValuesByFieldOrder(tableName, namespace, fields, values, prevRecord.getValues(), schema);
        return updateRecordSortedValues(tableName, namespace, id, newValues, prevRecord);
    }

    private long updateRecordSortedValues(String tableName, String namespace, long id, Object[] newValues, Record prevRecord) throws DatabaseException {
        if (newValues == null) {
            return -1;
        }
        if (id <= 0) {
            throw new InvalidValueException("Invalid id=" + id);
        }
        DBTable table = schema.getTable(tableName, namespace);
        if (newValues.length != table.getSortedFields().size()) {
            throw new UnexpectedFieldValueException("Size of inserting values " + newValues.length + " doesn't equal table field size " + table.getSortedFields().size());
        }

        Record record = new Record(id, newValues);
        // update hash-indexed values
        for (DBHashIndex index : table.getHashIndexes()) {
            if (anyChanged(index.getFieldIds(), record, prevRecord)) {
                updateIndexedValue(index, prevRecord, record, table);
            }
        }

        // update prefix-indexed values
        for (DBPrefixIndex index : table.getPrefixIndexes()) {
            if (anyChanged(index.getFieldIds(), record, prevRecord)) {
                updateIndexedValue(index, prevRecord, record, table);
            }
        }

        // update interval-indexed values
        for (DBIntervalIndex index : table.getIntervalIndexes()) {
            if (anyChanged(index.getFieldIds(), record, prevRecord)) {
                updateIndexedValue(index, prevRecord, record, table);
            }
        }

        // update range-indexed values
        for (DBRangeIndex index: table.getRangeIndexes()) {
            if (anyChanged(index.getFieldIds(), record, prevRecord)) {
                updateIndexedValue(index, prevRecord, record, table);
            }
        }

        // update self-object
        for (int i = 0; i < newValues.length; ++i) {
            Object newValue = newValues[i];
            DBField field = table.getField(i);
            if (newValue == null) {
                //todo Убрать после удаления TypeConverter'ов
                byte[] key = new FieldKey(record.getId(), TypeConvert.pack(field.getName())).pack();
                dataCommand.delete(table.getDataColumnFamily(), key);
                continue;
            }

            validateUpdatingValue(record, field, newValue, table);

            byte[] key = new FieldKey(record.getId(), TypeConvert.pack(field.getName())).pack();
            byte[] bValue = TypeConvert.pack(field.getType(), newValue, null);
            dataCommand.put(table.getDataColumnFamily(), key, bValue);
        }

        return id;
    }

    public void deleteRecord(String tableName, String namespace, long id) throws DatabaseException {
        DBTable table = schema.getTable(tableName, namespace);
        validateForeignValues(table, id);

        Record record = getById(tableName, namespace, id);
        // delete hash-indexed values
        for (DBHashIndex index : table.getHashIndexes()) {
            removeIndexedValue(index, record, table);
        }

        // delete prefix-indexed values
        for (DBPrefixIndex index : table.getPrefixIndexes()) {
            removeIndexedValue(index, record, table);
        }

        // delete interval-indexed values
        for (DBIntervalIndex index : table.getIntervalIndexes()) {
            removeIndexedValue(index, record, table);
        }

        // delete range-indexed values
        for (DBRangeIndex index : table.getRangeIndexes()) {
            removeIndexedValue(index, record, table);
        }

        // delete self-object
        byte[] lowerEndpointId = FieldKey.buildKeyPrefix(record.getId());
        byte[] upperEndpointId = FieldKey.buildKeyPrefix(record.getId() + 1);
        dataCommand.singleDeleteRange(table.getDataColumnFamily(),
                lowerEndpointId,
                upperEndpointId
        );
    }

    public void clearTable(String table, String namespace) throws DatabaseException {
        try (RecordIterator ri = select(table, namespace)) {
            while (ri.hasNext()){
                deleteRecord(table, namespace, ri.next().getId());
            }
        }
    }

    private void validateForeignValues(DBTable table, long id) throws DatabaseException {
        Set<FieldReference> references = schema.getTableReferences(table.getName(), table.getNamespace());
        if (references.isEmpty()) {
            return;
        }
        for (FieldReference ref : references) {
            KeyPattern keyPattern = HashIndexKey.buildKeyPattern(ref.getHashIndex(), id);
            try (DBIterator i = dataCommand.createIterator(ref.getNamespace() + "." + ref.getName() + ".index")) {
                KeyValue keyValue = i.seek(keyPattern);
                if (keyValue != null) {
                    long referencingId = HashIndexKey.unpackId(keyValue.getKey());
                    throw new ForeignDependencyException(id, table.getName(), table.getNamespace(), referencingId, ref.getName(), ref.getNamespace());
                }
            }
        }
    }

    private static boolean anyChanged(int[] fieldIds, Record newRecord, Record prevRecord) {
        for (int fieldId : fieldIds) {
            if (!Objects.equals(newRecord.getValues()[fieldId], (prevRecord.getValues()[fieldId]))) {
                return true;
            }
        }
        return false;
    }

    private void removeIndexedValue(DBHashIndex index, Record record, DBTable table) throws DatabaseException {
        final HashIndexKey indexKey = new HashIndexKey(record.getId(), index);

        setHashValues(table.getFields(index.getFieldIds()), record, indexKey.getFieldValues());
        dataCommand.singleDelete(table.getIndexColumnFamily(), indexKey.pack());
    }

    private void createIndexedValue(DBHashIndex index, Record record, DBTable table) throws DatabaseException {
        final HashIndexKey indexKey = new HashIndexKey(record.getId(), index);

        // Add new value-index
        setHashValues(table.getFields(index.getFieldIds()), record, indexKey.getFieldValues());
        dataCommand.put(table.getIndexColumnFamily(), indexKey.pack(), TypeConvert.EMPTY_BYTE_ARRAY);
    }

    private void updateIndexedValue(DBHashIndex index, Record prevRecord, Record record, DBTable table) throws DatabaseException {
        removeIndexedValue(index, prevRecord, table);
        createIndexedValue(index, record, table);
    }

    private void removeIndexedValue(DBPrefixIndex index, Record record, DBTable table) throws DatabaseException {
        SortedSet<String> lexemes = PrefixIndexUtils.buildSortedSet();
        for (int fieldId : index.getFieldIds()) {
            PrefixIndexUtils.splitIndexingTextIntoLexemes((String) record.getValues()[fieldId], lexemes);
        }

        PrefixIndexUtils.removeIndexedLexemes(index, record.getId(), lexemes, table, dataCommand);
    }

    private void createIndexedValue(DBPrefixIndex index, Record record, DBTable table) throws DatabaseException {
        List<String> insertingLexemes = new ArrayList<>();
        PrefixIndexUtils.getIndexedLexemes(table.getFields(index.getFieldIds()), record.getValues(), insertingLexemes);
        PrefixIndexUtils.insertIndexedLexemes(index, record.getId(), insertingLexemes, table, dataCommand);
    }

    private void updateIndexedValue(DBPrefixIndex index, Record prevRecord, Record record, DBTable table) throws DatabaseException {
        List<String> deletingLexemes = new ArrayList<>();
        List<String> insertingLexemes = new ArrayList<>();
        PrefixIndexUtils.diffIndexedLexemes(index.getFieldIds(), prevRecord.getValues(), record.getValues(), deletingLexemes, insertingLexemes);
        PrefixIndexUtils.removeIndexedLexemes(index, record.getId(), deletingLexemes, table, dataCommand);
        createIndexedValue(index, record, table);
    }

    private void removeIndexedValue(DBIntervalIndex index, Record record, DBTable table) throws DatabaseException {
        final DBField[] hashedFields = table.getFields(index.getHashFieldIds());
        final DBField indexedField = table.getField(index.getIndexedFieldId());
        final IntervalIndexKey indexKey = new IntervalIndexKey(record.getId(), new long[hashedFields.length], index);

        setHashValues(hashedFields, record, indexKey.getHashedValues());
        indexKey.setIndexedValue(record.getValues()[indexedField.getId()]);

        dataCommand.singleDelete(table.getIndexColumnFamily(), indexKey.pack());
    }

    private void createIndexedValue(DBIntervalIndex index, Record record, DBTable table) throws DatabaseException {
        final DBField[] hashedFields = table.getFields(index.getHashFieldIds());
        final DBField indexedField = table.getField(index.getIndexedFieldId());
        final IntervalIndexKey indexKey = new IntervalIndexKey(record.getId(), new long[hashedFields.length], index);
        // Add new value-index
        setHashValues(hashedFields, record, indexKey.getHashedValues());
        indexKey.setIndexedValue(record.getValues()[indexedField.getId()]);
        dataCommand.put(table.getIndexColumnFamily(), indexKey.pack(), TypeConvert.EMPTY_BYTE_ARRAY);
    }

    private void updateIndexedValue(DBIntervalIndex index, Record prevRecord, Record record, DBTable table) throws DatabaseException {
        removeIndexedValue(index, prevRecord, table);
        createIndexedValue(index, record, table);
    }

    private void removeIndexedValue(DBRangeIndex index, Record record, DBTable table) throws DatabaseException {
        final DBField[] hashedFields = table.getFields(index.getHashFieldIds());
        final RangeIndexKey indexKey = new RangeIndexKey(record.getId(), new long[hashedFields.length], index);

        setHashValues(hashedFields, record, indexKey.getHashedValues());
        RangeIndexUtils.removeIndexedRange(index,
                indexKey,
                record.getValues()[index.getBeginFieldId()],
                record.getValues()[index.getEndFieldId()],
                table,
                dataCommand);
    }

    private void createIndexedValue(DBRangeIndex index, Record record, DBTable table) throws DatabaseException {
        final DBField[] hashedFields = table.getFields(index.getHashFieldIds());
        final RangeIndexKey indexKey = new RangeIndexKey(record.getId(), new long[hashedFields.length], index);
        // Add new value-index
        setHashValues(hashedFields, record, indexKey.getHashedValues());
        RangeIndexUtils.insertIndexedRange(index,
                indexKey,
                record.getValues()[index.getBeginFieldId()],
                record.getValues()[index.getEndFieldId()],
                table,
                dataCommand);
    }

    private void updateIndexedValue(DBRangeIndex index, Record prevRecord, Record record, DBTable table) throws DatabaseException {
        removeIndexedValue(index, prevRecord, table);
        createIndexedValue(index, record, table);
    }

    private static void setHashValues(DBField[] fields, Record record, long[] destination) {
        for (int i = 0; i < fields.length; ++i) {
            DBField field = fields[i];
            Object value = record.getValues()[field.getId()];
            destination[i] = HashIndexUtils.buildHash(field.getType(), value, null);
        }
    }

    private void validateUpdatingValue(Record record, DBField field, Object value, DBTable table) throws DatabaseException {
        if (value == null) {
            return;
        }

        if (!field.isForeignKey()) {
            return;
        }

        long fkeyIdValue = (Long) value;
        DBTable foreignTable = schema.getTableById(field.getForeignTableId());
        if (dataCommand.getValue(foreignTable.getDataColumnFamily(), new FieldKey(fkeyIdValue).pack()) == null) {
            throw new ForeignDependencyException(record.getId(),
                    table,
                    foreignTable,
                    field,
                    fkeyIdValue);
        }
    }
}
