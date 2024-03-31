package com.fuzzy.subsystem.core.remote.manageremployeeaccess;

import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.QueryRemoteController;
import com.fuzzy.platform.sdk.context.ContextTransaction;

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
