package com.fuzzy.subsystem.core.domainobject.managerallaccess;

import com.fuzzy.subsystems.entityelements.EntityEditable;

public class ManagerAllAccessEditable extends ManagerAllAccessReadable implements EntityEditable {

    public ManagerAllAccessEditable(long id) {
        super(id);
    }

    public void setManagerId(Long managerId) {
        set(FIELD_MANAGER_ID, managerId);
    }

    @Override
    public void setEntityId(Long entityId) {
        setManagerId(entityId);
    }
}
