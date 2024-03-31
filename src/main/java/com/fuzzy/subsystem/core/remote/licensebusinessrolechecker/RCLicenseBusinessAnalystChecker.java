package com.fuzzy.subsystem.core.remote.licensebusinessrolechecker;

import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.QueryRemoteController;
import com.fuzzy.platform.sdk.context.ContextTransaction;

public interface RCLicenseBusinessAnalystChecker extends QueryRemoteController {

    default boolean isBusinessAnalystRole(Long accessRoleId, ContextTransaction context) throws PlatformException {
        return false;
    }

    default boolean isBusinessAnalystEmployee(Long employeeId, ContextTransaction context) throws PlatformException {
        return false;
    }
}
