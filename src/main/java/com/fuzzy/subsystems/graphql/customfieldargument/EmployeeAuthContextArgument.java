package com.fuzzy.subsystems.graphql.customfieldargument;

import com.fuzzy.cluster.graphql.fieldargument.custom.CustomFieldArgument;
import com.fuzzy.platform.component.frontend.context.ContextTransactionRequest;
import com.fuzzy.subsystem.core.authcontext.employee.EmployeeAuthContext;

import java.lang.reflect.Method;

public class EmployeeAuthContextArgument implements CustomFieldArgument<EmployeeAuthContext> {

    @Override
    public boolean isSupport(Class classType) {
        return (classType == EmployeeAuthContext.class);
    }

    @Override
    public EmployeeAuthContext getValue(Class classType, Method method, com.fuzzy.cluster.graphql.struct.ContextRequest context) {
        ContextTransactionRequest contextTransactionRequest = (ContextTransactionRequest) context;
        return (EmployeeAuthContext) contextTransactionRequest.getSource().getAuthContext();
    }
}