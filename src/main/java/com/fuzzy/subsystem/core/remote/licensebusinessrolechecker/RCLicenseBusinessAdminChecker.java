package com.fuzzy.subsystem.core.remote.licensebusinessrolechecker;

import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.QueryRemoteController;
import com.fuzzy.platform.sdk.context.ContextTransaction;

public interface RCLicenseBusinessAdminChecker extends QueryRemoteController {

    default boolean isBusinessAdminRole(Long accessRoleId, ContextTransaction context) throws PlatformException {
        return false;
    }

    default boolean isBusinessAdminEmployee(Long employeeId, ContextTransaction context) throws PlatformException {
        return false;
    }
}
