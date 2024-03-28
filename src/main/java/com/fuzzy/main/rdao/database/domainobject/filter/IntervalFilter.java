package com.fuzzy.main.rdao.database.domainobject.filter;

import java.time.Instant;
import java.time.LocalDateTime;

public class IntervalFilter extends BaseIntervalFilter {

    private final Integer indexedFieldId;
    private SortDirection sortDirection = SortDirection.ASC;

    public IntervalFilter(Integer indexedFieldId, Double beginValue, Double endValue) {
        super(beginValue, endValue);

        this.indexedFieldId = indexedFieldId;
    }

    public IntervalFilter(Integer indexedFieldId, Long beginValue, Long endValue) {
        super(beginValue, endValue);

        this.indexedFieldId = indexedFieldId;
    }

    public IntervalFilter(Integer indexedFieldId, Instant beginValue, Instant endValue) {
        super(beginValue, endValue);

        this.indexedFieldId = indexedFieldId;
    }

    public IntervalFilter(Integer indexedFieldId, LocalDateTime beginValue, LocalDateTime endValue) {
        super(beginValue, endValue);

        this.indexedFieldId = indexedFieldId;
    }

    public Integer getIndexedFieldId() {
        return indexedFieldId;
    }

    public SortDirection getSortDirection() {
        return sortDirection;
    }

    @Override
    public IntervalFilter appendHashedField(Integer number, Object value) {
        return (IntervalFilter) super.appendHashedField(number, value);
    }

    public IntervalFilter setSortDirection(SortDirection sortDirection) {
        if (sortDirection == null) {
            throw new IllegalArgumentException();
        }

        this.sortDirection = sortDirection;
        return this;
    }
}
