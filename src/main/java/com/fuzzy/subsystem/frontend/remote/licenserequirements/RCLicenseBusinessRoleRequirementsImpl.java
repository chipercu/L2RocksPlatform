package com.fuzzy.subsystem.frontend.remote.licenserequirements;

import com.fuzzy.platform.querypool.AbstractQueryRController;
import com.fuzzy.platform.querypool.ResourceProvider;
import com.fuzzy.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystem.core.remote.licenserequirements.RCLicenseBusinessRoleRequirements;
import com.fuzzy.subsystem.frontend.FrontendSubsystem;
import com.fuzzy.subsystem.frontend.access.FrontendPrivilege;

import java.util.HashSet;

public class RCLicenseBusinessRoleRequirementsImpl extends AbstractQueryRController<FrontendSubsystem> implements RCLicenseBusinessRoleRequirements {
    public RCLicenseBusinessRoleRequirementsImpl(FrontendSubsystem component, ResourceProvider resources) {
        super(component, resources);
    }

    @Override
    public HashSet<String> getNonBusinessAdminPrivileges(ContextTransaction context) {
        return new HashSet<>(){{add(FrontendPrivilege.DOCUMENTATION_ACCESS.getUniqueKey());}};
    }
}
