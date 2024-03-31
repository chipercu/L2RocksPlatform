package com.fuzzy.subsystem.core.remote.department;

import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.QueryRemoteController;
import com.fuzzy.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystem.core.domainobject.department.DepartmentReadable;

public interface RControllerDepartmentNotification extends QueryRemoteController {

    void onBeforeUpdateDepartment(DepartmentReadable department, DepartmentBuilder changes, ContextTransaction context)
            throws PlatformException;

    void onBeforeRemoveDepartment(Long departmentId, ContextTransaction context) throws PlatformException;

    void onAfterUpdateParentDepartment(Long departmentId, ContextTransaction context) throws PlatformException;
}
