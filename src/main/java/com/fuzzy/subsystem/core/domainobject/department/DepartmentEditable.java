package com.fuzzy.subsystem.core.domainobject.department;

import com.infomaximum.database.domainobject.DomainObjectEditable;

public class DepartmentEditable extends DepartmentReadable implements DomainObjectEditable {

    public DepartmentEditable(long id) {
        super(id);
    }

    public void setName(String name) {
        set(FIELD_NAME, name);
    }

    public void setParentId(Long parentId) {
        set(FIELD_PARENT_ID, parentId);
    }
}
