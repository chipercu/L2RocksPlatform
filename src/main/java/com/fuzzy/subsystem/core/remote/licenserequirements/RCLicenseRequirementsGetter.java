package com.fuzzy.subsystem.core.remote.licenserequirements;

import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.QueryRemoteController;
import com.fuzzy.platform.sdk.context.ContextTransaction;

import java.util.HashSet;

public interface RCLicenseRequirementsGetter extends QueryRemoteController {
    HashSet<String> getNonBusinessAdminPrivileges(ContextTransaction context) throws PlatformException;
}
