package com.fuzzy.subsystem.core.service.notification.message;

import com.fuzzy.database.domainobject.filter.HashFilter;
import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.EditableResource;
import com.fuzzy.platform.querypool.QueryTransaction;
import com.fuzzy.platform.querypool.ResourceProvider;
import com.fuzzy.subsystem.core.CoreConfig;
import com.fuzzy.subsystem.core.CoreSubsystem;
import com.fuzzy.subsystem.core.domainobject.employeesystemnotification.EmployeeSystemNotificationEditable;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public class SystemNotificationMessageSetter {
    private final CoreSubsystem component;
    private final EditableResource<EmployeeSystemNotificationEditable> employeeSystemNotificationResource;


    public SystemNotificationMessageSetter(CoreSubsystem component, ResourceProvider resourceProvider) {
        this.component = component;
        employeeSystemNotificationResource = resourceProvider.getEditableResource(EmployeeSystemNotificationEditable.class);
    }

    public void blockMessage(Long employeeId, QueryTransaction transaction) throws PlatformException {
        CoreConfig.SystemNotification systemNotificationMessage = component.getConfig().getSystemNotificationMessage();
        EmployeeSystemNotificationEditable notificationEditable = employeeSystemNotificationResource.find(
                new HashFilter(EmployeeSystemNotificationEditable.FIELD_ID_EMPLOYEE, employeeId), transaction);

        if (StringUtils.isEmpty(systemNotificationMessage.getMessage())) {
            return;
        }
        if (Objects.isNull(notificationEditable)) {
            notificationEditable = employeeSystemNotificationResource.create(transaction);
            notificationEditable.setIdEmployee(employeeId);
            notificationEditable.setMessageHash(systemNotificationMessage.getHash());
            employeeSystemNotificationResource.save(notificationEditable, transaction);
        } else if (notificationEditable.getMessageHash().intValue() != systemNotificationMessage.getHash()) {
            notificationEditable.setMessageHash(systemNotificationMessage.getHash());
            employeeSystemNotificationResource.save(notificationEditable, transaction);
        }
    }
}