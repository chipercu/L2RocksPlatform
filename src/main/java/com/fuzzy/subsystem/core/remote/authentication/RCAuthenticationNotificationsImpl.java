package com.fuzzy.subsystem.core.remote.authentication;

import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.*;
import com.fuzzy.main.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystem.core.CoreSubsystem;
import com.fuzzy.subsystem.core.CoreSubsystemConsts;
import com.fuzzy.subsystem.core.config.CoreConfigDescription;
import com.fuzzy.subsystem.core.config.CoreConfigSetter;
import com.fuzzy.subsystem.core.domainobject.authentication.AuthenticationReadable;
import com.fuzzy.subsystem.core.domainobject.employee.EmployeeEditable;
import com.fuzzy.subsystem.core.domainobject.employeeauthorizationhistory.EmployeeAuthorizationHistoryEditable;
import com.fuzzy.subsystem.core.domainobject.employeeinvitationtoken.EmployeeInvitationTokenEditable;
import com.fuzzy.subsystem.core.domainobject.employeetokenrestoreaccess.EmployeeTokenRestoreAccessEditable;
import com.fuzzy.subsystem.core.domainobject.usedpassword.UsedPasswordEditable;

import java.util.Objects;

public class RCAuthenticationNotificationsImpl extends AbstractQueryRController<CoreSubsystem>
        implements RCAuthenticationNotifications {

    private final ReadableResource<AuthenticationReadable> authenticationReadableResource;
    private final EditableResource<EmployeeEditable> employeeEditableResource;
    private final RemovableResource<EmployeeAuthorizationHistoryEditable> employeeAuthorizationHistoryRemovableResource;
    private final RemovableResource<EmployeeInvitationTokenEditable> employeeInvitationTokenRemovableResource;
    private final RemovableResource<EmployeeTokenRestoreAccessEditable> employeeTokenRestoreAccessRemovableResource;
    private final RemovableResource<UsedPasswordEditable> usedPasswordRemovableResource;
    private final CoreConfigSetter coreConfigSetter;

    public RCAuthenticationNotificationsImpl(CoreSubsystem component, ResourceProvider resources) {
        super(component, resources);
        authenticationReadableResource = resources.getReadableResource(AuthenticationReadable.class);
        employeeEditableResource = resources.getEditableResource(EmployeeEditable.class);
        employeeAuthorizationHistoryRemovableResource =
                resources.getRemovableResource(EmployeeAuthorizationHistoryEditable.class);
        employeeInvitationTokenRemovableResource = resources.getRemovableResource(EmployeeInvitationTokenEditable.class);
        employeeTokenRestoreAccessRemovableResource =
                resources.getRemovableResource(EmployeeTokenRestoreAccessEditable.class);
        usedPasswordRemovableResource = resources.getRemovableResource(UsedPasswordEditable.class);
        coreConfigSetter = new CoreConfigSetter(resources);
    }

    @Override
    public void onBeforeRemoval(long id, ContextTransaction context) throws PlatformException {
        AuthenticationReadable authentication = authenticationReadableResource.get(id, context.getTransaction());
        if (authentication != null
                && Objects.equals(authentication.getType(), CoreSubsystemConsts.AuthenticationTypes.INTEGRATED)) {
            coreConfigSetter.resetToDefault(CoreConfigDescription.SecurityConfig.COMPLEX_PASSWORD, context);
            coreConfigSetter.resetToDefault(CoreConfigDescription.SecurityConfig.MIN_PASSWORD_LENGTH, context);
            coreConfigSetter.resetToDefault(CoreConfigDescription.SecurityConfig.PASSWORD_EXPIRATION_TIME, context);
            coreConfigSetter.resetToDefault(CoreConfigDescription.SecurityConfig.MAX_INVALID_LOGON_COUNT, context);
            QueryTransaction transaction = context.getTransaction();
            employeeAuthorizationHistoryRemovableResource.clear(transaction);
            employeeInvitationTokenRemovableResource.clear(transaction);
            employeeTokenRestoreAccessRemovableResource.clear(transaction);
            usedPasswordRemovableResource.clear(transaction);
            employeeEditableResource.forEach(employee -> {
                employee.setPasswordHashWithSalt(null);
                employee.setNeedToChangePassword(false);
                employeeEditableResource.save(employee, transaction);
            }, transaction);
        }
    }
}
