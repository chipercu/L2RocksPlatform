package com.fuzzy.subsystem.core.remote.serverinit;

import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.AbstractQueryRController;
import com.infomaximum.platform.querypool.ResourceProvider;
import com.infomaximum.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystem.core.CoreSubsystem;
import com.fuzzy.subsystem.core.CoreSubsystemConsts;
import com.fuzzy.subsystem.core.access.CorePrivilege;
import com.fuzzy.subsystem.core.config.Language;
import com.fuzzy.subsystem.core.domainobject.authentication.AuthenticationReadable;
import com.fuzzy.subsystem.core.remote.accessroleprivileges.PrivilegeValue;
import com.fuzzy.subsystem.core.remote.accessroleprivileges.RControllerAccessRolePrivileges;
import com.fuzzy.subsystem.core.remote.authentication.AuthenticationCreatingBuilder;
import com.fuzzy.subsystem.core.remote.authentication.RCAuthentication;
import com.fuzzy.subsystem.core.remote.employeeauthentication.RCEmployeeAuthentication;
import com.fuzzy.subsystems.access.AccessOperation;
import com.fuzzy.subsystems.access.AccessOperationCollection;

public class RControllerServerInitNotificationImpl extends AbstractQueryRController<CoreSubsystem>
        implements RControllerServerInitNotification {

    private final CoreSubsystem component;
    private final RControllerAccessRolePrivileges rControllerAccessRolePrivileges;
    private final RCAuthentication rcAuthentication;
    private final RCEmployeeAuthentication rcEmployeeAuthentication;

    public RControllerServerInitNotificationImpl(CoreSubsystem component, ResourceProvider resources) {
        super(component, resources);
        this.component = component;
        rControllerAccessRolePrivileges =
                resources.getQueryRemoteController(CoreSubsystem.class, RControllerAccessRolePrivileges.class);
        rcAuthentication = resources.getQueryRemoteController(CoreSubsystem.class, RCAuthentication.class);
        rcEmployeeAuthentication = resources.getQueryRemoteController(CoreSubsystem.class, RCEmployeeAuthentication.class);
    }

    @Override
    public void onServerInit(ServerInitData serverInitData, ContextTransaction context) throws PlatformException {
        initAdministratorPrivileges(serverInitData.getAdministratorAccessRoleId(), context);
        initSecurityAdministratorPrivileges(serverInitData.getSecurityAdministratorAccessRoleId(), context);
        initAuthentications(serverInitData.getEmployeeId(), serverInitData.getServerLanguage(), context);
    }

    private void initAdministratorPrivileges(long accessRoleId, ContextTransaction<?> context)
            throws PlatformException {
        PrivilegeValue[] privilegeValues = CorePrivilege.getAdminPrivileges();
        rControllerAccessRolePrivileges.setPrivilegesToAccessRole(accessRoleId, privilegeValues, context);
    }

    private void initSecurityAdministratorPrivileges(long accessRoleId, ContextTransaction<?> context)
            throws PlatformException {
        PrivilegeValue[] privilegeValues = new PrivilegeValue[]{
                new PrivilegeValue(CorePrivilege.GENERAL_SETTINGS.getUniqueKey(), new AccessOperationCollection(
                        AccessOperation.READ
                )),
                new PrivilegeValue(CorePrivilege.API_KEYS.getUniqueKey(), new AccessOperationCollection(
                        AccessOperation.READ
                )),
                new PrivilegeValue(CorePrivilege.AUTHENTICATION.getUniqueKey(), new AccessOperationCollection(
                        AccessOperation.READ
                )),
                new PrivilegeValue(CorePrivilege.EMPLOYEES.getUniqueKey(), new AccessOperationCollection(
                        AccessOperation.READ
                )),
                new PrivilegeValue(CorePrivilege.EMPLOYEE_ACCESS.getUniqueKey(), new AccessOperationCollection(
                        AccessOperation.READ
                )),
                new PrivilegeValue(CorePrivilege.ACCESS_ROLE.getUniqueKey(), new AccessOperationCollection(
                        AccessOperation.READ
                )),
                new PrivilegeValue(CorePrivilege.PRIVATE_SETTINGS.getUniqueKey(), new AccessOperationCollection(
                        AccessOperation.WRITE
                )),
                new PrivilegeValue(CorePrivilege.GRAPHQL_TOOL.getUniqueKey(), new AccessOperationCollection(
                        AccessOperation.EXECUTE
                )),
                new PrivilegeValue(CorePrivilege.TAG_SETTINGS.getUniqueKey(), new AccessOperationCollection(
                        AccessOperation.READ
                ))
        };
        rControllerAccessRolePrivileges.setPrivilegesToAccessRole(accessRoleId, privilegeValues, context);
    }

    private void initAuthentications(long employeeId,
                                     Language language,
                                     ContextTransaction<?> context) throws PlatformException {
        String name = component.getMessageSource().getString(
                CoreSubsystemConsts.Localization.AuthenticationName.INTEGRATED, language);
        AuthenticationCreatingBuilder builder = new AuthenticationCreatingBuilder(
                name, CoreSubsystemConsts.AuthenticationTypes.INTEGRATED);
        AuthenticationReadable authentication = rcAuthentication.create(builder, context);
        rcEmployeeAuthentication.assignAuthenticationToEmployee(authentication.getId(), employeeId, context);
    }
}