package com.fuzzy.subsystems.entityelements;

import com.fuzzy.subsystems.remote.RDomainObject;

public abstract class EntityReadable extends RDomainObject {

    public EntityReadable(long id) {
        super(id);
    }

    public abstract Long getEntityId();

}
