package com.fuzzy.subsystem.core.remote.accessroleprivileges;

import com.infomaximum.database.domainobject.filter.HashFilter;
import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.AbstractQueryRController;
import com.infomaximum.platform.querypool.ReadableResource;
import com.infomaximum.platform.querypool.ResourceProvider;
import com.infomaximum.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystem.core.CoreSubsystem;
import com.fuzzy.subsystem.core.access.CorePrivilege;
import com.fuzzy.subsystem.core.domainobject.accessroleprivilege.AccessRoleCorePrivilegeReadable;
import com.fuzzy.subsystems.access.AccessOperationCollection;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.stream.Collectors;

public class RControllerAccessRolePrivilegesGetterImpl extends AbstractQueryRController<CoreSubsystem>
        implements RControllerAccessRolePrivilegesGetter {

    private ReadableResource<AccessRoleCorePrivilegeReadable> accessRolePrivilegeReadableResource;

    public RControllerAccessRolePrivilegesGetterImpl(CoreSubsystem component, ResourceProvider resources) {
        super(component, resources);
        accessRolePrivilegeReadableResource = resources.getReadableResource(AccessRoleCorePrivilegeReadable.class);
    }

    @Override
    public HashMap<String, AccessOperationCollection> getPrivileges(long accessRoleId, ContextTransaction context)
            throws PlatformException {
        HashMap<String, AccessOperationCollection> privileges = new HashMap<>();
        HashFilter filter = new HashFilter(AccessRoleCorePrivilegeReadable.FIELD_ACCESS_ROLE_ID, accessRoleId);
        return accessRolePrivilegeReadableResource.getAll(filter, context.getTransaction())
                .stream()
                .collect(Collectors.toMap(
                        o -> o.getPrivilege().getUniqueKey(),
                        AccessRoleCorePrivilegeReadable::getOperations,
                        (v1, v2) -> { v1.addOperation(v2.getOperations()); return v1; },
                        HashMap::new
                ));
    }

    @Override
    public HashSet<String> getPrivilegeCollection(ContextTransaction context) {
        return Arrays.stream(CorePrivilege.values())
                .map(CorePrivilege::getUniqueKey)
                .collect(Collectors.toCollection(HashSet::new));
    }
}
