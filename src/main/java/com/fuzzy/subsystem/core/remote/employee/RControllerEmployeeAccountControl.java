package com.fuzzy.subsystem.core.remote.employee;

import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.QueryRemoteController;
import com.fuzzy.platform.sdk.context.ContextTransaction;

import java.util.ArrayList;

public interface RControllerEmployeeAccountControl extends QueryRemoteController {

    Long get(EmployeeData employeeData, ContextTransaction context) throws PlatformException;

    String getAdGuid(EmployeeData employeeData, ContextTransaction context) throws PlatformException;

    ArrayList<EmployeeAccountMetaData> get(Long employeeId, ContextTransaction context) throws PlatformException;
}