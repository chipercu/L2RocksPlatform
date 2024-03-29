package com.fuzzy.subsystem.core.remote.department;

import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.QueryRemoteController;
import com.infomaximum.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystem.core.domainobject.department.DepartmentReadable;

public interface RControllerDepartmentNotification extends QueryRemoteController {

    void onBeforeUpdateDepartment(DepartmentReadable department, DepartmentBuilder changes, ContextTransaction context)
            throws PlatformException;

    void onBeforeRemoveDepartment(Long departmentId, ContextTransaction context) throws PlatformException;

    void onAfterUpdateParentDepartment(Long departmentId, ContextTransaction context) throws PlatformException;
}
