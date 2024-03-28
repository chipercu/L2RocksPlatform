package com.fuzzy.subsystems.comparators;

import com.fuzzy.subsystems.function.Function;
import com.fuzzy.subsystems.utils.ComparatorUtility;

public class IdStringComparator<T> extends IdValueComparator<T, String> {

    public IdStringComparator(Function<T, Long> idGetter, Function<Long, String> valueGetter) {
        super(idGetter, valueGetter);
    }

    @Override
    protected int compareValues(String o1, String o2) {
        return ComparatorUtility.compare(o1, o2);
    }
}
