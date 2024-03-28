package com.fuzzy.subsystems.comparators;

import com.fuzzy.main.rdao.database.domainobject.DomainObject;
import com.fuzzy.subsystems.function.Function;
import com.fuzzy.subsystems.utils.ComparatorUtility;

public class DomainObjectStringComparator<T extends DomainObject> extends DomainObjectValueComparator<T, String> {

    public DomainObjectStringComparator(Function<T, String> valueGetter) {
        super(valueGetter);
    }

    @Override
    protected int compareValues(String o1, String o2) {
        return ComparatorUtility.compare(o1, o2);
    }
}
