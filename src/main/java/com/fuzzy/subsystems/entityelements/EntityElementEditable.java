package com.fuzzy.subsystems.entityelements;

import com.infomaximum.database.domainobject.DomainObjectEditable;

public interface EntityElementEditable<T> extends DomainObjectEditable {

    void setEntityId(Long entityId);

    void setElement(T element);

}
