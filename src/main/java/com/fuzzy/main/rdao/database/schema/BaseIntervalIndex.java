package com.fuzzy.main.rdao.database.schema;

import java.util.List;

public abstract class BaseIntervalIndex extends BaseIndex {

    BaseIntervalIndex(List<Field> sortedIndexedFields, StructEntity parent) {
        super(sortedIndexedFields, parent);
    }

    public abstract List<Field> getHashedFields();

    public abstract void checkIndexedValueType(Class<?> valueType);
}
