package com.fuzzy.subsystem.core.authcontext.employee;

import com.fuzzy.subsystem.frontend.service.session.Session;
import com.fuzzy.subsystems.access.AccessOperationCollection;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashMap;
import java.util.Map;

public class EmployeeSessionAuthContext extends EmployeeAuthContext {

    private final String login;
    private final String sessionId;

    public EmployeeSessionAuthContext(
            @NonNull HashMap<String, AccessOperationCollection> privileges,
            long employeeId,
            String login,
            @NonNull String sessionId
    ) {
        super(privileges, employeeId);
        this.login = login;
        this.sessionId = sessionId;
        Map<String, String> params = new HashMap<>();
        params.put("id", String.valueOf(getEmployeeId()));
        params.put("login", login);
        params.put("session_hash", Session.getHash(sessionId));
        addParams(params);
    }

    public @NonNull String getSessionId() {
        return sessionId;
    }

    public @NonNull String getLogin() {
        return login;
    }
}
