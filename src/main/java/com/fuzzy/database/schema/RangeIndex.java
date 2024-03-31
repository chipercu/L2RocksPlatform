package com.fuzzy.database.schema;

import com.fuzzy.database.exception.IllegalTypeException;
import com.fuzzy.database.schema.BaseIntervalIndex;
import com.fuzzy.database.schema.Field;
import com.fuzzy.database.schema.StructEntity;
import com.fuzzy.database.utils.IntervalIndexUtils;
import com.fuzzy.database.utils.TypeConvert;

import java.util.Collection;
import java.util.List;

public class RangeIndex extends BaseIntervalIndex {

    private final static byte[] INDEX_NAME_BYTES = TypeConvert.pack("rng");

    private final List<com.fuzzy.database.schema.Field> hashedFields;
    private final com.fuzzy.database.schema.Field beginIndexedField;
    private final com.fuzzy.database.schema.Field endIndexedField;

    public RangeIndex(com.fuzzy.database.anotation.RangeIndex index, StructEntity parent) {
        super(buildIndexedFields(index, parent), parent);

        this.hashedFields = sortedFields.subList(0, sortedFields.size() - 2);
        this.beginIndexedField = sortedFields.get(sortedFields.size() - 2);
        this.endIndexedField = sortedFields.get(sortedFields.size() - 1);
    }

    @Override
    public List<com.fuzzy.database.schema.Field> getHashedFields() {
        return hashedFields;
    }

    public com.fuzzy.database.schema.Field getBeginIndexedField() {
        return beginIndexedField;
    }

    public com.fuzzy.database.schema.Field getEndIndexedField() {
        return endIndexedField;
    }

    @Override
    public void checkIndexedValueType(Class<?> valueType) {
        beginIndexedField.throwIfNotMatch(valueType);
    }

    private static List<com.fuzzy.database.schema.Field> buildIndexedFields(com.fuzzy.database.anotation.RangeIndex index, StructEntity parent) {
        com.fuzzy.database.schema.Field beginField = parent.getField(index.beginField());
        IntervalIndexUtils.checkType(beginField.getType());

        com.fuzzy.database.schema.Field endField = parent.getField(index.endField());
        IntervalIndexUtils.checkType(endField.getType());

        if (beginField.getType() != endField.getType()) {
            throw new IllegalTypeException("Inconsistent range-types, " + beginField.getType().getSimpleName() + " and " + endField.getType().getSimpleName());
        }

        List<Field> fields = buildIndexedFields(index.hashedFields(), parent);
        fields.add(beginField);
        fields.add(endField);
        return fields;
    }

    public static String toString(Collection<String> hashedFields, String beginField, String endField) {
        return RangeIndex.class.getSimpleName() + ": " + hashedFields + ", [" + beginField + " - " + endField + "]";
    }

    @Override
    public byte[] getIndexNameBytes() {
        return INDEX_NAME_BYTES;
    }
}
