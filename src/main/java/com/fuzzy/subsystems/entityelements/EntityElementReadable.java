package com.fuzzy.subsystems.entityelements;

import com.fuzzy.subsystems.remote.RDomainObject;

public abstract class EntityElementReadable<T> extends RDomainObject {

    public EntityElementReadable(long id) {
        super(id);
    }

    public abstract Long getEntityId();

    public abstract T getElement();

}
