package com.fuzzy.subsystem.core.remote.licensebusinessrolechecker;

import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.QueryRemoteController;
import com.fuzzy.main.platform.sdk.context.ContextTransaction;

public interface RCLicenseBusinessUserChecker extends QueryRemoteController {

    default boolean isBusinessUserEmployee(Long employeeId, ContextTransaction context) throws PlatformException {
        return false;
    }
}
