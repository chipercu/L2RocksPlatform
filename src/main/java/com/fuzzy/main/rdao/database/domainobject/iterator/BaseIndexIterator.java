package com.fuzzy.main.rdao.database.domainobject.iterator;

import com.fuzzy.main.rdao.database.domainobject.DataEnumerable;
import com.fuzzy.main.rdao.database.domainobject.DomainObject;
import com.fuzzy.main.rdao.database.exception.DatabaseException;
import com.fuzzy.main.rdao.database.provider.DBIterator;
import com.fuzzy.main.rdao.database.provider.KeyPattern;
import com.fuzzy.main.rdao.database.schema.Field;
import com.fuzzy.main.rdao.database.schema.Schema;
import com.fuzzy.main.rdao.database.schema.StructEntity;
import com.fuzzy.main.rdao.database.utils.key.FieldKey;

import java.lang.reflect.Constructor;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

public abstract class BaseIndexIterator<E extends DomainObject> implements IteratorEntity<E> {

    private final DataEnumerable dataEnumerable;
    private final Constructor<E> constructor;
    private final Set<Integer> loadingFields;
    final StructEntity entity;

    DBIterator indexIterator = null;
    KeyPattern dataKeyPattern = null;
    DBIterator dataIterator = null;
    E nextElement;

    BaseIndexIterator(DataEnumerable dataEnumerable, Class<E> clazz, Set<Integer> loadingFields) throws DatabaseException {
        this.dataEnumerable = dataEnumerable;
        this.constructor = DomainObject.getConstructor(clazz);
        this.loadingFields = loadingFields;
        this.entity = Schema.getEntity(clazz);
    }

    @Override
    public boolean hasNext() {
        return nextElement != null;
    }

    @Override
    public E next() throws DatabaseException {
        if (nextElement == null) {
            throw new NoSuchElementException();
        }

        E element = nextElement;
        nextImpl();
        return element;
    }

    @Override
    public void close() throws DatabaseException {
        try (DBIterator i1 = indexIterator;
             DBIterator i2 = dataIterator) {
            // do nothing
        }
    }

    abstract void nextImpl() throws DatabaseException;

    static KeyPattern buildDataKeyPattern(List<Field> fields1, Set<Integer> loadingFields, StructEntity entity) {
        if (loadingFields == null) {
            return FieldKey.buildKeyPattern(0, null);
        }

        if (fields1 == null || fields1.isEmpty()) {
            return loadingFields.isEmpty() ? null : FieldKey.buildKeyPattern(entity.getFieldNames(loadingFields));
        }

        Set<Integer> fields = new HashSet<>(fields1.size() + loadingFields.size());
        fields1.forEach(field -> fields.add(field.getNumber()));
        fields.addAll(loadingFields);
        return FieldKey.buildKeyPattern(entity.getFieldNames(fields));
    }

    E findObject(long id) throws DatabaseException {
        if (dataEnumerable.isMarkedForDeletion(entity, id)) {
            return null;
        }

        if (dataKeyPattern == null) {
            return dataEnumerable.buildDomainObject(constructor, id, loadingFields);
        }

        dataKeyPattern.setPrefix(FieldKey.buildKeyPrefix(id));

        E obj = dataEnumerable.seekObject(constructor, loadingFields, dataIterator, dataKeyPattern);
        return checkFilter(obj) ? obj : null;
    }

    abstract boolean checkFilter(E obj) throws DatabaseException;
}

