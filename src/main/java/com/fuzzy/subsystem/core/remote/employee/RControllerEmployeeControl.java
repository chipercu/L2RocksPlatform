package com.fuzzy.subsystem.core.remote.employee;

import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.QueryRemoteController;
import com.fuzzy.main.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystem.core.domainobject.employee.EmployeeReadable;
import com.fuzzy.subsystems.remote.RemovalData;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashSet;

public interface RControllerEmployeeControl extends QueryRemoteController {

    EmployeeReadable create(EmployeeBuilder employeeBuilder, ContextTransaction context) throws PlatformException;

    EmployeeReadable update(long employeeId, EmployeeBuilder employeeBuilder, ContextTransaction context)
            throws PlatformException;

    RemovalData removeWithCauses(HashSet<Long> employeeIds, ContextTransaction context) throws PlatformException;

    void merge(long mainEmployeeId, @NonNull HashSet<Long> secondaryEmployees, ContextTransaction context) throws PlatformException;

    default HashSet<Long> remove(HashSet<Long> employeeIds, ContextTransaction context) throws PlatformException {
        return removeWithCauses(employeeIds, context).getRemoved();
    }
}