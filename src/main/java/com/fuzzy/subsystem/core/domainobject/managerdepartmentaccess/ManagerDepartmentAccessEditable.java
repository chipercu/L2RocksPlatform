package com.fuzzy.subsystem.core.domainobject.managerdepartmentaccess;

import com.fuzzy.subsystems.entityelements.EntityElementEditable;

public class ManagerDepartmentAccessEditable extends ManagerDepartmentAccessReadable
        implements EntityElementEditable<Long> {

    public ManagerDepartmentAccessEditable(long id) {
        super(id);
    }

    public void setManagerId(Long managerId) {
        set(FIELD_MANAGER_ID, managerId);
    }

    public void setDepartmentId(Long departmentId) {
        set(FIELD_DEPARTMENT_ID, departmentId);
    }

    @Override
    public void setEntityId(Long entityId) {
        setManagerId(entityId);
    }

    @Override
    public void setElement(Long element) {
        setDepartmentId(element);
    }
}
