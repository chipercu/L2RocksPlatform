package com.fuzzy.database.schema;

import com.fuzzy.database.schema.BaseIntervalIndex;
import com.fuzzy.database.schema.Field;
import com.fuzzy.database.schema.StructEntity;
import com.fuzzy.database.utils.IntervalIndexUtils;
import com.fuzzy.database.utils.TypeConvert;

import java.util.Collection;
import java.util.List;

public class IntervalIndex extends BaseIntervalIndex {

    private final static byte[] INDEX_NAME_BYTES = TypeConvert.pack("int");

    private final List<com.fuzzy.database.schema.Field> hashedFields;
    private final com.fuzzy.database.schema.Field indexedField;

    public IntervalIndex(com.fuzzy.database.anotation.IntervalIndex index, com.fuzzy.database.schema.StructEntity parent) {
        super(buildIndexedFields(index, parent), parent);

        this.hashedFields = sortedFields.subList(0, sortedFields.size() - 1);
        this.indexedField = sortedFields.get(sortedFields.size() - 1);
    }

    @Override
    public List<com.fuzzy.database.schema.Field> getHashedFields() {
        return hashedFields;
    }

    public com.fuzzy.database.schema.Field getIndexedField() {
        return indexedField;
    }

    @Override
    public void checkIndexedValueType(Class<?> valueType) {
        indexedField.throwIfNotMatch(valueType);
    }

    private static List<com.fuzzy.database.schema.Field> buildIndexedFields(com.fuzzy.database.anotation.IntervalIndex index, StructEntity parent) {
        com.fuzzy.database.schema.Field indexedField = parent.getField(index.indexedField());
        IntervalIndexUtils.checkType(indexedField.getType());

        List<Field> fields = buildIndexedFields(index.hashedFields(), parent);
        fields.add(indexedField);
        return fields;
    }

    public static String toString(Collection<String> hashedFields, String indexedField) {
        return IntervalIndex.class.getSimpleName() + ": " + hashedFields + ", " + indexedField;
    }

    @Override
    public byte[] getIndexNameBytes() {
        return INDEX_NAME_BYTES;
    }
}
