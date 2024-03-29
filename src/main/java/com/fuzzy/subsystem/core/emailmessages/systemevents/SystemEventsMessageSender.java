package com.fuzzy.subsystem.core.emailmessages.systemevents;

import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.QueryTransaction;
import com.fuzzy.subsystem.core.domainobject.employee.EmployeeReadable;

public interface SystemEventsMessageSender {

    void sendAsync(EmployeeReadable employee, SystemEventsMessage message, QueryTransaction transaction) throws PlatformException;
}