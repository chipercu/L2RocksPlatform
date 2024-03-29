package com.fuzzy.subsystem.core.remote.employee;

import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.QueryRemoteController;
import com.infomaximum.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystems.graphql.enums.EnableMonitoringType;

import java.util.ArrayList;
import java.util.HashSet;

public interface RControllerEmployeeMonitoringGetter extends QueryRemoteController {
    HashSet<Long> getEmployeesWithEnabledMonitoring (ArrayList<EnableMonitoringType> enableMonitoringType, final HashSet<Long> employeeIds, ContextTransaction contextTransaction) throws PlatformException;
    EnableMonitoringType getEmployeeMonitoringType(Long employeeId, ContextTransaction contextTransaction) throws PlatformException;
}
