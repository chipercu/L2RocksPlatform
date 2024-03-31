package com.fuzzy.database.domainobject;

import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import com.google.common.collect.TreeRangeSet;
import com.fuzzy.database.DataCommand;
import com.fuzzy.database.domainobject.DataEnumerable;
import com.fuzzy.database.domainobject.DomainObject;
import com.fuzzy.database.domainobject.DomainObjectEditable;
import com.fuzzy.database.domainobject.Value;
import com.fuzzy.database.domainobject.filter.EmptyFilter;
import com.fuzzy.database.domainobject.iterator.IteratorEntity;
import com.fuzzy.database.exception.ClosedObjectException;
import com.fuzzy.database.exception.DatabaseException;
import com.fuzzy.database.exception.ForeignDependencyException;
import com.fuzzy.database.provider.*;
import com.fuzzy.database.schema.*;
import com.fuzzy.database.utils.HashIndexUtils;
import com.fuzzy.database.utils.PrefixIndexUtils;
import com.fuzzy.database.utils.RangeIndexUtils;
import com.fuzzy.database.utils.TypeConvert;
import com.fuzzy.database.utils.key.FieldKey;
import com.fuzzy.database.utils.key.HashIndexKey;
import com.fuzzy.database.utils.key.IntervalIndexKey;
import com.fuzzy.database.utils.key.RangeIndexKey;

import java.io.Serializable;
import java.util.*;

public class Transaction extends DataEnumerable implements AutoCloseable {

    private DBTransaction transaction = null;
    private DataCommand dataCommand = null;
    private boolean closed = false;
    private boolean foreignFieldEnabled = true;
    private final Map<String, Objects> deletingObjects = new HashMap<>();

    protected Transaction(DBProvider dbProvider, Boolean reloadSchema) {
        super(dbProvider, reloadSchema);
    }

    public boolean isForeignFieldEnabled() {
        return foreignFieldEnabled;
    }

    public void setForeignFieldEnabled(boolean value) {
        this.foreignFieldEnabled = value;
    }

    public DBTransaction getDBTransaction() throws DatabaseException {
        ensureTransaction();
        return transaction;
    }

    public DataCommand getDataCommand() throws DatabaseException {
        ensureTransaction();
        dataCommand = new DataCommand(transaction, schema.getDbSchema());
        return dataCommand;
    }

    public <T extends DomainObject & DomainObjectEditable> T create(final Class<T> clazz) throws DatabaseException {
        ensureTransaction();

        StructEntity entity = Schema.getEntity(clazz);

        long id = transaction.nextId(entity.getColumnFamily());

        T domainObject = buildDomainObject(DomainObject.getConstructor(clazz), id, Collections.emptyList());
        domainObject._setAsJustCreated();

        //Принудительно указываем, что все поля отредактированы - иначе для не инициализированных полей не правильно построятся индексы
        for (Field field: entity.getFields()) {
            domainObject.set(field.getNumber(), null);
        }

        return domainObject;
    }

    public <T extends DomainObject & DomainObjectEditable> void save(final T object) throws DatabaseException {
        com.fuzzy.database.domainobject.Value<Serializable>[] newValues = object.getNewValues();
        if (newValues == null) {
            return;
        }

        ensureTransaction();

        final String columnFamily = object.getStructEntity().getColumnFamily();
        final com.fuzzy.database.domainobject.Value<Serializable>[] loadedValues = object.getLoadedValues();

        // update hash-indexed values
        for (HashIndex index: object.getStructEntity().getHashIndexes()) {
            if (anyChanged(index.sortedFields, newValues)) {
                tryLoadFields(columnFamily, object, index.sortedFields, loadedValues);
                updateIndexedValue(index, object, loadedValues, newValues, transaction);
            }
        }

        // update prefix-indexed values
        for (PrefixIndex index: object.getStructEntity().getPrefixIndexes()) {
            if (anyChanged(index.sortedFields, newValues)) {
                tryLoadFields(columnFamily, object, index.sortedFields, loadedValues);
                updateIndexedValue(index, object, loadedValues, newValues, transaction);
            }
        }

        // update interval-indexed values
        for (IntervalIndex index: object.getStructEntity().getIntervalIndexes()) {
            if (anyChanged(index.sortedFields, newValues)) {
                tryLoadFields(columnFamily, object, index.sortedFields, loadedValues);
                updateIndexedValue(index, object, loadedValues, newValues, transaction);
            }
        }

        // update range-indexed values
        for (RangeIndex index: object.getStructEntity().getRangeIndexes()) {
            if (anyChanged(index.sortedFields, newValues)) {
                tryLoadFields(columnFamily, object, index.sortedFields, loadedValues);
                updateIndexedValue(index, object, loadedValues, newValues, transaction);
            }
        }

        // update self-object
        if (object._isJustCreated()) {
            transaction.put(columnFamily, new FieldKey(object.getId()).pack(), TypeConvert.EMPTY_BYTE_ARRAY);
        }
        for (int i = 0; i < newValues.length; ++i) {
            com.fuzzy.database.domainobject.Value<Serializable> newValue = newValues[i];
            if (newValue == null) {
                continue;
            }

            Field field = object.getStructEntity().getFields()[i];
            Object value = newValue.getValue();

            validateUpdatingValue(object, field, value);
            if (object._isJustCreated() && value == null) {
                continue;
            }

            byte[] key = new FieldKey(object.getId(), field.getNameBytes()).pack();
            byte[] bValue = TypeConvert.pack(field.getType(), value, field.getConverter());
            transaction.put(columnFamily, key, bValue);
        }

        object._flushNewValues();
    }

    public <T extends DomainObject & DomainObjectEditable> void remove(final T obj) throws DatabaseException {
        ensureTransaction();

        validateForeignValues(obj);

        String columnFamily = obj.getStructEntity().getColumnFamily();
        deletingObjects.computeIfAbsent(columnFamily, s -> new Objects(obj.getStructEntity())).add(obj);
    }

    private void deleteObjects() throws DatabaseException {
        for (Map.Entry<String, Objects> entry : deletingObjects.entrySet()) {
            String columnFamily = entry.getKey();
            StructEntity entity = entry.getValue().entity;
            com.fuzzy.database.domainobject.Value<Serializable>[] loadedValues = new com.fuzzy.database.domainobject.Value[entity.getFields().length];

            for (Range<Long> range : entry.getValue().ids.asRanges()) {
                for (long objId = range.lowerEndpoint(); objId < range.upperEndpoint(); ++objId) {
                    Arrays.fill(loadedValues, null);

                    // delete hash-indexed values
                    for (HashIndex index : entity.getHashIndexes()) {
                        tryLoadFields(columnFamily, objId, index.sortedFields, loadedValues);
                        removeIndexedValue(index, objId, loadedValues, transaction);
                    }

                    // delete prefix-indexed values
                    for (PrefixIndex index : entity.getPrefixIndexes()) {
                        tryLoadFields(columnFamily, objId, index.sortedFields, loadedValues);
                        removeIndexedValue(index, objId, loadedValues, transaction);
                    }

                    // delete interval-indexed values
                    for (IntervalIndex index : entity.getIntervalIndexes()) {
                        tryLoadFields(columnFamily, objId, index.sortedFields, loadedValues);
                        removeIndexedValue(index, objId, loadedValues, transaction);
                    }

                    // delete range-indexed values
                    for (RangeIndex index : entity.getRangeIndexes()) {
                        tryLoadFields(columnFamily, objId, index.sortedFields, loadedValues);
                        removeIndexedValue(index, objId, loadedValues, transaction);
                    }
                }

                // delete self-object
                transaction.singleDeleteRange(columnFamily,
                        FieldKey.buildKeyPrefix(range.lowerEndpoint()),
                        FieldKey.buildKeyPrefix(range.upperEndpoint())
                );
            }
        }
    }

    @Override
    public boolean isMarkedForDeletion(StructEntity entity, long objId) {
        Objects objs = deletingObjects.get(entity.getColumnFamily());
        return objs != null && objs.ids.contains(objId);
    }

    public <T extends DomainObject & DomainObjectEditable> void removeAll(Class<T> objClass) throws DatabaseException {
        ensureTransaction();

        StructEntity entity = Schema.getEntity(objClass);

        validateForeignValues(entity);

        Objects objects = deletingObjects.computeIfAbsent(entity.getColumnFamily(), s -> new Objects(entity));
        try (IteratorEntity<T> i = find(objClass, EmptyFilter.INSTANCE, Collections.emptySet())) {
            while (i.hasNext()) {
                objects.add(i.next());
            }
        }
    }

    @Override
    public DBIterator createIterator(String columnFamily) throws DatabaseException {
        ensureTransaction();

        return transaction.createIterator(columnFamily);
    }

    public void commit() throws DatabaseException {
        if (transaction != null) {
            deleteObjects();
            transaction.commit();
        }
        close();
    }

    public boolean isClosed() {
        return closed;
    }

    @Override
    public void close() throws DatabaseException {
        closed = true;
        try (DBTransaction t = transaction) {
            transaction = null;
        }
    }

    private void ensureTransaction() throws DatabaseException {
        if (closed) {
            throw new ClosedObjectException(this.getClass());
        }

        if (transaction == null) {
            transaction = getDbProvider().beginTransaction();
        }
    }

    private void tryLoadFields(String columnFamily, DomainObject obj, List<Field> fields, com.fuzzy.database.domainobject.Value<Serializable>[] loadedValues) throws DatabaseException {
        if (!obj._isJustCreated()) {
            tryLoadFields(columnFamily, obj.getId(), fields, loadedValues);
        }
    }

    private void tryLoadFields(String columnFamily, long objId, List<Field> fields, com.fuzzy.database.domainobject.Value<Serializable>[] loadedValues) throws DatabaseException {
        for (Field field: fields) {
            tryLoadField(columnFamily, objId, field, loadedValues);
        }
    }

    private void tryLoadField(String columnFamily, long id, Field field, com.fuzzy.database.domainobject.Value<Serializable>[] loadedValues) throws DatabaseException {
        if (loadedValues[field.getNumber()] != null) {
            return;
        }

        final byte[] key = new FieldKey(id, field.getNameBytes()).pack();
        final byte[] value = transaction.getValue(columnFamily, key);
        loadedValues[field.getNumber()] = com.fuzzy.database.domainobject.Value.of(TypeConvert.unpack(field.getType(), value, field.getConverter()));
    }

    private static void updateIndexedValue(HashIndex index, DomainObject obj, com.fuzzy.database.domainobject.Value<Serializable>[] prevValues, com.fuzzy.database.domainobject.Value<Serializable>[] newValues, DBTransaction transaction) throws DatabaseException {
        final HashIndexKey indexKey = new HashIndexKey(obj.getId(), index);

        if (!obj._isJustCreated()) {
            // Remove old value-index
            HashIndexUtils.setHashValues(index.sortedFields, prevValues, indexKey.getFieldValues());
            transaction.delete(index.columnFamily, indexKey.pack());
        }

        // Add new value-index
        setHashValues(index.sortedFields, prevValues, newValues, indexKey.getFieldValues());
        transaction.put(index.columnFamily, indexKey.pack(), TypeConvert.EMPTY_BYTE_ARRAY);
    }

    private static void removeIndexedValue(HashIndex index, long id, com.fuzzy.database.domainobject.Value<Serializable>[] values, DBTransaction transaction) throws DatabaseException {
        final HashIndexKey indexKey = new HashIndexKey(id, index);

        HashIndexUtils.setHashValues(index.sortedFields, values, indexKey.getFieldValues());
        transaction.singleDelete(index.columnFamily, indexKey.pack());
    }

    private static void updateIndexedValue(PrefixIndex index, DomainObject obj, com.fuzzy.database.domainobject.Value<Serializable>[] prevValues, com.fuzzy.database.domainobject.Value<Serializable>[] newValues, DBTransaction transaction) throws DatabaseException {
        List<String> deletingLexemes = new ArrayList<>();
        List<String> insertingLexemes = new ArrayList<>();
        PrefixIndexUtils.diffIndexedLexemes(index.sortedFields, prevValues, newValues, deletingLexemes, insertingLexemes);

        if (!obj._isJustCreated()) {
            PrefixIndexUtils.removeIndexedLexemes(index, obj.getId(), deletingLexemes, transaction);
        }
        PrefixIndexUtils.insertIndexedLexemes(index, obj.getId(), insertingLexemes, transaction);
    }

    private static void removeIndexedValue(PrefixIndex index, long id, com.fuzzy.database.domainobject.Value<Serializable>[] values, DBTransaction transaction) throws DatabaseException {
        SortedSet<String> lexemes = PrefixIndexUtils.buildSortedSet();
        for (Field field : index.sortedFields) {
            PrefixIndexUtils.splitIndexingTextIntoLexemes((String) values[field.getNumber()].getValue(), lexemes);
        }

        PrefixIndexUtils.removeIndexedLexemes(index, id, lexemes, transaction);
    }

    private static void updateIndexedValue(IntervalIndex index, DomainObject obj, com.fuzzy.database.domainobject.Value<Serializable>[] prevValues, com.fuzzy.database.domainobject.Value<Serializable>[] newValues, DBTransaction transaction) throws DatabaseException {
        final List<Field> hashedFields = index.getHashedFields();
        final Field indexedField = index.getIndexedField();
        final IntervalIndexKey indexKey = new IntervalIndexKey(obj.getId(), new long[hashedFields.size()], index);

        if (!obj._isJustCreated()) {
            // Remove old value-index
            HashIndexUtils.setHashValues(hashedFields, prevValues, indexKey.getHashedValues());
            indexKey.setIndexedValue(prevValues[indexedField.getNumber()].getValue());
            transaction.delete(index.columnFamily, indexKey.pack());
        }

        // Add new value-index
        setHashValues(hashedFields, prevValues, newValues, indexKey.getHashedValues());
        indexKey.setIndexedValue(getValue(indexedField, prevValues, newValues));
        transaction.put(index.columnFamily, indexKey.pack(), TypeConvert.EMPTY_BYTE_ARRAY);
    }

    private static void removeIndexedValue(IntervalIndex index, long id, com.fuzzy.database.domainobject.Value<Serializable>[] values, DBTransaction transaction) throws DatabaseException {
        final List<Field> hashedFields = index.getHashedFields();
        final IntervalIndexKey indexKey = new IntervalIndexKey(id, new long[hashedFields.size()], index);

        HashIndexUtils.setHashValues(hashedFields, values, indexKey.getHashedValues());
        indexKey.setIndexedValue(values[index.getIndexedField().getNumber()].getValue());

        transaction.singleDelete(index.columnFamily, indexKey.pack());
    }

    private static void updateIndexedValue(RangeIndex index, DomainObject obj, com.fuzzy.database.domainobject.Value<Serializable>[] prevValues, com.fuzzy.database.domainobject.Value<Serializable>[] newValues, DBTransaction transaction) throws DatabaseException {
        final List<Field> hashedFields = index.getHashedFields();
        final RangeIndexKey indexKey = new RangeIndexKey(obj.getId(), new long[hashedFields.size()], index);

        if (!obj._isJustCreated()) {
            // Remove old value-index
            HashIndexUtils.setHashValues(hashedFields, prevValues, indexKey.getHashedValues());
            RangeIndexUtils.removeIndexedRange(index, indexKey,
                    prevValues[index.getBeginIndexedField().getNumber()].getValue(),
                    prevValues[index.getEndIndexedField().getNumber()].getValue(),
                    transaction, transaction::delete);
        }

        // Add new value-index
        setHashValues(hashedFields, prevValues, newValues, indexKey.getHashedValues());
        RangeIndexUtils.insertIndexedRange(index, indexKey,
                getValue(index.getBeginIndexedField(), prevValues, newValues),
                getValue(index.getEndIndexedField(), prevValues, newValues),
                transaction);
    }

    private static void removeIndexedValue(RangeIndex index, long id, com.fuzzy.database.domainobject.Value<Serializable>[] values, DBTransaction transaction) throws DatabaseException {
        final List<Field> hashedFields = index.getHashedFields();
        final RangeIndexKey indexKey = new RangeIndexKey(id, new long[hashedFields.size()], index);

        HashIndexUtils.setHashValues(hashedFields, values, indexKey.getHashedValues());
        RangeIndexUtils.removeIndexedRange(index, indexKey,
                values[index.getBeginIndexedField().getNumber()].getValue(),
                values[index.getEndIndexedField().getNumber()].getValue(),
                transaction, transaction::singleDelete);
    }

    private static void setHashValues(List<Field> fields, com.fuzzy.database.domainobject.Value<Serializable>[] prevValues, com.fuzzy.database.domainobject.Value<Serializable>[] newValues, long[] destination) {
        for (int i = 0; i < fields.size(); ++i) {
            Field field = fields.get(i);
            Object value = getValue(field, prevValues, newValues);
            destination[i] = HashIndexUtils.buildHash(field.getType(), value, field.getConverter());
        }
    }

    private static Object getValue(Field field, com.fuzzy.database.domainobject.Value<Serializable>[] prevValues, com.fuzzy.database.domainobject.Value<Serializable>[] newValues) {
        com.fuzzy.database.domainobject.Value<Serializable> value = newValues[field.getNumber()];
        if (value == null) {
            value = prevValues[field.getNumber()];
        }
        return value.getValue();
    }

    private static boolean anyChanged(List<Field> fields, Value<Serializable>[] newValues) {
        for (Field field: fields) {
            if (newValues[field.getNumber()] != null) {
                return true;
            }
        }
        return false;
    }

    private void validateUpdatingValue(DomainObject obj, Field field, Object value) throws DatabaseException {
        if (value == null) {
            return;
        }

        if (!foreignFieldEnabled || !field.isForeign()) {
            return;
        }

        long fkeyIdValue = (Long) value;
        if (transaction.getValue(field.getForeignDependency().getColumnFamily(), new FieldKey(fkeyIdValue).pack()) == null ||
                isMarkedForDeletion(field.getForeignDependency(), fkeyIdValue)) {
            throw new ForeignDependencyException(obj.getId(), obj.getStructEntity().getObjectClass(), field, fkeyIdValue);
        }
    }

    private void validateForeignValues(DomainObject obj) throws DatabaseException {
        if (!foreignFieldEnabled) {
            return;
        }

        List<StructEntity.Reference> references = obj.getStructEntity().getReferencingForeignFields();
        if (references.isEmpty()) {
            return;
        }

        for (StructEntity.Reference ref : references) {
            KeyPattern keyPattern = HashIndexKey.buildKeyPattern(ref.fieldIndex, obj.getId());
            try (DBIterator i = transaction.createIterator(ref.fieldIndex.columnFamily)) {
                KeyValue keyValue = i.seek(keyPattern);
                if (keyValue != null) {
                    long referencingId = HashIndexKey.unpackId(keyValue.getKey());
                    if (!isMarkedForDeletion(Schema.getEntity(ref.objClass), referencingId)) {
                        throw new ForeignDependencyException(obj.getId(), obj.getStructEntity().getObjectClass(), referencingId, ref.objClass);
                    }
                }
            }
        }
    }

    private void validateForeignValues(StructEntity entity) throws DatabaseException {
        if (!foreignFieldEnabled) {
            return;
        }

        List<StructEntity.Reference> references = entity.getReferencingForeignFields();
        if (references.isEmpty()) {
            return;
        }

        for (StructEntity.Reference ref : references) {
            if (ref.objClass.equals(entity.getObjectClass())) {
                continue;
            }

            Objects objs = deletingObjects.get(Schema.getEntity(ref.objClass).getColumnFamily());
            KeyPattern keyPattern = HashIndexKey.buildKeyPatternForLastKey(ref.fieldIndex);
            keyPattern.setForBackward(true);
            try (DBIterator i = transaction.createIterator(ref.fieldIndex.columnFamily)) {
                for (KeyValue keyValue = i.seek(keyPattern); keyValue != null; keyValue = i.step(DBIterator.StepDirection.BACKWARD)) {
                    if (HashIndexKey.unpackFirstIndexedValue(keyValue.getKey()) == 0) {
                        break;
                    }
                    long referencingId = HashIndexKey.unpackId(keyValue.getKey());
                    if (objs != null && objs.ids.contains(referencingId)) {
                        continue;
                    }

                    long objId = HashIndexKey.unpackFirstIndexedValue(keyValue.getKey());
                    throw new ForeignDependencyException(objId, entity.getObjectClass(), referencingId, ref.objClass);
                }
            }
        }
    }

    private static class Objects {

        final StructEntity entity;
        final RangeSet<Long> ids = TreeRangeSet.create();

        Objects(StructEntity entity) {
            this.entity = entity;
        }

        void add(DomainObject obj) {
            ids.add(Range.closedOpen(obj.getId(), obj.getId() + 1));
        }
    }
}
