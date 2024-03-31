package com.fuzzy.subsystem.core.employeetoken;

import com.fuzzy.platform.querypool.ResourceProvider;
import com.fuzzy.subsystem.core.CoreSubsystem;
import com.fuzzy.subsystem.core.domainobject.employeeinvitationtoken.EmployeeInvitationTokenEditable;
import com.fuzzy.subsystem.core.domainobject.employeetokenrestoreaccess.EmployeeTokenRestoreAccessEditable;

import java.time.Duration;

import static com.fuzzy.subsystem.core.CoreSubsystemConsts.Mail.INVITATION_TOKEN_EXPIRATION_DAY;

public class EmployeeTokenManagerFactory {

    public static EmployeeTokenManager<?> newEmployeeTokenRestoreAccessManager(CoreSubsystem coreSubsystem, ResourceProvider resources) {
        return new EmployeeTokenManagerImpl<>(
                resources.getRemovableResource(EmployeeTokenRestoreAccessEditable.class),
                coreSubsystem.getConfig().getTimeoutRestoreLink()
        );
    }

    public static EmployeeTokenManager<?> newEmployeeInvitationTokenManager(ResourceProvider resources) {
        return new EmployeeTokenManagerImpl<>(
                resources.getRemovableResource(EmployeeInvitationTokenEditable.class),
                Duration.ofDays(INVITATION_TOKEN_EXPIRATION_DAY));
    }
}
