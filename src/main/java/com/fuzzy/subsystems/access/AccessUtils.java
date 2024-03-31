package com.fuzzy.subsystems.access;

import com.fuzzy.cluster.graphql.struct.GOptional;
import com.fuzzy.platform.component.frontend.authcontext.UnauthorizedContext;
import com.fuzzy.platform.component.frontend.context.ContextTransactionRequest;
import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.subsystem.frontend.authcontext.AuthorizedContext;
import com.fuzzy.subsystems.exception.GeneralExceptionBuilder;

public class AccessUtils {

    public static void validateInputParameters(
            ContextTransactionRequest context,
            PrivilegeEnum privilege,
            AccessOperation operation,
            GOptional<?>... parameters
    ) throws PlatformException{
        for (GOptional<?> parameter : parameters) {
            if (parameter.isPresent()) {
                validateAccess(context, privilege, operation);
            }
        }
    }

    public static void validateAccess(
            ContextTransactionRequest context,
            PrivilegeEnum privilege,
            AccessOperation... operations
    ) throws PlatformException {
        if (!hasAccess(context, privilege, operations)) {
            throw GeneralExceptionBuilder.buildAccessDeniedException();
        }
    }

    public static boolean hasAccess(
            ContextTransactionRequest context,
            PrivilegeEnum privilege,
            AccessOperation... operations
    ) {
        UnauthorizedContext authContext = context.getSource().getAuthContext();
        if (authContext instanceof AuthorizedContext) {
            return ((AuthorizedContext) authContext).getOperations(privilege.getUniqueKey()).contains(operations);
        } else {
            return false;
        }
    }
}
