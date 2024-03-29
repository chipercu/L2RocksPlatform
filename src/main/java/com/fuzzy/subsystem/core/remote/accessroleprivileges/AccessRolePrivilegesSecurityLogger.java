package com.fuzzy.subsystem.core.remote.accessroleprivileges;

import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.ReadableResource;
import com.fuzzy.main.platform.querypool.ResourceProvider;
import com.fuzzy.main.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystem.core.accessroleprivileges.AccessRolePrivilegesGetter;
import com.fuzzy.subsystem.core.domainobject.accessrole.AccessRoleReadable;
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

public class AccessRolePrivilegesSecurityLogger {

    private final ReadableResource<AccessRoleReadable> accessRoleReadable;
    private final AccessRolePrivilegesGetter accessRolePrivilegesGetter;
    private Map<String, AccessOperationCollection> prevPrivileges;
    private Long accessRoleId;

    public AccessRolePrivilegesSecurityLogger(ResourceProvider resources) {
        accessRolePrivilegesGetter = new AccessRolePrivilegesGetter(resources);
        accessRoleReadable = resources.getReadableResource(AccessRoleReadable.class);
    }

    public void saveStateBeforeModifications(long accessRoleId, ContextTransaction context) throws PlatformException {
        this.accessRoleId = accessRoleId;
        prevPrivileges = accessRolePrivilegesGetter.getPrivileges(accessRoleId, context);
    }

    public void writeToLog(ContextTransaction context) throws PlatformException {
        AccessRoleReadable accessRole = this.accessRoleReadable.get(accessRoleId, context.getTransaction());
        Map<String, AccessOperationCollection> currentPrivileges =
                accessRolePrivilegesGetter.getPrivileges(accessRoleId, context);
        PrivilegesChanges.mergeChangesPrivileges(
                prevPrivileges,
                currentPrivileges,
                (key, prevOperationCollection, curOperationCollection) -> writeToLog(accessRole, key, prevOperationCollection.getOperations(), curOperationCollection.getOperations(), context)
        );
    }

    private void writeToLog(AccessRoleReadable accessRole, String key, AccessOperation[] prevAccessOperations,  AccessOperation[] curAccessOperations, ContextTransaction context) {
        String oldOperations = Stream.of(prevAccessOperations).map(AccessOperation::name).collect(Collectors.joining(","));
        String newOperations = Stream.of(curAccessOperations).map(AccessOperation::name).collect(Collectors.joining(","));

        SecurityLog.info(
                new SyslogStructDataEvent(CoreEvent.TYPE_CHANGE_PRIVILEGE)
                        .withParam(CoreEvent.PARAM_PRIVILEGE, key)
                        .withParam(CoreEvent.PARAM_OLD_OPERATIONS, oldOperations)
                        .withParam(CoreEvent.PARAM_NEW_OPERATIONS, newOperations),
                new SyslogStructDataTarget(CoreTarget.TYPE_ACCESS_ROLE, accessRole.getId())
                        .withParam(GeneralEvent.PARAM_NAME, accessRole.getName()),
                context
        );
    }
}
