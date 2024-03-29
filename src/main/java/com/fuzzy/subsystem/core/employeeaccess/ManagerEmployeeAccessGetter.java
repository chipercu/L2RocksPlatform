package com.fuzzy.subsystem.core.employeeaccess;

import com.infomaximum.platform.component.frontend.authcontext.UnauthorizedContext;
import com.infomaximum.platform.component.frontend.context.ContextTransactionRequest;
import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.QueryTransaction;
import com.infomaximum.platform.querypool.ResourceProvider;
import com.fuzzy.subsystem.core.authcontext.employee.EmployeeAuthContext;
import com.fuzzy.subsystem.core.authcontext.system.ApiKeyAuthContext;
import com.fuzzy.subsystem.core.grouping.DepartmentGrouping;
import com.fuzzy.subsystems.entityelements.Elements;
import com.fuzzy.subsystems.entityelements.EntityElementGetter;

public class ManagerEmployeeAccessGetter {

    private EntityElementGetter entityElementGetter;

    public ManagerEmployeeAccessGetter(ResourceProvider resources) {
        entityElementGetter = new EntityElementGetter(
                new ManagerEmployeeAccessEnumerator(resources),
                new DepartmentGrouping(resources)
        );
    }

    public ManagerEmployeeAccess getAccess(long managerId, QueryTransaction transaction) throws PlatformException {
        Elements access = entityElementGetter.getElements(managerId, transaction);
        return new ManagerEmployeeAccess(access);
    }

    public ManagerEmployeeAccess getAccess(ContextTransactionRequest context) throws PlatformException {
        UnauthorizedContext authContext = context.getSource().getAuthContext();
        if (authContext instanceof EmployeeAuthContext) {
            return getAccess(((EmployeeAuthContext)authContext).getEmployeeId(), context.getTransaction());
        } else if (authContext instanceof ApiKeyAuthContext) {
            Elements elements = new Elements();
            elements.isAll = true;
            return new ManagerEmployeeAccess(elements);
        }
        return new ManagerEmployeeAccess(new Elements());
    }
}
