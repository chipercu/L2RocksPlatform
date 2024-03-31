package com.fuzzy.subsystem.core.remote.licensebusinessrolechecker;

import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.QueryRemoteController;
import com.fuzzy.platform.sdk.context.ContextTransaction;

public interface RCLicenseBusinessUserChecker extends QueryRemoteController {

    default boolean isBusinessUserEmployee(Long employeeId, ContextTransaction context) throws PlatformException {
        return false;
    }
}
