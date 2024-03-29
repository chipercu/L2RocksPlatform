package com.fuzzy.subsystems.comparators;

import com.infomaximum.platform.exception.PlatformException;
import com.fuzzy.subsystems.function.Function;
import com.fuzzy.subsystems.sorter.SorterComparator;

import java.util.HashMap;
import java.util.Map;

abstract class IdValueComparator<T, Y> implements SorterComparator<T> {

    private final Function<T, Long> idGetter;
    private final Function<Long, Y> valueGetter;
    private final Map<Long, Y> cache;

    public IdValueComparator(Function<T, Long> idGetter, Function<Long, Y> valueGetter) {
        this.idGetter = idGetter;
        this.valueGetter = valueGetter;
        this.cache = new HashMap<>();
    }

    @Override
    public int compare(T o1, T o2) throws PlatformException {
        Long id1 = idGetter.apply(o1);
        Long id2 = idGetter.apply(o2);
        int result = Tool.compare(getValue(id1), getValue(id2), this::compareValues);
        if (result == 0) {
            result = Tool.compare(id1, id2, Long::compare);
        }
        return result;
    }

    protected abstract int compareValues(Y o1, Y o2);

    private Y getValue(Long id) throws PlatformException {
        Y value;
        if (id == null) {
            value = null;
        } else if (cache.containsKey(id)) {
            value = cache.get(id);
        } else {
            value = valueGetter.apply(id);
            cache.put(id, value);
        }
        return value;
    }
}
