package com.fuzzy.main.rdao.database.domainobject.iterator;

import com.fuzzy.main.rdao.database.domainobject.DataEnumerable;
import com.fuzzy.main.rdao.database.domainobject.DomainObject;
import com.fuzzy.main.rdao.database.domainobject.filter.IdFilter;
import com.fuzzy.main.rdao.database.exception.DatabaseException;
import com.fuzzy.main.rdao.database.provider.DBIterator;
import com.fuzzy.main.rdao.database.provider.KeyPattern;
import com.fuzzy.main.rdao.database.schema.Schema;
import com.fuzzy.main.rdao.database.schema.StructEntity;
import com.fuzzy.main.rdao.database.utils.key.FieldKey;

import java.lang.reflect.Constructor;
import java.util.NoSuchElementException;
import java.util.Set;

public class IdIterator<E extends DomainObject> implements IteratorEntity<E> {

    private final DataEnumerable dataEnumerable;
    private final Constructor<E> constructor;
    private final Set<Integer> loadingFields;
    private final DBIterator dataIterator;
    private final StructEntity entity;

    private final DataEnumerable.NextState state;
    private final long endId;

    public IdIterator(DataEnumerable dataEnumerable, Class<E> clazz, Set<Integer> loadingFields, IdFilter filter) throws DatabaseException {
        this.dataEnumerable = dataEnumerable;
        this.constructor = DomainObject.getConstructor(clazz);
        this.loadingFields = loadingFields;
        this.endId = filter.getToId();
        this.entity = Schema.getEntity(clazz);
        this.dataIterator = dataEnumerable.createIterator(entity.getColumnFamily());

        KeyPattern dataKeyPattern;
        if (loadingFields != null) {
            dataKeyPattern = new KeyPattern(FieldKey.buildKeyPrefix(filter.getFromId()), 0, FieldKey.buildInnerPatterns(entity.getFieldNames(loadingFields)));
        } else {
            dataKeyPattern = new KeyPattern(FieldKey.buildKeyPrefix(filter.getFromId()), 0);
        }
        this.state = dataEnumerable.seek(dataIterator, dataKeyPattern, entity);
        if (endReached()) {
            state.reset();
            close();
        }
    }

    @Override
    public boolean hasNext() {
        return !state.isEmpty();
    }

    @Override
    public E next() throws DatabaseException {
        if (state.isEmpty()) {
            throw new NoSuchElementException();
        }

        E result = dataEnumerable.nextObject(constructor, loadingFields, dataIterator, state, entity);
        if (endReached()) {
            state.reset();
            close();
        }

        return result;
    }

    @Override
    public void close() throws DatabaseException {
        dataIterator.close();
    }

    private boolean endReached() {
        return state.isEmpty() || state.getNextId() > endId;
    }
}
