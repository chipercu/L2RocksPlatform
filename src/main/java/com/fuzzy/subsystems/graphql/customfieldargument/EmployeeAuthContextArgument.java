package com.fuzzy.subsystems.graphql.customfieldargument;

import com.fuzzy.main.cluster.graphql.fieldargument.custom.CustomFieldArgument;
import com.fuzzy.main.cluster.graphql.struct.ContextRequest;
import com.fuzzy.main.platform.component.frontend.context.ContextTransactionRequest;
import com.fuzzy.subsystem.core.authcontext.employee.EmployeeAuthContext;

import java.lang.reflect.Method;

public class EmployeeAuthContextArgument implements CustomFieldArgument<EmployeeAuthContext> {

    @Override
    public boolean isSupport(Class classType) {
        return (classType == EmployeeAuthContext.class);
    }

    @Override
    public EmployeeAuthContext getValue(Class classType, Method method, ContextRequest context) {
        ContextTransactionRequest contextTransactionRequest = (ContextTransactionRequest) context;
        return (EmployeeAuthContext) contextTransactionRequest.getSource().getAuthContext();
    }
}