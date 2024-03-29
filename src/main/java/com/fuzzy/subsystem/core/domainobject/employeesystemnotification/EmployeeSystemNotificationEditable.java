package com.fuzzy.subsystem.core.domainobject.employeesystemnotification;

import com.infomaximum.database.domainobject.DomainObjectEditable;

public class EmployeeSystemNotificationEditable extends EmployeeSystemNotificationReadable  implements DomainObjectEditable {
    public EmployeeSystemNotificationEditable(long id) {
        super(id);
    }

    public void setIdEmployee(Long employeeId) {
        set(FIELD_ID_EMPLOYEE, employeeId);
    }

    public void setMessageHash(Integer massageHash) {
        set(FIELD_MESSAGE_HASH, massageHash);
    }
}
