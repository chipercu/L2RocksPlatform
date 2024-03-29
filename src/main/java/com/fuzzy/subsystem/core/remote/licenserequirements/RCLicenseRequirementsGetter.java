package com.fuzzy.subsystem.core.remote.licenserequirements;

import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.QueryRemoteController;
import com.infomaximum.platform.sdk.context.ContextTransaction;

import java.util.HashSet;

public interface RCLicenseRequirementsGetter extends QueryRemoteController {
    HashSet<String> getNonBusinessAdminPrivileges(ContextTransaction context) throws PlatformException;
}
