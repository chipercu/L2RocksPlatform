package com.fuzzy.subsystem.core.remote.department;

import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.QueryRemoteController;
import com.fuzzy.platform.sdk.context.ContextTransaction;

public interface RControllerDepartmentLogNotification extends QueryRemoteController {

    void startChangeParentDepartment(Long departmentId, ContextTransaction context)
            throws PlatformException;

    void endChangeParentDepartment(Long departmentId, ContextTransaction context)
            throws PlatformException;
}
