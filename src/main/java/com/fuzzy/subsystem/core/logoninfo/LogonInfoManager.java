package com.fuzzy.subsystem.core.logoninfo;

import com.fuzzy.main.rdao.database.domainobject.filter.HashFilter;
import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.QueryTransaction;
import com.fuzzy.main.platform.querypool.RemovableResource;
import com.fuzzy.main.platform.querypool.ResourceProvider;
import com.fuzzy.main.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystem.core.domainobject.logoninfo.LogonInfoEditable;
import com.fuzzy.subsystem.core.domainobject.logoninfo.LogonInfoReadable;

import java.time.Instant;

public class LogonInfoManager {

    private final RemovableResource<LogonInfoEditable> logonInfoRemovableResource;

    public LogonInfoManager(ResourceProvider resources) {
        logonInfoRemovableResource = resources.getRemovableResource(LogonInfoEditable.class);
    }

    public void setLastLogonTime(long employeeId, Instant time, ContextTransaction<?> context) throws PlatformException {
        QueryTransaction transaction = context.getTransaction();
        HashFilter filter = new HashFilter(LogonInfoReadable.FIELD_EMPLOYEE_ID, employeeId);
        LogonInfoEditable logonInfo = logonInfoRemovableResource.find(filter, transaction);
        boolean newObject = logonInfo == null;
        if (newObject) {
            logonInfo = logonInfoRemovableResource.create(transaction);
            logonInfo.setEmployeeId(employeeId);
        }
        logonInfo.setLastLogonTime(time);
        logonInfoRemovableResource.save(logonInfo, transaction);
    }

    public void removeEmployee(long employeeId, QueryTransaction transaction) throws PlatformException {
        HashFilter filter = new HashFilter(LogonInfoReadable.FIELD_EMPLOYEE_ID, employeeId);
        logonInfoRemovableResource.removeAll(filter, transaction);
    }
}
