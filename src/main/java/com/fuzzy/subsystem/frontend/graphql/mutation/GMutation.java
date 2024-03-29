package com.fuzzy.subsystem.frontend.graphql.mutation;

import com.fuzzy.main.cluster.core.remote.struct.RemoteObject;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLDescription;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLField;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLName;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLTypeOutObject;
import com.fuzzy.main.platform.component.frontend.authcontext.UnauthorizedContext;
import com.fuzzy.main.platform.component.frontend.context.ContextTransactionRequest;
import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.ReadableResource;
import com.fuzzy.main.platform.querypool.ResourceProvider;
import com.fuzzy.main.platform.sdk.graphql.annotation.GraphQLAuthControl;
import com.fuzzy.main.platform.sdk.graphql.customfield.graphqlquery.GraphQLQuery;
import com.fuzzy.subsystem.core.CoreSubsystem;
import com.fuzzy.subsystem.core.accessroleprivileges.EmployeePrivilegesGetter;
import com.fuzzy.subsystem.core.authcontext.employee.EmployeeAuthContext;
import com.fuzzy.subsystem.core.authcontext.employee.EmployeeSessionAuthContext;
import com.fuzzy.subsystem.core.domainobject.employee.EmployeeReadable;
import com.fuzzy.subsystem.core.remote.logon.AuthStatus;
import com.fuzzy.subsystem.core.remote.logon.EmployeeLogin;
import com.fuzzy.subsystem.core.remote.logon.RControllerEmployeeLogon;
import com.fuzzy.subsystem.core.securitylog.CoreEvent;
import com.fuzzy.subsystem.core.securitylog.CoreParameter;
import com.fuzzy.subsystem.core.securitylog.CoreTarget;
import com.fuzzy.subsystem.frontend.FrontendSubsystem;
import com.fuzzy.subsystem.frontend.graphql.query.GSession;
import com.fuzzy.subsystems.security.SecurityLog;
import com.fuzzy.subsystems.security.struct.data.event.SyslogStructDataEvent;
import com.fuzzy.subsystems.security.struct.data.target.SyslogStructDataTarget;
import org.checkerframework.checker.nullness.qual.NonNull;

@GraphQLTypeOutObject("mutation")
public class GMutation {

    @GraphQLField
    @GraphQLAuthControl(UnauthorizedContext.class)
    @GraphQLDescription("Запрос на авторизацию")
    public static GraphQLQuery<RemoteObject, GSession> logon(
            FrontendSubsystem frontEndSubSystem,
            @NonNull @GraphQLName("login") final String login,
            @NonNull @GraphQLName("password_hash") final String passwordHash
    ) {
        return new GraphQLQuery <RemoteObject, GSession>() {

            private ReadableResource<EmployeeReadable> employeeReadableResource;
            private RControllerEmployeeLogon controllerEmployeeLogon;
            private EmployeePrivilegesGetter employeePrivilegesGetter;

            @Override
            public void prepare(ResourceProvider resources) {
                employeeReadableResource = resources.getReadableResource(EmployeeReadable.class);
                controllerEmployeeLogon =
                        resources.getQueryRemoteController(CoreSubsystem.class, RControllerEmployeeLogon.class);
                employeePrivilegesGetter = new EmployeePrivilegesGetter(resources);
            }

            @Override
            public GSession execute(RemoteObject source, ContextTransactionRequest context) throws PlatformException {
                EmployeeLogin employeeLogin = controllerEmployeeLogon.logon(login, passwordHash, context);
                if (employeeLogin.authStatus == AuthStatus.SUCCESS) {

                    EmployeeSessionAuthContext authContextUser = frontEndSubSystem.getSessionService().auth(
                            employeeReadableResource.get(employeeLogin.employeeId, context.getTransaction()),
                            employeeLogin.componentUuid,
                            employeeLogin.authenticationType,
                            employeePrivilegesGetter,
                            context
                    );

                    return new GSession(authContextUser.getSessionId(), employeeLogin.employeeId, employeeLogin.authStatus);
                } else {
                    SecurityLog.info(
                            new SyslogStructDataEvent(CoreEvent.Employee.TYPE_LOGON)
                                    .withParam(CoreParameter.Employee.STATUS, employeeLogin.authStatus.name().toLowerCase())
                                    .withParam(CoreParameter.Employee.SESSION_HASH, null),
                            new SyslogStructDataTarget(CoreTarget.TYPE_EMPLOYEE, employeeLogin.employeeId)
                                    .withParam(CoreParameter.Employee.LOGIN, login),
                            context
                    );
                    return new GSession(null, null, employeeLogin.authStatus);
                }
            }
        };
    }

    @GraphQLField
    @GraphQLAuthControl(UnauthorizedContext.class)
    @GraphQLDescription("Запрос на выход из системы")
    public static GraphQLQuery<RemoteObject, Boolean> logout(
            FrontendSubsystem frontEndSubSystem
    ) {
        return new GraphQLQuery<RemoteObject, Boolean>() {

            @Override
            public void prepare(ResourceProvider resources) {
            }


            @Override
            public Boolean execute(RemoteObject source, ContextTransactionRequest context) throws PlatformException {
                if (context.getSource().getAuthContext() == null) return true;
                if (!(context.getSource().getAuthContext() instanceof EmployeeAuthContext)) return true;

                frontEndSubSystem.getSessionService().logout(context);
                return true;
            }
        };
    }
}
