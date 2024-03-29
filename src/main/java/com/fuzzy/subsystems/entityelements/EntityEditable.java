package com.fuzzy.subsystems.entityelements;

import com.infomaximum.database.domainobject.DomainObjectEditable;

public interface EntityEditable extends DomainObjectEditable {

    void setEntityId(Long entityId);

}