package com.fuzzy.subsystem.frontend.remote.accessroleprivileges;

import com.infomaximum.database.domainobject.filter.HashFilter;
import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.AbstractQueryRController;
import com.infomaximum.platform.querypool.ReadableResource;
import com.infomaximum.platform.querypool.ResourceProvider;
import com.infomaximum.platform.querypool.iterator.IteratorEntity;
import com.infomaximum.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystem.core.remote.accessroleprivileges.RControllerAccessRolePrivilegesGetter;
import com.fuzzy.subsystem.frontend.FrontendSubsystem;
import com.fuzzy.subsystem.frontend.access.FrontendPrivilege;
import com.fuzzy.subsystem.frontend.domainobject.accessroleprivilege.AccessRoleFrontendPrivilegeReadable;
import com.fuzzy.subsystems.access.AccessOperationCollection;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashMap;
import java.util.HashSet;

public class RControllerAccessRolePrivilegesGetterImpl extends AbstractQueryRController<FrontendSubsystem> implements RControllerAccessRolePrivilegesGetter {

    private final ReadableResource<AccessRoleFrontendPrivilegeReadable> accessRoleFrontendPrivilegeReadableResource;

    public RControllerAccessRolePrivilegesGetterImpl(FrontendSubsystem component, ResourceProvider resources) {
        super(component, resources);
        accessRoleFrontendPrivilegeReadableResource = resources.getReadableResource(AccessRoleFrontendPrivilegeReadable.class);
    }

    @Override
    public @NonNull HashMap<String, AccessOperationCollection> getPrivileges(long accessRoleId, ContextTransaction context) throws PlatformException {
        HashMap<String, AccessOperationCollection> privileges = new HashMap<>();
        final HashFilter filter = new HashFilter(AccessRoleFrontendPrivilegeReadable.FIELD_ACCESS_ROLE_ID, accessRoleId);
        try (final IteratorEntity<AccessRoleFrontendPrivilegeReadable> ie = accessRoleFrontendPrivilegeReadableResource.findAll(filter, context.getTransaction())) {
            while (ie.hasNext()) {
                final AccessRoleFrontendPrivilegeReadable accessRoleFrontendPrivilege = ie.next();
                privileges.put(accessRoleFrontendPrivilege.getPrivilege().getUniqueKey(), accessRoleFrontendPrivilege.getOperations());
            }
        }
        return privileges;
    }

    @Override
    public @NonNull HashSet<String> getPrivilegeCollection(ContextTransaction context) throws PlatformException {
        HashSet<String> privileges = new HashSet<>();
        for (FrontendPrivilege privilege : FrontendPrivilege.values()) {
            privileges.add(privilege.getUniqueKey());
        }
        return privileges;
    }
}
