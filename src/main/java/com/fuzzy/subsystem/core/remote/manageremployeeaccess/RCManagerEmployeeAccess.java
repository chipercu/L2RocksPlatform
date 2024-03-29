package com.fuzzy.subsystem.core.remote.manageremployeeaccess;

import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.QueryRemoteController;
import com.infomaximum.platform.sdk.context.ContextTransaction;

import java.util.HashSet;

public interface RCManagerEmployeeAccess extends QueryRemoteController {

    void set(long managerId,
             HashSet<Long> insertedDepartments,
             HashSet<Long> insertedEmployees,
             HashSet<Long> removedDepartments,
             HashSet<Long> removedEmployees,
             ContextTransaction context) throws PlatformException;

    void setAll(long managerId, ContextTransaction context) throws PlatformException;

    void setState(long managerId,
                  HashSet<Long> departments,
                  HashSet<Long> employees,
                  ContextTransaction context) throws PlatformException;
}
