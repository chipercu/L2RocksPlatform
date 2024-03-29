package com.fuzzy.subsystem.core.employeetoken;

import java.time.Instant;

public interface EmployeeTokenEditable extends EmployeeTokenReadable {

    void setEmployeeId(Long employeeId);

    void setToken(String token);

    void setCreationTime(Instant creationTime);
}
