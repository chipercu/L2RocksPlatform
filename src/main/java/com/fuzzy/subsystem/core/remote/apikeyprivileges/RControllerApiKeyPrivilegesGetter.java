package com.fuzzy.subsystem.core.remote.apikeyprivileges;

import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.QueryRemoteController;
import com.fuzzy.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystems.access.AccessOperationCollection;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashMap;
import java.util.HashSet;

public interface RControllerApiKeyPrivilegesGetter extends QueryRemoteController {

    @NonNull HashMap<String, AccessOperationCollection> getPrivileges(long apiKeyId, ContextTransaction context)
            throws PlatformException;

    @NonNull HashSet<String> getPrivilegeCollection() throws PlatformException;
}
