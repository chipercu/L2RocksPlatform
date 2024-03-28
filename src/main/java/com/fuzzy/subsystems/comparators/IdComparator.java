package com.fuzzy.subsystems.comparators;

import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.subsystems.function.Function;
import com.fuzzy.subsystems.sorter.SorterComparator;

public class IdComparator<K extends Comparable<K>, T> implements SorterComparator<T> {

    private final Function<T, K> idGetter;

    public IdComparator(Function<T, K> idGetter) {
        this.idGetter = idGetter;
    }

    @Override
    public int compare(T o1, T o2) throws PlatformException {
        return Tool.compare(idGetter.apply(o1), idGetter.apply(o2), K::compareTo);
    }
}