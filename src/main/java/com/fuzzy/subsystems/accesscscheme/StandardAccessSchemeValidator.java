package com.fuzzy.subsystems.accesscscheme;

import com.infomaximum.database.domainobject.DomainObject;
import com.infomaximum.platform.component.frontend.authcontext.UnauthorizedContext;
import com.infomaximum.platform.component.frontend.context.ContextTransactionRequest;
import com.infomaximum.platform.exception.PlatformException;
import com.fuzzy.subsystem.core.authcontext.employee.EmployeeAuthContext;
import com.fuzzy.subsystem.core.authcontext.system.ApiKeyAuthContext;
import com.fuzzy.subsystems.accesscscheme.domainobject.AccessSchemeItem;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Collection;

public abstract class StandardAccessSchemeValidator<
        O, K,
        T extends DomainObject & AccessSchemeItem<Long, O, StandardAccessOperation>,
        U extends DomainObject & AccessSchemeItem<Long, O, StandardAccessOperation>> extends StandardAccessValidator<O, K> {

    private final AccessSchemeItemGetter<Long, O, StandardAccessOperation, T> employeeAccessGetter;
    private final AccessSchemeItemGetter<Long, O, StandardAccessOperation, U> apiKeyAccessGetter;

    public StandardAccessSchemeValidator(@NonNull AccessSchemeItemGetter<Long, O, StandardAccessOperation, T> employeeAccessGetter,
                                         @Nullable AccessSchemeItemGetter<Long, O, StandardAccessOperation, U> apiKeyAccessGetter) {
        this.employeeAccessGetter = employeeAccessGetter;
        this.apiKeyAccessGetter = apiKeyAccessGetter;
    }

    @Override
    public Collection<StandardAccessOperation> getAccessOperationsById(@NonNull O objectId,
                                                                       @NonNull ContextTransactionRequest context) throws PlatformException {
        UnauthorizedContext authContext = context.getSource().getAuthContext();
        if (authContext instanceof EmployeeAuthContext) {
            long employeeId = ((EmployeeAuthContext) authContext).getEmployeeId();
            return employeeAccessGetter.getOperations(employeeId, objectId, context.getTransaction());
        } else if (apiKeyAccessGetter != null && authContext instanceof ApiKeyAuthContext) {
            long apiKeyId = ((ApiKeyAuthContext) authContext).getApiKeyId();
            return apiKeyAccessGetter.getOperations(apiKeyId, objectId, context.getTransaction());
        }
        return null;
    }

    @Override
    public Collection<StandardAccessOperation> getAccessOperationsByObject(@NonNull K object,
                                                                           @NonNull ContextTransactionRequest context) throws PlatformException {
        return getAccessOperationsById(getId(object), context);
    }

    protected abstract @NonNull O getId(@NonNull K object) throws PlatformException;
}
