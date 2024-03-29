package com.fuzzy.subsystem.core.domainobject.manageremployeeaccess;

import com.fuzzy.subsystems.entityelements.EntityElementEditable;

public class ManagerEmployeeAccessEditable extends ManagerEmployeeAccessReadable
        implements EntityElementEditable<Long> {

    public ManagerEmployeeAccessEditable(long id) {
        super(id);
    }

    public void setManagerId(Long managerId) {
        set(FIELD_MANAGER_ID, managerId);
    }

    public void setEmployeeId(Long employeeId) {
        set(FIELD_EMPLOYEE_ID, employeeId);
    }

    @Override
    public void setEntityId(Long entityId) {
        setManagerId(entityId);
    }

    @Override
    public void setElement(Long element) {
        setEmployeeId(element);
    }
}
