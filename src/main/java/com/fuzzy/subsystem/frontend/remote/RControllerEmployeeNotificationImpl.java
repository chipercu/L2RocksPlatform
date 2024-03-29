package com.fuzzy.subsystem.frontend.remote;

import com.infomaximum.platform.component.frontend.authcontext.UnauthorizedContext;
import com.infomaximum.platform.component.frontend.context.source.SourceGRequest;
import com.infomaximum.platform.component.frontend.context.source.SourceGRequestAuth;
import com.infomaximum.platform.querypool.AbstractQueryRController;
import com.infomaximum.platform.querypool.ResourceProvider;
import com.infomaximum.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystem.core.authcontext.employee.EmployeeSessionAuthContext;
import com.fuzzy.subsystem.core.domainobject.employee.EmployeeReadable;
import com.fuzzy.subsystem.core.remote.employee.EmployeeBuilder;
import com.fuzzy.subsystem.core.remote.employee.RControllerEmployeeNotification;
import com.fuzzy.subsystem.frontend.FrontendSubsystem;
import com.fuzzy.subsystem.frontend.service.session.SessionServiceEmployee;

import java.util.HashSet;

public class RControllerEmployeeNotificationImpl extends AbstractQueryRController<FrontendSubsystem>
        implements RControllerEmployeeNotification {

    private final SessionServiceEmployee sessionService;

    public RControllerEmployeeNotificationImpl(FrontendSubsystem component, ResourceProvider resources) {
        super(component, resources);
        this.sessionService = component.getSessionService();
    }

    @Override
    public void onBeforeRemoveEmployee(Long employeeId, ContextTransaction context) {
        context.getTransaction().addCommitListener(() -> sessionService.clearSessions(employeeId, context));
    }

    @Override
    public void onAfterCreateEmployee(Long employeeId, ContextTransaction context) {
    }

    @Override
    public void onBeforeUpdateEmployee(EmployeeReadable employee, EmployeeBuilder changes, ContextTransaction context) {
        if (changes.isContainPasswordHash()) {
            //Смена пароля - чистим все сессии кроме активной
            context.getTransaction().addCommitListener(() -> {
                String exceptSessionId = null;
                if (context.getSource() instanceof SourceGRequest) {
                    SourceGRequestAuth sourceGRequestAuth = (SourceGRequestAuth) context.getSource();
                    UnauthorizedContext authContext = sourceGRequestAuth.getAuthContext();
                    exceptSessionId = (authContext instanceof EmployeeSessionAuthContext) ? ((EmployeeSessionAuthContext) authContext).getSessionId() : null;
                }
                sessionService.clearSessions(employee.getId(), exceptSessionId, context);
            });
        }
        //Изменение возможности входа обрабатывается в RCEnabledLogonNotificationImpl
    }

    @Override
    public void onAfterUpdateEmployee(EmployeeReadable employee, ContextTransaction context) {

    }

    @Override
    public void onBeforeMergeEmployees(long mainEmployeeId, HashSet<Long> secondaryEmployees, ContextTransaction context) {
        for (Long employeeId : secondaryEmployees) {
            onBeforeRemoveEmployee(employeeId, context);
        }
    }
}
