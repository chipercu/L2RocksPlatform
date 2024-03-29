package com.fuzzy.subsystem.core.emailmessages.systemevents;

import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.QueryTransaction;
import com.fuzzy.subsystem.core.domainobject.employee.EmployeeReadable;

public interface SystemEventsMessageSender {

    void sendAsync(EmployeeReadable employee, SystemEventsMessage message, QueryTransaction transaction) throws PlatformException;
}