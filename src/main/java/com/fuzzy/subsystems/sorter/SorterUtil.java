package com.fuzzy.subsystems.sorter;

import com.fuzzy.database.domainobject.DomainObject;
import com.fuzzy.subsystems.comparators.DomainObjectStringComparator;
import com.fuzzy.subsystems.function.Function;
import com.fuzzy.subsystems.graphql.input.GPaging;

public class SorterUtil {

    public static <T extends DomainObject> Sorter<T> getSorter(Function<T, String> valueGetter, GPaging paging) {
        return new Sorter<>(
                new DomainObjectStringComparator<>(valueGetter),
                paging != null ? paging.getLimit() : null
        );
    }
}
