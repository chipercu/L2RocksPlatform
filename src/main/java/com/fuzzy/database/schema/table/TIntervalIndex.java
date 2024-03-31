package com.fuzzy.database.schema.table;

import com.fuzzy.database.schema.table.TBaseIndex;
import com.fuzzy.database.schema.table.TField;

import java.util.Arrays;
import java.util.Objects;

public class TIntervalIndex extends TBaseIndex {

    private static final String[] EMPTY_HASHED_FIELDS = {};

    private final String indexedField;
    private final String[] hashedFields;

    public TIntervalIndex(String indexedField, String[] hashedFields) {
        this.indexedField = indexedField;
        this.hashedFields = hashedFields;
    }

    public TIntervalIndex(com.fuzzy.database.schema.table.TField indexedField, com.fuzzy.database.schema.table.TField[] hashedFields) {
        this(indexedField.getName(),
                Arrays.stream(hashedFields).map(com.fuzzy.database.schema.table.TField::getName).toArray(String[]::new));
    }

    public TIntervalIndex(String indexedField) {
        this(indexedField, EMPTY_HASHED_FIELDS);
    }

    public TIntervalIndex(TField indexedField) {
        this(indexedField.getName());
    }

    public String getIndexedField() {
        return indexedField;
    }

    public String[] getHashedFields() {
        return hashedFields;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TIntervalIndex that = (TIntervalIndex) o;
        return Objects.equals(indexedField, that.indexedField) &&
                Arrays.equals(hashedFields, that.hashedFields);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(indexedField);
        result = 31 * result + Arrays.hashCode(hashedFields);
        return result;
    }

    @Override
    public String toString() {
        return "IntervalIndex{" +
                "indexedField='" + indexedField + '\'' +
                ", hashedFields=" + Arrays.toString(hashedFields) +
                '}';
    }
}
