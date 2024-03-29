package com.fuzzy.subsystem.core.remote.department;

import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.QueryRemoteController;
import com.infomaximum.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystem.core.domainobject.department.DepartmentReadable;
import com.fuzzy.subsystems.remote.RemovalData;

import java.util.HashSet;

public interface RControllerDepartmentControl extends QueryRemoteController {

    DepartmentReadable create(DepartmentBuilder departmentBuilder, ContextTransaction context) throws PlatformException;

    DepartmentReadable update(long departmentId, DepartmentBuilder departmentBuilder, ContextTransaction context) throws PlatformException;

    RemovalData removeWithCauses(final HashSet<Long> departmentIds, ContextTransaction context) throws PlatformException;

    default HashSet<Long> remove(final HashSet<Long> departmentIds, ContextTransaction context) throws PlatformException {
        return removeWithCauses(departmentIds, context).getRemoved();
    }
}