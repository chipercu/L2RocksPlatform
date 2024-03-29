package com.fuzzy.subsystem.core.authcontext.employee;

import com.fuzzy.subsystem.frontend.authcontext.AuthorizedContext;
import com.fuzzy.subsystems.access.AccessOperationCollection;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashMap;


/**
 * Created by kris on 08.02.17.
 */
public abstract class EmployeeAuthContext extends AuthorizedContext {

    private final long employeeId;

    public EmployeeAuthContext(@NonNull HashMap<String, AccessOperationCollection> privileges, long employeeId) {
        super(privileges);
        this.employeeId = employeeId;
    }

    public long getEmployeeId() {
        return employeeId;
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append("AuthContextUser(")
                .append("id: ").append(employeeId)
                .append(')').toString();
    }

}
