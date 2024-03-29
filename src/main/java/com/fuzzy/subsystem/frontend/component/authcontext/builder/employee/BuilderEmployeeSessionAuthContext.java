package com.fuzzy.subsystem.frontend.component.authcontext.builder.employee;

import com.fuzzy.main.cluster.graphql.struct.GRequest;
import com.fuzzy.main.platform.component.frontend.request.GRequestHttp;
import com.fuzzy.main.platform.component.frontend.request.GRequestWebSocket;
import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.QueryTransaction;
import com.fuzzy.main.platform.querypool.ReadableResource;
import com.fuzzy.main.platform.querypool.ResourceProvider;
import com.fuzzy.main.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystem.core.CoreSubsystem;
import com.fuzzy.subsystem.core.access.CorePrivilege;
import com.fuzzy.subsystem.core.accessroleprivileges.EmployeePrivilegesGetter;
import com.fuzzy.subsystem.core.authcontext.employee.EmployeeSessionAuthContext;
import com.fuzzy.subsystem.core.domainobject.employee.EmployeeReadable;
import com.fuzzy.subsystem.core.remote.liscense.RCLicenseGetter;
import com.fuzzy.subsystem.frontend.FrontendSubsystem;
import com.fuzzy.subsystem.frontend.authcontext.AuthorizedContext;
import com.fuzzy.subsystem.frontend.component.authcontext.builder.BuilderAuthContext;
import com.fuzzy.subsystem.frontend.service.session.SessionEmployee;
import com.fuzzy.subsystems.access.AccessOperation;
import com.fuzzy.subsystems.access.AccessOperationCollection;
import com.fuzzy.subsystems.exception.GeneralExceptionBuilder;
import jakarta.servlet.http.Cookie;

import java.util.HashMap;

public class BuilderEmployeeSessionAuthContext implements BuilderAuthContext {

    public static final String REQUEST_PARAM_SESSION = "session";

    private final FrontendSubsystem frontEndSubSystem;

    private String sessionUuid;

    private ReadableResource<EmployeeReadable> employeeReadableResource;
    private EmployeePrivilegesGetter employeePrivilegesGetter;
    private RCLicenseGetter rcLicenseGetter;

    public BuilderEmployeeSessionAuthContext(FrontendSubsystem frontEndSubSystem) {
        this.frontEndSubSystem = frontEndSubSystem;
    }

    @Override
    public boolean prepare(ResourceProvider resources, GRequest gRequest) {
        sessionUuid = getValidAuthSession(gRequest);
        if (sessionUuid != null) {
            this.employeeReadableResource = resources.getReadableResource(EmployeeReadable.class);
            this.employeePrivilegesGetter = new EmployeePrivilegesGetter(resources);
            rcLicenseGetter = frontEndSubSystem.getRemotes().get(CoreSubsystem.class, RCLicenseGetter.class);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public AuthorizedContext auth(GRequest gRequest, ContextTransaction context) throws PlatformException {
        QueryTransaction transaction = context.getTransaction();
        SessionEmployee session = frontEndSubSystem.getSessionService().getAuthSession(sessionUuid);
        if (session != null) {
            EmployeeReadable employeeReadable = employeeReadableResource.get(session.employeeId, transaction);
            if (employeeReadable != null) {
                HashMap<String, AccessOperationCollection> privileges =
                        employeePrivilegesGetter.getPrivileges(session.employeeId, context);
                if (!rcLicenseGetter.hasActualLicense()) {
                    AccessOperationCollection lkiOperation =
                            privileges.get(CorePrivilege.LICENSE_KEY_INSERTION.getUniqueKey());
                    throw GeneralExceptionBuilder.buildCurrentLicenseIsExpiredException(
                            lkiOperation != null && lkiOperation.contains(AccessOperation.EXECUTE));
                }
                EmployeeSessionAuthContext employeeSessionAuthContext = new EmployeeSessionAuthContext(
                        privileges,
                        session.employeeId,
                        employeeReadable.getLogin(),
                        sessionUuid
                );
                employeeSessionAuthContext.addParams(session.getParams());
                return employeeSessionAuthContext;
            }
        }
        return null;
    }

    @Override
    public String getBuilderName() throws PlatformException{
        return this.getClass().getSimpleName();
    }

    private boolean isValidAuthSession(String sessionUuid) {
        if (sessionUuid == null) {
            return false;
        }
        SessionEmployee session = frontEndSubSystem.getSessionService().getAuthSession(sessionUuid);
        return session != null;
    }

    private String getValidAuthSession(GRequest gRequest) {
        if (gRequest instanceof GRequestHttp) {
            GRequestHttp gRequestHttp = (GRequestHttp) gRequest;

            String sessionUuid = gRequestHttp.getParameter(REQUEST_PARAM_SESSION);
            if (sessionUuid == null) {
                Cookie cookie = gRequestHttp.getCookie(REQUEST_PARAM_SESSION);
                if (cookie != null) sessionUuid = cookie.getValue();
            }

            if (isValidAuthSession(sessionUuid)) {
                return sessionUuid;
            } else {
                return null;
            }
        } else if (gRequest instanceof GRequestWebSocket) {
            GRequestWebSocket gRequestWebSocket = (GRequestWebSocket) gRequest;

            //Проверяем переопределенную авторизацию вебсокета
            String sessionUuid = gRequestWebSocket.getSessionUuid();
            if (isValidAuthSession(sessionUuid)) {
                return sessionUuid;
            }

            //Проверяем базовый реквест на наличие возможных авторизационных данных
            sessionUuid = gRequestWebSocket.getParameter(REQUEST_PARAM_SESSION);
            if (sessionUuid == null) {
                Cookie cookie = gRequestWebSocket.getCookie(REQUEST_PARAM_SESSION);
                if (cookie != null) sessionUuid = cookie.getValue();
            }
            if (isValidAuthSession(sessionUuid)) {
                return sessionUuid;
            }

            return null;
        } else {
            throw new RuntimeException("Unknown type request: " + gRequest);
        }
    }

    @Override
    public String toString() {
        return "BuilderEmployeeSessionAuthContext";
    }
}
