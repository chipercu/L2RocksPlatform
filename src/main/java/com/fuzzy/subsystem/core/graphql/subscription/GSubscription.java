package com.fuzzy.subsystem.core.graphql.subscription;

import com.infomaximum.cluster.graphql.anotation.GraphQLDescription;
import com.infomaximum.cluster.graphql.anotation.GraphQLField;
import com.infomaximum.cluster.graphql.anotation.GraphQLTypeOutObject;
import com.infomaximum.cluster.graphql.struct.GSubscribeEvent;
import com.infomaximum.platform.sdk.graphql.annotation.GraphQLAuthControl;
import com.fuzzy.subsystem.core.CoreSubsystem;
import com.fuzzy.subsystem.core.authcontext.employee.EmployeeAuthContext;
import com.fuzzy.subsystem.core.subscription.employee.GEmployeeUpdateEvent;

@GraphQLTypeOutObject("subscription")
public class GSubscription {

    @GraphQLField
    @GraphQLAuthControl({ EmployeeAuthContext.class })
    @GraphQLDescription("Изменение данных текущего сотрудника")
    public static GSubscribeEvent<Boolean> updateActiveEmployee(CoreSubsystem component, EmployeeAuthContext context) {
        return new GEmployeeUpdateEvent(component, context.getEmployeeId());
    }
}
