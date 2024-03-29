package com.fuzzy.subsystem.core.employeetoken;

import java.time.Instant;

public interface EmployeeTokenReadable {

    int FIELD_EMPLOYEE_ID = 0;
    int FIELD_TOKEN = 1;
    int FIELD_CREATED = 2;

    Long getEmployeeId();

    String getToken();

    Instant getCreationTime();
}
