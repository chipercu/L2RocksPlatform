package com.fuzzy.subsystem.core.remote.licenserequirements;

import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.AbstractQueryRController;
import com.fuzzy.platform.querypool.ResourceProvider;
import com.fuzzy.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystem.core.CoreSubsystem;

import java.util.HashSet;
import java.util.Set;

public class RCLicenseRequirementsGetterImpl extends AbstractQueryRController<CoreSubsystem> implements RCLicenseRequirementsGetter {
    private final Set<RCLicenseBusinessRoleRequirements> rcLicenseBusinessRoleRequirementsSet;

    public RCLicenseRequirementsGetterImpl(CoreSubsystem component, ResourceProvider resources) {
        super(component, resources);
        rcLicenseBusinessRoleRequirementsSet = resources.getQueryRemoteControllers(RCLicenseBusinessRoleRequirements.class);
    }

    public HashSet<String> getNonBusinessAdminPrivileges(ContextTransaction context) throws PlatformException {
        HashSet<String> nonBusinessAdminPrivileges = new HashSet<>();
        for (RCLicenseBusinessRoleRequirements rc : rcLicenseBusinessRoleRequirementsSet) {
            HashSet<String> modulePrivileges = rc.getNonBusinessAdminPrivileges(context);
            if (modulePrivileges != null) {
                nonBusinessAdminPrivileges.addAll(modulePrivileges);
            }
        }
        return nonBusinessAdminPrivileges;
    }
}
