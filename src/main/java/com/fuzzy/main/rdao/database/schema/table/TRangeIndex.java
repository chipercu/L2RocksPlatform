package com.fuzzy.main.rdao.database.schema.table;

import java.util.Arrays;
import java.util.Objects;

public class TRangeIndex extends TBaseIndex {

    private static final String[] EMPTY_HASHED_FIELDS = {};

    private final String beginField;
    private final String endField;
    private final String[] hashedFields;

    public TRangeIndex(String beginField, String endField) {
        this(beginField, endField, EMPTY_HASHED_FIELDS);
    }

    public TRangeIndex(TField beginField, TField endField) {
        this(beginField.getName(), endField.getName());
    }

    public TRangeIndex(String beginField, String endField, String[] hashedFields) {
        this.beginField = beginField;
        this.endField = endField;
        this.hashedFields = hashedFields;
    }

    public TRangeIndex(TField beginField, TField endField, TField[] hashedFields) {
        this(beginField.getName(),
                endField.getName(),
                Arrays.stream(hashedFields).map(TField::getName).toArray(String[]::new));
    }

    public String getBeginField() {
        return beginField;
    }

    public String getEndField() {
        return endField;
    }

    public String[] getHashedFields() {
        return hashedFields;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TRangeIndex that = (TRangeIndex) o;
        return Objects.equals(beginField, that.beginField) &&
                Objects.equals(endField, that.endField) &&
                Arrays.equals(hashedFields, that.hashedFields);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(beginField, endField);
        result = 31 * result + Arrays.hashCode(hashedFields);
        return result;
    }

    @Override
    public String toString() {
        return "RangeIndex{" +
                "beginField='" + beginField + '\'' +
                ", endField='" + endField + '\'' +
                ", hashedFields=" + Arrays.toString(hashedFields) +
                '}';
    }
}
