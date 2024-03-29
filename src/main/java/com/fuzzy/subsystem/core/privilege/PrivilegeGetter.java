package com.fuzzy.subsystem.core.privilege;

import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.ResourceProvider;
import com.fuzzy.main.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystem.core.config.Language;
import com.fuzzy.subsystem.core.remote.privilege.RControllerPrivilegeGetter;
import com.fuzzy.subsystems.access.AccessOperationCollection;
import com.fuzzy.subsystems.exception.GeneralExceptionBuilder;

import java.util.Set;

public class PrivilegeGetter {

    private Set<RControllerPrivilegeGetter> rControllerPrivilegeGetters;

    public PrivilegeGetter(ResourceProvider resources) {
        rControllerPrivilegeGetters = resources.getQueryRemoteControllers(RControllerPrivilegeGetter.class);
    }

    public String getPrivilegeDisplayName(String privilegeKey, Language language, ContextTransaction context)
            throws PlatformException {
        for (RControllerPrivilegeGetter rControllerPrivilegeGetter : rControllerPrivilegeGetters) {
            String displayName = rControllerPrivilegeGetter.getPrivilegeDisplayName(privilegeKey, language, context);
            if (!displayName.equals(privilegeKey)) {
                return displayName;
            }
        }
        throw GeneralExceptionBuilder.buildInvalidValueException("privilege_key", privilegeKey);
    }

    public AccessOperationCollection getAvailableOperations(String privilegeKey, ContextTransaction context)
            throws PlatformException {
        for (RControllerPrivilegeGetter rControllerPrivilegeGetter : rControllerPrivilegeGetters) {
            AccessOperationCollection availableOperations =
                    rControllerPrivilegeGetter.getAvailableOperations(privilegeKey, context);
            if (availableOperations != null) {
                return availableOperations;
            }
        }
        throw GeneralExceptionBuilder.buildInvalidValueException("privilege_key", privilegeKey);
    }
}
