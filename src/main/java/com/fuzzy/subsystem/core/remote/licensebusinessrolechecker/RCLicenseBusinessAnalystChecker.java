package com.fuzzy.subsystem.core.remote.licensebusinessrolechecker;

import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.QueryRemoteController;
import com.fuzzy.main.platform.sdk.context.ContextTransaction;

public interface RCLicenseBusinessAnalystChecker extends QueryRemoteController {

    default boolean isBusinessAnalystRole(Long accessRoleId, ContextTransaction context) throws PlatformException {
        return false;
    }

    default boolean isBusinessAnalystEmployee(Long employeeId, ContextTransaction context) throws PlatformException {
        return false;
    }
}
