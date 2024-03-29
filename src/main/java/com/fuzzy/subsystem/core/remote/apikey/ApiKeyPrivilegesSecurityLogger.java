package com.fuzzy.subsystem.core.remote.apikey;

import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.ReadableResource;
import com.infomaximum.platform.querypool.ResourceProvider;
import com.infomaximum.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystem.core.apikeyprivileges.ApiKeyPrivilegesGetter;
import com.fuzzy.subsystem.core.domainobject.apikey.ApiKeyReadable;
import com.fuzzy.subsystem.core.securitylog.CoreEvent;
import com.fuzzy.subsystem.core.securitylog.CoreTarget;
import com.fuzzy.subsystem.core.utils.PrivilegesChanges;
import com.fuzzy.subsystems.access.AccessOperation;
import com.fuzzy.subsystems.access.AccessOperationCollection;
import com.fuzzy.subsystems.security.SecurityLog;
import com.fuzzy.subsystems.security.struct.data.event.GeneralEvent;
import com.fuzzy.subsystems.security.struct.data.event.SyslogStructDataEvent;
import com.fuzzy.subsystems.security.struct.data.target.SyslogStructDataTarget;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ApiKeyPrivilegesSecurityLogger {

    private final ReadableResource<ApiKeyReadable> apiKeyReadable;
    private final ApiKeyPrivilegesGetter apiKeyPrivilegesGetter;
    private Map<String, AccessOperationCollection> prevPrivileges;
    private Long apiKeyId;

    public ApiKeyPrivilegesSecurityLogger(ResourceProvider resources) {
        apiKeyPrivilegesGetter = new ApiKeyPrivilegesGetter(resources);
        apiKeyReadable = resources.getReadableResource(ApiKeyReadable.class);
    }

    public void saveStateBeforeModifications(long apiKeyId, ContextTransaction<?> context) throws PlatformException {
        this.apiKeyId = apiKeyId;
        prevPrivileges = apiKeyPrivilegesGetter.getPrivileges(apiKeyId, context);
    }

    public void writeToLog(ContextTransaction<?> context) throws PlatformException {
        ApiKeyReadable apiKey = this.apiKeyReadable.get(apiKeyId, context.getTransaction());
        Map<String, AccessOperationCollection> currentPrivileges = apiKeyPrivilegesGetter.getPrivileges(apiKeyId, context);

        PrivilegesChanges.mergeChangesPrivileges(
                prevPrivileges,
                currentPrivileges,
                (key, prevOperationCollection, curOperationCollection) -> writeToLog(apiKey, key, prevOperationCollection.getOperations(), curOperationCollection.getOperations(), context)
        );
    }

    private void writeToLog(ApiKeyReadable apiKey, String key, AccessOperation[] prevAccessOperations, AccessOperation[] curAccessOperations, ContextTransaction<?> context) {
        String oldOperations = Stream.of(prevAccessOperations).map(AccessOperation::name).collect(Collectors.joining(","));
        String newOperations = Stream.of(curAccessOperations).map(AccessOperation::name).collect(Collectors.joining(","));

        SecurityLog.info(
                new SyslogStructDataEvent(CoreEvent.TYPE_CHANGE_PRIVILEGE)
                        .withParam(CoreEvent.PARAM_PRIVILEGE, key)
                        .withParam(CoreEvent.PARAM_OLD_OPERATIONS, oldOperations)
                        .withParam(CoreEvent.PARAM_NEW_OPERATIONS, newOperations),
                new SyslogStructDataTarget(CoreTarget.TYPE_API_KEY, apiKey.getId())
                        .withParam(GeneralEvent.PARAM_NAME, apiKey.getName()),
                context
        );
    }
}
