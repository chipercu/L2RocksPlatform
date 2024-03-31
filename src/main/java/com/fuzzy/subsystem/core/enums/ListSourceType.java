package com.fuzzy.subsystem.core.enums;

import com.fuzzy.cluster.core.remote.struct.RemoteObject;
import com.fuzzy.cluster.graphql.anotation.GraphQLTypeOutObject;
import com.fuzzy.subsystem.core.domainobject.department.DepartmentReadable;
import com.fuzzy.subsystem.core.domainobject.employee.EmployeeReadable;

@GraphQLTypeOutObject("list_source_type")
public enum ListSourceType implements RemoteObject {

    EMPLOYEE(EmployeeReadable.class.getName()),

    DEPARTMENT(DepartmentReadable.class.getName());

    private final String table;

    ListSourceType(String table) {
        this.table = table;
    }

    public String getTable() {
        return table;
    }

    public static ListSourceType get(String value) {
        for (ListSourceType listSourceType : values()) {
            if (listSourceType.getTable().equals(value)) {
                return listSourceType;
            }
        }
        return null;
    }
}
