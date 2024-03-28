package com.fuzzy.subsystems.comparators;

import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.rdao.database.domainobject.DomainObject;
import com.fuzzy.subsystems.function.Function;
import com.fuzzy.subsystems.sorter.SorterComparator;

abstract class DomainObjectValueComparator<T extends DomainObject, Y> implements SorterComparator<T> {

    private final Function<T, Y> valueGetter;

    public DomainObjectValueComparator(Function<T, Y> valueGetter) {
        this.valueGetter = valueGetter;
    }

    @Override
    public int compare(T o1, T o2) throws PlatformException {
        int result = Tool.compare(valueGetter.apply(o1), valueGetter.apply(o2), this::compareValues);
        if (result == 0) {
            result = Tool.compare(o1.getId(), o2.getId(), Long::compare);
        }
        return result;
    }

    protected abstract int compareValues(Y o1, Y o2);
}
