package com.fuzzy.main.rdao.database.domainobject.filter;

import java.time.Instant;
import java.time.LocalDateTime;

public class RangeFilter extends BaseIntervalFilter {

    public static class IndexedField {

        public final int beginField;
        public final int endField;

        public IndexedField(int beginField, int endField) {
            this.beginField = beginField;
            this.endField = endField;
        }
    }

    private final IndexedField indexedField;

    public RangeFilter(IndexedField indexedField, Double beginValue, Double endValue) {
        super(beginValue, endValue);

        this.indexedField = indexedField;
    }

    public RangeFilter(IndexedField indexedField, Long beginValue, Long endValue) {
        super(beginValue, endValue);

        this.indexedField = indexedField;
    }

    public RangeFilter(IndexedField indexedField, Instant beginValue, Instant endValue) {
        super(beginValue, endValue);

        this.indexedField = indexedField;
    }

    public RangeFilter(IndexedField indexedField, LocalDateTime beginValue, LocalDateTime endValue) {
        super(beginValue, endValue);

        this.indexedField = indexedField;
    }

    @Override
    public RangeFilter appendHashedField(Integer number, Object value) {
        return (RangeFilter) super.appendHashedField(number, value);
    }

    public IndexedField getIndexedField() {
        return indexedField;
    }
}
