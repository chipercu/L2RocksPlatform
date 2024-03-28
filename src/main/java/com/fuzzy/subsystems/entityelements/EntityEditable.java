package com.fuzzy.subsystems.entityelements;

import com.fuzzy.main.rdao.database.domainobject.DomainObjectEditable;

public interface EntityEditable extends DomainObjectEditable {

    void setEntityId(Long entityId);

}