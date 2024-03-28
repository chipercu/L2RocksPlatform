package com.fuzzy.subsystems.accesscscheme.queries.accessschemeemployeelist;

import com.fuzzy.main.cluster.core.remote.struct.RemoteObject;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLTypeOutObject;
import com.fuzzy.main.rdao.database.utils.BaseEnum;

@GraphQLTypeOutObject("access_scheme_employee_sorting_column")
public enum AccessSchemeEmployeeSortingColumn implements RemoteObject, BaseEnum {

    EMPLOYEE(1),
    DEPARTMENT(2),
    ACCESS_OPERATION(3);

    private final int id;

    AccessSchemeEmployeeSortingColumn(int id) {
        this.id = id;
    }

    @Override
    public int intValue() {
        return id;
    }

    public static AccessSchemeEmployeeSortingColumn get(long id) {
        for (AccessSchemeEmployeeSortingColumn sortingColumn : AccessSchemeEmployeeSortingColumn.values()) {
            if (sortingColumn.intValue() == id) {
                return sortingColumn;
            }
        }
        return null;
    }
}