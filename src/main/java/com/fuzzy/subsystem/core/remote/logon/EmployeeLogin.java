package com.fuzzy.subsystem.core.remote.logon;

import com.fuzzy.main.cluster.core.remote.struct.RemoteObject;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class EmployeeLogin implements RemoteObject {

    public final Long employeeId;
    public final AuthStatus authStatus;
    public final String componentUuid;
    public final String authenticationType;

    public EmployeeLogin(@Nullable Long employeeId,
                         @NonNull AuthStatus authStatus,
                         @NonNull String componentUuid,
                         @NonNull String authenticationType) {
        this.employeeId = employeeId;
        this.authStatus = authStatus;
        this.componentUuid = componentUuid;
        this.authenticationType = authenticationType;
    }
}
