package com.fuzzy.subsystem.core.apikeyprivileges;

import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.ResourceProvider;
import com.infomaximum.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystem.core.remote.apikeyprivileges.RControllerApiKeyPrivilegesGetter;
import com.fuzzy.subsystems.access.AccessOperationCollection;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class ApiKeyPrivilegesGetter {

    private final Set<RControllerApiKeyPrivilegesGetter> rControllerApiKeyPrivilegesGetters;

    public ApiKeyPrivilegesGetter(ResourceProvider resources) {
        rControllerApiKeyPrivilegesGetters =
                resources.getQueryRemoteControllers(RControllerApiKeyPrivilegesGetter.class);
    }

    public @NonNull HashMap<String, AccessOperationCollection> getPrivileges(long apiKeyId, ContextTransaction<?> context)
            throws PlatformException {
        HashMap<String, AccessOperationCollection> privileges = new HashMap<>();
        for (RControllerApiKeyPrivilegesGetter rControllerApiKeyPrivilegesGetter : rControllerApiKeyPrivilegesGetters) {
            privileges.putAll(rControllerApiKeyPrivilegesGetter.getPrivileges(apiKeyId, context));
        }
        return privileges;
    }

    public @NonNull HashSet<String> getPrivilegeCollection() throws PlatformException {
        HashSet<String> privileges = new HashSet<>();
        for (RControllerApiKeyPrivilegesGetter rControllerApiKeyPrivilegesGetter : rControllerApiKeyPrivilegesGetters) {
            privileges.addAll(rControllerApiKeyPrivilegesGetter.getPrivilegeCollection());
        }
        return privileges;
    }
}
