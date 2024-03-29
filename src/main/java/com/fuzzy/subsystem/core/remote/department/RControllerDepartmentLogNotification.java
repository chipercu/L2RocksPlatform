package com.fuzzy.subsystem.core.remote.department;

import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.QueryRemoteController;
import com.fuzzy.main.platform.sdk.context.ContextTransaction;

public interface RControllerDepartmentLogNotification extends QueryRemoteController {

    void startChangeParentDepartment(Long departmentId, ContextTransaction context)
            throws PlatformException;

    void endChangeParentDepartment(Long departmentId, ContextTransaction context)
            throws PlatformException;
}
