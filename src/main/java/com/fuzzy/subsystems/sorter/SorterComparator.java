package com.fuzzy.subsystems.sorter;

import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.subsystems.graphql.enums.SortingDirection;

@FunctionalInterface
public interface SorterComparator<T> {

    int compare(T o1, T o2) throws PlatformException;

    default SorterComparator<T> thenComparing(SorterComparator<? super T> other) {
        return (o1, o2) -> {
            int res = compare(o1, o2);
            return (res != 0) ? res : other.compare(o1, o2);
        };
    }

    default SorterComparator<T> direction(SortingDirection direction) {
        return (o1, o2) -> direction == SortingDirection.ASC ? compare(o1, o2) : compare(o2, o1);
    }

    default SorterComparator<T> reverseOrder() {
        return direction(SortingDirection.DESC);
    }
}
