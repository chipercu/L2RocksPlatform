package com.fuzzy.subsystems.comparators;

import com.fuzzy.main.rdao.database.domainobject.DomainObject;

public class DomainObjectIdComparator<T extends DomainObject> extends IdComparator<Long, T> {

    public DomainObjectIdComparator() {
        super(DomainObject::getId);
    }
}
