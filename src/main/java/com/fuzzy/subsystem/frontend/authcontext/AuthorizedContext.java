package com.fuzzy.subsystem.frontend.authcontext;

import com.infomaximum.platform.component.frontend.authcontext.UnauthorizedContext;
import com.fuzzy.subsystems.access.AccessOperationCollection;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashMap;
import java.util.Map;

public abstract class AuthorizedContext extends UnauthorizedContext {

    private final HashMap<String, AccessOperationCollection> privileges;
    private final HashMap<String, String> params;

    public AuthorizedContext(@NonNull HashMap<String, AccessOperationCollection> privileges) {
        this.privileges = privileges;
        this.params = new HashMap<>();
    }

    public @NonNull AccessOperationCollection getOperations(@NonNull String privilegeUniqueKey) {
        AccessOperationCollection operations = privileges.get(privilegeUniqueKey);
        return operations != null ? operations : AccessOperationCollection.EMPTY;
    }

    public Map<String, String> getParams() {
        return this.params;
    }

    public void addParams(Map<String, String> params) {
        if (params != null) {
            this.params.putAll(params);
        }
    }
}
