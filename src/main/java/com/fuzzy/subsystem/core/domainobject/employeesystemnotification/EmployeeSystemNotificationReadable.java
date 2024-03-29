package com.fuzzy.subsystem.core.domainobject.employeesystemnotification;

import com.infomaximum.database.anotation.Entity;
import com.infomaximum.database.anotation.Field;
import com.infomaximum.database.anotation.HashIndex;
import com.fuzzy.subsystem.core.CoreSubsystemConsts;
import com.fuzzy.subsystem.core.domainobject.employee.EmployeeReadable;
import com.fuzzy.subsystems.remote.RDomainObject;

@Entity(
        namespace = CoreSubsystemConsts.UUID,
        name = "EmployeeSystemNotification",
        fields = {
                @Field(name = "message_hash", number = EmployeeSystemNotificationReadable.FIELD_MESSAGE_HASH,
                        type = Integer.class),
                @Field(name = "id_employee", number = EmployeeSystemNotificationReadable.FIELD_ID_EMPLOYEE,
                        type = Long.class, foreignDependency = EmployeeReadable.class),
        },
        hashIndexes = {
                @HashIndex(fields = { EmployeeSystemNotificationReadable.FIELD_ID_EMPLOYEE }),
                @HashIndex(fields = { EmployeeSystemNotificationReadable.FIELD_MESSAGE_HASH }),
                @HashIndex(fields = {
                        EmployeeSystemNotificationReadable.FIELD_ID_EMPLOYEE,
                        EmployeeSystemNotificationReadable.FIELD_MESSAGE_HASH
                })
        }
)
public class EmployeeSystemNotificationReadable extends RDomainObject {
    public final static int FIELD_MESSAGE_HASH = 0;
    public final static int FIELD_ID_EMPLOYEE = 1;

    public EmployeeSystemNotificationReadable(long id) {
        super(id);
    }

    public Long getIdEmployee() {
        return getLong(FIELD_ID_EMPLOYEE);
    }

    public Integer getMessageHash() {
        return getInteger(FIELD_MESSAGE_HASH);
    }
}
