package com.fuzzy.database.domainobject.filter;

import com.fuzzy.database.domainobject.filter.Filter;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class PrefixFilter implements Filter {

    private final Set<Integer> fieldNames;
    private String fieldValue;

    public PrefixFilter(int fieldNumber, String fieldValue) {
        this.fieldNames = Collections.singleton(fieldNumber);
        this.fieldValue = fieldValue;
    }

    public PrefixFilter(Collection<Integer> fieldNames, String fieldValue) {
        this.fieldNames = Collections.unmodifiableSet(new HashSet<>(fieldNames));
        this.fieldValue = fieldValue;
    }

    public Set<Integer> getFieldNames() {
        return fieldNames;
    }

    public String getFieldValue() {
        return fieldValue;
    }

    public void setFieldValue(String fieldValue) {
        this.fieldValue = fieldValue;
    }
}
