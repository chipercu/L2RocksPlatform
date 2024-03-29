package com.fuzzy.subsystem.core.remote.licenserequirements;

import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.QueryRemoteController;
import com.fuzzy.main.platform.sdk.context.ContextTransaction;

import java.util.HashSet;

public interface RCLicenseRequirementsGetter extends QueryRemoteController {
    HashSet<String> getNonBusinessAdminPrivileges(ContextTransaction context) throws PlatformException;
}
