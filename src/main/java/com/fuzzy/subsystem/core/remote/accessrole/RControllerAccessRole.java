package com.fuzzy.subsystem.core.remote.accessrole;

import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.QueryRemoteController;
import com.fuzzy.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystem.core.domainobject.accessrole.AccessRoleReadable;
import com.fuzzy.subsystems.remote.RemovalData;

import java.util.HashSet;

public interface RControllerAccessRole extends QueryRemoteController {

    AccessRoleReadable create(AccessRoleBuilder builder, ContextTransaction context) throws PlatformException;

    AccessRoleReadable update(long accessRoleId, AccessRoleBuilder builder, ContextTransaction context) throws PlatformException;

    RemovalData removeWithCauses(HashSet<Long> accessRoleIds, ContextTransaction context) throws PlatformException;

    default HashSet<Long> remove(HashSet<Long> accessRoleIds, ContextTransaction context) throws PlatformException {
        return removeWithCauses(accessRoleIds, context).getRemoved();
    }

    void assignAccessRoleToEmployee(long accessRoleId, long employeeId, ContextTransaction context) throws PlatformException;

    void eraseAccessRoleForEmployee(long accessRoleId, long employeeId, ContextTransaction context) throws PlatformException;

    void replaceAccessRolesAtEmployee(HashSet<Long> newAccessRoleIds, long employeeId, ContextTransaction context) throws PlatformException;
}
