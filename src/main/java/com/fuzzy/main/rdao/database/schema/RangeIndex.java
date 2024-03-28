package com.fuzzy.main.rdao.database.schema;

import com.fuzzy.main.rdao.database.exception.IllegalTypeException;
import com.fuzzy.main.rdao.database.utils.IntervalIndexUtils;
import com.fuzzy.main.rdao.database.utils.TypeConvert;

import java.util.Collection;
import java.util.List;

public class RangeIndex extends BaseIntervalIndex {

    private final static byte[] INDEX_NAME_BYTES = TypeConvert.pack("rng");

    private final List<Field> hashedFields;
    private final Field beginIndexedField;
    private final Field endIndexedField;

    public RangeIndex(com.fuzzy.main.rdao.database.anotation.RangeIndex index, StructEntity parent) {
        super(buildIndexedFields(index, parent), parent);

        this.hashedFields = sortedFields.subList(0, sortedFields.size() - 2);
        this.beginIndexedField = sortedFields.get(sortedFields.size() - 2);
        this.endIndexedField = sortedFields.get(sortedFields.size() - 1);
    }

    @Override
    public List<Field> getHashedFields() {
        return hashedFields;
    }

    public Field getBeginIndexedField() {
        return beginIndexedField;
    }

    public Field getEndIndexedField() {
        return endIndexedField;
    }

    @Override
    public void checkIndexedValueType(Class<?> valueType) {
        beginIndexedField.throwIfNotMatch(valueType);
    }

    private static List<Field> buildIndexedFields(com.fuzzy.main.rdao.database.anotation.RangeIndex index, StructEntity parent) {
        Field beginField = parent.getField(index.beginField());
        IntervalIndexUtils.checkType(beginField.getType());

        Field endField = parent.getField(index.endField());
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
