package com.fuzzy.subsystem.core.remote.employee;

import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.QueryRemoteController;
import com.infomaximum.platform.sdk.context.ContextTransaction;

import java.util.HashSet;

public interface RControllerEmployeeLogNotification extends QueryRemoteController {

    void startCreateEmployee(ContextTransaction context) throws PlatformException;

    void endCreateEmployee(Long employeeId, ContextTransaction context) throws PlatformException;

    void startRemoveEmployee(Long employeeId, ContextTransaction context) throws PlatformException;

    void endRemoveEmployee(Long employeeId, ContextTransaction context) throws PlatformException;

    void startChangeParentDepartment(Long employeeId, ContextTransaction context) throws PlatformException;

    void endChangeParentDepartment(Long employeeId, ContextTransaction context) throws PlatformException;

    void startMergeEmployees(long mainEmployeeId, HashSet<Long> secondaryEmployees, ContextTransaction context) throws PlatformException;

    void endMergeEmployees(long mainEmployeeId, HashSet<Long> secondaryEmployees, ContextTransaction context) throws PlatformException;
}
