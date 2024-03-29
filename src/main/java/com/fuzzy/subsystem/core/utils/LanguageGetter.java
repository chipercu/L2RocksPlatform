package com.fuzzy.subsystem.core.utils;

import com.infomaximum.platform.component.frontend.authcontext.UnauthorizedContext;
import com.infomaximum.platform.component.frontend.context.ContextTransactionRequest;
import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.QueryTransaction;
import com.infomaximum.platform.querypool.ReadableResource;
import com.infomaximum.platform.querypool.ResourceProvider;
import com.infomaximum.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystem.core.authcontext.employee.EmployeeAuthContext;
import com.fuzzy.subsystem.core.config.CoreConfigDescription;
import com.fuzzy.subsystem.core.config.CoreConfigGetter;
import com.fuzzy.subsystem.core.config.Language;
import com.fuzzy.subsystem.core.domainobject.employee.EmployeeReadable;
import com.fuzzy.subsystems.exception.GeneralExceptionBuilder;

public class LanguageGetter {

    private final ReadableResource<EmployeeReadable> employees;
    private final CoreConfigGetter coreConfigGetter;

    public LanguageGetter(ResourceProvider resources) {
        employees = resources.getReadableResource(EmployeeReadable.class);
        coreConfigGetter = new CoreConfigGetter(resources);
    }

    public Language get(ContextTransactionRequest context) throws PlatformException {
        QueryTransaction transaction = context.getTransaction();
        UnauthorizedContext authContext = context.getSource().getAuthContext();
        if (authContext instanceof EmployeeAuthContext) {
            return getById(((EmployeeAuthContext)authContext).getEmployeeId(), transaction);
        }

        return getSystem(transaction);
    }

    public Language get(ContextTransaction<?> context) throws PlatformException {
        if (context instanceof ContextTransactionRequest) {
            return get((ContextTransactionRequest) context);
        }
        return getSystem(context.getTransaction());
    }

    public Language getById(Long employeeId, QueryTransaction transaction) throws PlatformException {
        EmployeeReadable employee = employees.get(employeeId, transaction);
        if (employee == null) {
            throw GeneralExceptionBuilder.buildNotFoundDomainObjectException(EmployeeReadable.class, employeeId);
        }
        return get(employee, transaction);
    }

    public Language get(EmployeeReadable employee, QueryTransaction transaction) throws PlatformException {
        return employee.getLanguage() != null ? employee.getLanguage() : getSystem(transaction);
    }

    public Language getSystem(QueryTransaction transaction) throws PlatformException {
        return coreConfigGetter.get(CoreConfigDescription.SERVER_LANGUAGE, transaction);
    }
}
