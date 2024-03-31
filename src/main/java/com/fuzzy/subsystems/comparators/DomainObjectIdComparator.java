package com.fuzzy.subsystems.comparators;

import com.fuzzy.database.domainobject.DomainObject;

public class DomainObjectIdComparator<T extends DomainObject> extends IdComparator<Long, T> {

    public DomainObjectIdComparator() {
        super(DomainObject::getId);
    }
}
