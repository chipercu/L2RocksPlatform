package com.fuzzy.subsystem.core.emailmessages.systemevents;

import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.QueryTransaction;
import com.fuzzy.subsystem.core.domainobject.employee.EmployeeReadable;

public interface SystemEventsMessageSender {

    void sendAsync(EmployeeReadable employee, SystemEventsMessage message, QueryTransaction transaction) throws PlatformException;
}