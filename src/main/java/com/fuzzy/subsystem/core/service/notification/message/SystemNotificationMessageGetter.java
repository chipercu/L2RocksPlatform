package com.fuzzy.subsystem.core.service.notification.message;

import com.infomaximum.database.domainobject.filter.HashFilter;
import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.QueryTransaction;
import com.infomaximum.platform.querypool.ReadableResource;
import com.infomaximum.platform.querypool.ResourceProvider;
import com.fuzzy.subsystem.core.CoreConfig;
import com.fuzzy.subsystem.core.CoreSubsystem;
import com.fuzzy.subsystem.core.domainobject.employeesystemnotification.EmployeeSystemNotificationReadable;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

import static com.fuzzy.subsystem.core.domainobject.employeesystemnotification.EmployeeSystemNotificationReadable.FIELD_ID_EMPLOYEE;
import static com.fuzzy.subsystem.core.domainobject.employeesystemnotification.EmployeeSystemNotificationReadable.FIELD_MESSAGE_HASH;

public class SystemNotificationMessageGetter {
    private final CoreSubsystem component;
    private final ReadableResource<EmployeeSystemNotificationReadable> employeeSystemNotificationResource;


    public SystemNotificationMessageGetter(CoreSubsystem component, ResourceProvider resourceProvider) {
        this.component = component;
        employeeSystemNotificationResource = resourceProvider.getReadableResource(EmployeeSystemNotificationReadable.class);
    }

    public String getMessage(Long employeeId, QueryTransaction transaction) throws PlatformException {
        CoreConfig.SystemNotification systemNotificationMessage = component.getConfig().getSystemNotificationMessage();
        if (StringUtils.isNotEmpty(systemNotificationMessage.getMessage())) {
            EmployeeSystemNotificationReadable notificationEditable = employeeSystemNotificationResource.find(
                    new HashFilter(FIELD_ID_EMPLOYEE, employeeId)
                            .appendField(FIELD_MESSAGE_HASH, systemNotificationMessage.getHash()),
                    transaction);
            if (Objects.nonNull(notificationEditable)) {
                return StringUtils.EMPTY;
            }
        }
        return systemNotificationMessage.getMessage();
    }
}