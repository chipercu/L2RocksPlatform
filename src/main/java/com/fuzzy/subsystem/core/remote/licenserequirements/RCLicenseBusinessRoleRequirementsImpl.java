package com.fuzzy.subsystem.core.remote.licenserequirements;

import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.AbstractQueryRController;
import com.fuzzy.main.platform.querypool.ResourceProvider;
import com.fuzzy.main.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystem.core.CoreSubsystem;
import com.fuzzy.subsystem.core.access.CorePrivilege;

import java.util.HashSet;

public class RCLicenseBusinessRoleRequirementsImpl extends AbstractQueryRController<CoreSubsystem> implements RCLicenseBusinessRoleRequirements {

    public RCLicenseBusinessRoleRequirementsImpl(CoreSubsystem component, ResourceProvider resources) {
        super(component, resources);
    }

    @Override
    public HashSet<String> getNonBusinessAdminPrivileges(ContextTransaction context) throws PlatformException {
        return new HashSet<>(){{add(CorePrivilege.PRIVATE_SETTINGS.getUniqueKey());}};
    }
}
