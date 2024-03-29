package com.fuzzy.subsystem.core.remote.department;

import com.fuzzy.subsystem.core.domainobject.department.DepartmentReadable;
import com.fuzzy.subsystems.modelspace.BuilderFields;

/**
 * Created by kris on 04.07.17.
 */
public class DepartmentBuilder extends BuilderFields {

    public DepartmentBuilder withName(String value) {
        fields.put(DepartmentReadable.FIELD_NAME, value);
        return this;
    }

    public DepartmentBuilder withParentId(Long departmentId) {
        fields.put(DepartmentReadable.FIELD_PARENT_ID, departmentId);
        return this;
    }

    public boolean isContainName() {
        return fields.containsKey(DepartmentReadable.FIELD_NAME);
    }
    public boolean isContainParentId() {
        return fields.containsKey(DepartmentReadable.FIELD_PARENT_ID);
    }


    public String getName() {
        return (String) fields.get(DepartmentReadable.FIELD_NAME);
    }
    public Long getParentId() {
        return (Long) fields.get(DepartmentReadable.FIELD_PARENT_ID);
    }
}

