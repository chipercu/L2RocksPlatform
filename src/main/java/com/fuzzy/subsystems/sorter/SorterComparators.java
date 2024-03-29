package com.fuzzy.subsystems.sorter;

import com.infomaximum.database.domainobject.DomainObject;
import com.infomaximum.platform.exception.PlatformException;
import com.fuzzy.subsystems.function.Function;
import com.fuzzy.subsystems.graphql.enums.SortingDirection;
import com.fuzzy.subsystems.utils.ComparatorUtility;

import java.time.Instant;

public class SorterComparators {

    private record SorterComparatorImpl<T extends DomainObject>(SorterComparator<T> baseComparator,
                                                                SortingDirection sortingDirection) implements SorterComparator<T> {

        @Override
            public int compare(T o1, T o2) throws PlatformException {
                int result = baseComparator.compare(o1, o2);
                if (result == 0) {
                    result = getPrimaryKeyComparator().compare(o1, o2);
                }
                if (sortingDirection == SortingDirection.DESC) {
                    result = -result;
                }
                return result;
            }
        }

    public static <T extends DomainObject> SorterComparator<T> getPrimaryKeyComparator() {
        return (o1, o2) -> Long.compare(o1.getId(), o2.getId());
    }

    public static <T extends DomainObject> SorterComparator<T> getStringComparator(Function<T, String> fieldGetter,
                                                                                   SortingDirection sortingDirection) {
        return new SorterComparatorImpl<>(
                (o1, o2) -> ComparatorUtility.compare(fieldGetter.apply(o1), fieldGetter.apply(o2)),
                sortingDirection);
    }

    public static <T extends DomainObject> SorterComparator<T> getInstantComparator(Function<T, Instant> fieldGetter,
                                                                                    SortingDirection sortingDirection) {
        return new SorterComparatorImpl<>(
                (o1, o2) -> ComparatorUtility.compare(fieldGetter.apply(o1), fieldGetter.apply(o2)),
                sortingDirection);
    }

    public static <T extends DomainObject> SorterComparator<T> getLongComparator(Function<T, Long> fieldGetter,
                                                                                  SortingDirection sortingDirection) {
        return new SorterComparatorImpl<>(
                (o1, o2) -> Long.compare(fieldGetter.apply(o1), fieldGetter.apply(o2)),
                sortingDirection);
    }

    public static <T extends DomainObject> SorterComparator<T> getBooleanComparator(Function<T, Boolean> fieldGetter,
                                                                                    SortingDirection sortingDirection) {
        return new SorterComparatorImpl<>(
                (o1, o2) -> Boolean.compare(fieldGetter.apply(o1), fieldGetter.apply(o2)),
                sortingDirection);
    }
}
