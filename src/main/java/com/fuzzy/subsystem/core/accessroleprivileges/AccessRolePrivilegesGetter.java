package com.fuzzy.subsystem.core.accessroleprivileges;

import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.ResourceProvider;
import com.fuzzy.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystem.core.remote.accessroleprivileges.RControllerAccessRolePrivilegesGetter;
import com.fuzzy.subsystems.access.AccessOperationCollection;
import com.fuzzy.subsystems.remote.RCExecutor;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashMap;
import java.util.HashSet;

public class AccessRolePrivilegesGetter {

    private final RCExecutor<RControllerAccessRolePrivilegesGetter> rcAccessRolePrivilegesGetter;

    public AccessRolePrivilegesGetter(ResourceProvider resources) {
        rcAccessRolePrivilegesGetter = new RCExecutor<>(resources, RControllerAccessRolePrivilegesGetter.class);
    }

    public @NonNull HashMap<String, AccessOperationCollection> getPrivileges(long accessRoleId, ContextTransaction<?> context)
            throws PlatformException {
        HashMap<String, AccessOperationCollection> privileges = new HashMap<>();
        rcAccessRolePrivilegesGetter.exec(privilegesGetter ->
                privileges.putAll(privilegesGetter.getPrivileges(accessRoleId, context)));
        return privileges;
    }

    public @NonNull HashSet<String> getPrivilegeCollection(ContextTransaction<?> context) throws PlatformException {
        HashSet<String> privileges = new HashSet<>();
        rcAccessRolePrivilegesGetter.exec(privilegesGetter ->
                privileges.addAll(privilegesGetter.getPrivilegeCollection(context)));
        return privileges;
    }
}
