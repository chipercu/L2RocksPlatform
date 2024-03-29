package com.fuzzy.subsystem.core.graphql.query.employee;

import com.fuzzy.main.cluster.core.remote.struct.RemoteObject;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLDescription;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLField;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLTypeOutObjectInterface;
import com.fuzzy.main.platform.sdk.graphql.annotation.GraphQLAuthControl;
import com.fuzzy.subsystem.frontend.authcontext.AuthorizedContext;

@GraphQLTypeOutObjectInterface("department_employee_element")
public interface GDepartmentEmployeeElement extends RemoteObject {

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Идентификатор")
    long getId();
}
