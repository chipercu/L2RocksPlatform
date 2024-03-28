package com.fuzzy.main.rdao.database.domainobject;

import com.fuzzy.main.rdao.database.domainobject.filter.*;
import com.fuzzy.main.rdao.database.domainobject.iterator.*;
import com.fuzzy.main.rdao.database.exception.DatabaseException;
import com.fuzzy.main.rdao.database.exception.IllegalTypeException;
import com.fuzzy.main.rdao.database.exception.UnexpectedEndObjectException;
import com.fuzzy.main.rdao.database.provider.DBIterator;
import com.fuzzy.main.rdao.database.provider.DBProvider;
import com.fuzzy.main.rdao.database.provider.KeyPattern;
import com.fuzzy.main.rdao.database.provider.KeyValue;
import com.fuzzy.main.rdao.database.schema.Field;
import com.fuzzy.main.rdao.database.schema.Schema;
import com.fuzzy.main.rdao.database.schema.StructEntity;
import com.fuzzy.main.rdao.database.utils.TypeConvert;
import com.fuzzy.main.rdao.database.utils.key.FieldKey;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.Set;

public abstract class DataEnumerable {

    public static class NextState {

        private long nextId;

        private NextState(long recordId) {
            this.nextId = recordId;
        }

        public boolean isEmpty() {
            return nextId == -1;
        }

        public long getNextId() {
            return nextId;
        }

        public void reset() {
            nextId = -1;
        }
    }

    private final DBProvider dbProvider;
    protected final Schema schema;

    DataEnumerable(DBProvider dbProvider, Boolean reloadSchema) {
        this.dbProvider = dbProvider;
        this.schema = reloadSchema ?
                Schema.read(dbProvider)
                : Schema.readFromCache(dbProvider);
    }

    public DBProvider getDbProvider() {
        return dbProvider;
    }

    public abstract DBIterator createIterator(String columnFamily) throws DatabaseException;

    public abstract boolean isMarkedForDeletion(StructEntity entity, long objId);

    public <T extends DomainObject> T get(final Class<T> clazz, long id, final Set<Integer> loadingFields) throws DatabaseException {
        StructEntity entity = Schema.getEntity(clazz);

        if (isMarkedForDeletion(entity, id)) {
            return null;
        }

        try (DBIterator iterator = createIterator(entity.getColumnFamily())) {
            return seekObject(DomainObject.getConstructor(clazz), loadingFields, iterator, FieldKey.buildKeyPattern(id, entity.getFieldNames(loadingFields)));
        }
    }

    public <T extends DomainObject> T get(final Class<T> clazz, long id) throws DatabaseException {
        return get(clazz, id, null);
    }

    public <T extends DomainObject> IteratorEntity<T> find(final Class<T> clazz, Filter filter, final Set<Integer> loadingFields) throws DatabaseException {
        if (filter instanceof EmptyFilter) {
            return new AllIterator<>(this, clazz, loadingFields);
        } else if (filter instanceof HashFilter) {
            return new HashIndexIterator<>(this, clazz, loadingFields, (HashFilter) filter);
        } else if (filter instanceof PrefixFilter) {
            return new PrefixIndexIterator<>(this, clazz, loadingFields, (PrefixFilter) filter);
        } else if (filter instanceof IntervalFilter) {
            return new IntervalIndexIterator<>(this, clazz, loadingFields, (IntervalFilter) filter);
        } else if (filter instanceof RangeFilter) {
            return new RangeIndexIterator<>(this, clazz, loadingFields, (RangeFilter) filter);
        } else if (filter instanceof IdFilter) {
            return new IdIterator<>(this, clazz, loadingFields, (IdFilter) filter);
        }

        throw new IllegalArgumentException("Unknown filter type " + filter.getClass());
    }

    public <T extends DomainObject> IteratorEntity<T> find(final Class<T> clazz, Filter filter) throws DatabaseException {
        return find(clazz, filter, null);
    }

    public <T extends DomainObject> T buildDomainObject(final Constructor<T> constructor, long id, Collection<Integer> preInitializedFields) {
        T obj = buildDomainObject(constructor, id);
        if (preInitializedFields == null) {
            for (Field field : obj.getStructEntity().getFields()) {
                obj._setLoadedField(field.getNumber(), null);
            }
        } else {
            for (Integer field : preInitializedFields) {
                obj._setLoadedField(field, null);
            }
        }
        return obj;
    }

    private <T extends DomainObject> T buildDomainObject(final Constructor<T> constructor, long id) {
        try {
            return constructor.newInstance(id);
        } catch (ReflectiveOperationException e) {
            throw new IllegalTypeException(e);
        }
    }

    public <T extends DomainObject> T nextObject(final Constructor<T> constructor, Collection<Integer> preInitializedFields,
                                                 DBIterator iterator, NextState state, StructEntity entity) throws DatabaseException {
        if (state.isEmpty()) {
            return null;
        }

        T obj = buildDomainObject(constructor, state.nextId, preInitializedFields);
        state.nextId = readObject(obj, iterator);
        state.nextId = getNextNotMarkedForDeletion(entity, state.nextId, iterator);
        return obj;
    }

    public <T extends DomainObject> T seekObject(final Constructor<T> constructor, Collection<Integer> preInitializedFields,
                                                 DBIterator iterator, KeyPattern pattern) throws DatabaseException {
        KeyValue keyValue = iterator.seek(pattern);
        if (keyValue == null) {
            return null;
        }

        if (!FieldKey.unpackBeginningObject(keyValue.getKey())) {
            return null;
        }

        T obj = buildDomainObject(constructor, FieldKey.unpackId(keyValue.getKey()), preInitializedFields);
        readObject(obj, iterator);
        return obj;
    }

    public NextState seek(DBIterator iterator, KeyPattern pattern, StructEntity entity) throws DatabaseException {
        KeyValue keyValue = iterator.seek(pattern);
        if (keyValue == null) {
            return new NextState(-1);
        }

        if (!FieldKey.unpackBeginningObject(keyValue.getKey())) {
            return new NextState(-1);
        }

        long objId = FieldKey.unpackId(keyValue.getKey());
        return new NextState(getNextNotMarkedForDeletion(entity, objId, iterator));
    }

    private <T extends DomainObject> long readObject(T obj, DBIterator iterator) throws DatabaseException {
        KeyValue keyValue;
        while ((keyValue = iterator.next()) != null) {
            long id = FieldKey.unpackId(keyValue.getKey());
            if (id != obj.getId()) {
                if (!FieldKey.unpackBeginningObject(keyValue.getKey())) {
                    throw new UnexpectedEndObjectException(obj.getId(), id, FieldKey.unpackFieldName(keyValue.getKey()));
                }
                return id;
            }
            Field field = obj.getStructEntity().getField(new StructEntity.ByteArray(keyValue.getKey(), FieldKey.ID_BYTE_SIZE, keyValue.getKey().length));
            obj._setLoadedField(field.getNumber(), TypeConvert.unpack(field.getType(), keyValue.getValue(), field.getConverter()));
        }

        return -1;
    }

    private long getNextNotMarkedForDeletion(StructEntity entity, long startObjId, DBIterator iterator) throws DatabaseException {
        while (startObjId != -1 && isMarkedForDeletion(entity, startObjId)) {
            KeyValue keyValue;
            while ((keyValue = iterator.next()) != null) {
                if (FieldKey.unpackBeginningObject(keyValue.getKey())) {
                    break;
                }
            }

            startObjId = keyValue != null ? FieldKey.unpackId(keyValue.getKey()) : -1;
        }

        return startObjId;
    }
}
