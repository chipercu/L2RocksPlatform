package com.fuzzy.database.schema;

import com.fuzzy.database.schema.BaseIndex;
import com.fuzzy.database.schema.Field;
import com.fuzzy.database.schema.StructEntity;

import java.util.List;

public abstract class BaseIntervalIndex extends BaseIndex {

    BaseIntervalIndex(List<com.fuzzy.database.schema.Field> sortedIndexedFields, StructEntity parent) {
        super(sortedIndexedFields, parent);
    }

    public abstract List<Field> getHashedFields();

    public abstract void checkIndexedValueType(Class<?> valueType);
}
