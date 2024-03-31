package com.fuzzy.subsystems.entityelements;

import com.fuzzy.database.domainobject.DomainObjectEditable;

public interface EntityEditable extends DomainObjectEditable {

    void setEntityId(Long entityId);

}