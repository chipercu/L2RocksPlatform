package com.fuzzy.subsystem.core.logoninfo;

import com.fuzzy.database.domainobject.filter.HashFilter;
import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.QueryTransaction;
import com.fuzzy.platform.querypool.ReadableResource;
import com.fuzzy.platform.querypool.ResourceProvider;
import com.fuzzy.subsystem.core.domainobject.logoninfo.LogonInfoReadable;

import java.time.Instant;

public class LogonInfoGetter {

    private final ReadableResource<LogonInfoReadable> logonInfoReadableResource;

    public LogonInfoGetter(ResourceProvider resources) {
        logonInfoReadableResource = resources.getReadableResource(LogonInfoReadable.class);
    }

    public Instant getLastLogonTime(long employeeId, QueryTransaction transaction) throws PlatformException {
        LogonInfoReadable logonInfo = get(employeeId, transaction);
        return logonInfo != null ? logonInfo.getLastLogonTime() : null;
    }

    private LogonInfoReadable get(long employeeId, QueryTransaction transaction) throws PlatformException {
        HashFilter filter = new HashFilter(LogonInfoReadable.FIELD_EMPLOYEE_ID, employeeId);
        return logonInfoReadableResource.find(filter, transaction);
    }
}
