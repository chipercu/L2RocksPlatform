package com.fuzzy.main.rdao.database.domainobject.filter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class HashFilter implements Filter {

    private final Map<Integer, Object> values = new HashMap<>();

    public HashFilter(int fieldNumber, Object fieldValue) {
        appendField(fieldNumber, fieldValue);
    }

    public HashFilter appendField(int number, Object value) {
        values.put(number, value);
        return this;
    }

    public Map<Integer, Object> getValues() {
        return Collections.unmodifiableMap(values);
    }
}
