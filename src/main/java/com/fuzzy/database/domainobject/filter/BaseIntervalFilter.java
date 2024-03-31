package com.fuzzy.database.domainobject.filter;

import com.fuzzy.database.domainobject.filter.Filter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class BaseIntervalFilter implements Filter {

    private final Object beginValue;
    private final Object endValue;
    private Map<Integer, Object> values = null;

    BaseIntervalFilter(Double beginValue, Double endValue) {
        this.beginValue = beginValue;
        this.endValue = endValue;
    }

    BaseIntervalFilter(Long beginValue, Long endValue) {
        this.beginValue = beginValue;
        this.endValue = endValue;
    }

    BaseIntervalFilter(Instant beginValue, Instant endValue) {
        this.beginValue = beginValue;
        this.endValue = endValue;
    }

    BaseIntervalFilter(LocalDateTime beginValue, LocalDateTime endValue) {
        this.beginValue = beginValue;
        this.endValue = endValue;
    }

    public BaseIntervalFilter appendHashedField(Integer number, Object value) {
        if (values == null) {
            values = new HashMap<>();
        }
        values.put(number, value);
        return this;
    }

    public Map<Integer, Object> getHashedValues() {
        return values != null ? Collections.unmodifiableMap(values) : Collections.emptyMap();
    }

    public Object getBeginValue() {
        return beginValue;
    }

    public Object getEndValue() {
        return endValue;
    }
}
