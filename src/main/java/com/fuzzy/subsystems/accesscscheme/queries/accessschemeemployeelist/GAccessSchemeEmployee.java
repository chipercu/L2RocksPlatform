package com.fuzzy.subsystems.accesscscheme.queries.accessschemeemployeelist;

import com.fuzzy.cluster.core.remote.struct.RemoteObject;
import com.fuzzy.cluster.graphql.anotation.GraphQLDescription;
import com.fuzzy.cluster.graphql.anotation.GraphQLField;
import com.fuzzy.cluster.graphql.anotation.GraphQLTypeOutObject;
import com.fuzzy.platform.component.frontend.authcontext.UnauthorizedContext;
import com.fuzzy.platform.sdk.graphql.annotation.GraphQLAuthControl;
import com.fuzzy.subsystem.core.graphql.query.department.GDepartment;
import com.fuzzy.subsystem.core.graphql.query.employee.GEmployee;
import com.fuzzy.subsystems.accesscscheme.GAccessSchemeOperation;

@GraphQLTypeOutObject("access_scheme_employee")
public class GAccessSchemeEmployee implements RemoteObject {

    private final GEmployee employee;
    private final GDepartment department;
    private final GAccessSchemeOperation accessOperation;
    private final String accessOperationLoc;

    public GAccessSchemeEmployee(GEmployee employee,
                                 GDepartment department,
                                 GAccessSchemeOperation accessOperation,
                                 String accessOperationLoc) {
        this.employee = employee;
        this.department = department;
        this.accessOperation = accessOperation;
        this.accessOperationLoc = accessOperationLoc;
    }

    @GraphQLField
    @GraphQLAuthControl({ UnauthorizedContext.class })
    @GraphQLDescription("Сотрудник")
    public GEmployee getEmployee() {
        return employee;
    }

    @GraphQLField
    @GraphQLAuthControl({ UnauthorizedContext.class })
    @GraphQLDescription("Отдел")
    public GDepartment getDepartment() {
        return department;
    }

    @GraphQLField
    @GraphQLAuthControl({ UnauthorizedContext.class })
    @GraphQLDescription("Доступ")
    public GAccessSchemeOperation getAccessOperation() {
        return accessOperation;
    }

    @GraphQLField
    @GraphQLAuthControl({ UnauthorizedContext.class })
    @GraphQLDescription("Локализация доступа")
    public String getAccessOperationLoc() {
        return accessOperationLoc;
    }
}
