package com.fuzzy.subsystem.core.authcontext.system;

import com.fuzzy.subsystem.frontend.authcontext.AuthorizedContext;
import com.fuzzy.subsystems.access.AccessOperationCollection;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashMap;

public abstract class SystemAuthContext extends AuthorizedContext {

    public SystemAuthContext(@NonNull HashMap<String, AccessOperationCollection> privileges) {
        super(privileges);
    }

    public abstract String getUniqueId();
}
