package com.fuzzy.subsystem.core.remote.employee;

import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.QueryRemoteController;
import com.fuzzy.main.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystem.core.domainobject.employee.EmployeeReadable;

import java.util.HashSet;

public interface RControllerEmployeeNotification extends QueryRemoteController {

    void onBeforeRemoveEmployee(Long employeeId, ContextTransaction context) throws PlatformException;

    void onAfterCreateEmployee(Long employeeId, ContextTransaction context) throws PlatformException;

    void onBeforeUpdateEmployee(EmployeeReadable employee, EmployeeBuilder changes, ContextTransaction context)
            throws PlatformException;

    void onAfterUpdateEmployee(EmployeeReadable employee, ContextTransaction context)
            throws PlatformException;

    void onBeforeMergeEmployees(long mainEmployeeId, HashSet<Long> secondaryEmployees, ContextTransaction context)
            throws PlatformException;
}
