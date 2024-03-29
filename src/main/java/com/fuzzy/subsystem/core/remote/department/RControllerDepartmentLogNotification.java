package com.fuzzy.subsystem.core.remote.department;

import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.QueryRemoteController;
import com.infomaximum.platform.sdk.context.ContextTransaction;

public interface RControllerDepartmentLogNotification extends QueryRemoteController {

    void startChangeParentDepartment(Long departmentId, ContextTransaction context)
            throws PlatformException;

    void endChangeParentDepartment(Long departmentId, ContextTransaction context)
            throws PlatformException;
}
