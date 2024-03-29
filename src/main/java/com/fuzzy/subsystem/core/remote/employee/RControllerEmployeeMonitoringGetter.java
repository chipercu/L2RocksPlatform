package com.fuzzy.subsystem.core.remote.employee;

import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.QueryRemoteController;
import com.fuzzy.main.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystems.graphql.enums.EnableMonitoringType;

import java.util.ArrayList;
import java.util.HashSet;

public interface RControllerEmployeeMonitoringGetter extends QueryRemoteController {
    HashSet<Long> getEmployeesWithEnabledMonitoring (ArrayList<EnableMonitoringType> enableMonitoringType, final HashSet<Long> employeeIds, ContextTransaction contextTransaction) throws PlatformException;
    EnableMonitoringType getEmployeeMonitoringType(Long employeeId, ContextTransaction contextTransaction) throws PlatformException;
}
