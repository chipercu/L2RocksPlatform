package com.fuzzy.subsystem.core.remote.licensebusinessrolechecker;

import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.QueryRemoteController;
import com.infomaximum.platform.sdk.context.ContextTransaction;

public interface RCLicenseBusinessUserChecker extends QueryRemoteController {

    default boolean isBusinessUserEmployee(Long employeeId, ContextTransaction context) throws PlatformException {
        return false;
    }
}
