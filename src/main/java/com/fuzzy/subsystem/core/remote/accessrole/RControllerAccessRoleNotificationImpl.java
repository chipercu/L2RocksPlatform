package com.fuzzy.subsystem.core.remote.accessrole;

import com.infomaximum.database.domainobject.filter.HashFilter;
import com.fuzzy.main.Subsystems;
import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.AbstractQueryRController;
import com.infomaximum.platform.querypool.ReadableResource;
import com.infomaximum.platform.querypool.RemovableResource;
import com.infomaximum.platform.querypool.ResourceProvider;
import com.infomaximum.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystem.core.CoreSubsystem;
import com.fuzzy.subsystem.core.domainobject.accessroleprivilege.AccessRoleCorePrivilegeEditable;
import com.fuzzy.subsystem.core.domainobject.employeeaccessrole.EmployeeAccessRoleReadable;
import com.fuzzy.subsystem.core.servicemodesessionremover.ServiceModeSessionRemover;
import com.fuzzy.subsystem.frontend.FrontendSubsystem;

import java.util.HashSet;

public class RControllerAccessRoleNotificationImpl extends AbstractQueryRController<CoreSubsystem>
        implements RControllerAccessRoleNotification {

    private final ReadableResource<EmployeeAccessRoleReadable> employeeAccessRoleReadableResource;
    private RemovableResource<AccessRoleCorePrivilegeEditable> accessRoleCorePrivilegeRemovableResource;
    private final ServiceModeSessionRemover serviceModeSessionRemover;

    public RControllerAccessRoleNotificationImpl(CoreSubsystem component, ResourceProvider resources) {
        super(component, resources);
        employeeAccessRoleReadableResource = resources.getReadableResource(EmployeeAccessRoleReadable.class);
        accessRoleCorePrivilegeRemovableResource =
                resources.getRemovableResource(AccessRoleCorePrivilegeEditable.class);
        serviceModeSessionRemover = new ServiceModeSessionRemover(resources);
    }

    @Override
    public void onBeforeRemoveAccessRole(Long accessRoleId, ContextTransaction context) throws PlatformException {
        accessRoleCorePrivilegeRemovableResource.removeAll(new HashFilter(
                AccessRoleCorePrivilegeEditable.FIELD_ACCESS_ROLE_ID,
                accessRoleId
        ), context.getTransaction());
    }

    @Override
    public void onAfterRemoveAccessRole(Long accessRoleId, ContextTransaction context) throws PlatformException {
        FrontendSubsystem frontendSubsystem = getFrontendSubsystem();
        if (!frontendSubsystem.getConfig().isServiceMode()) {
            return;
        }
        HashFilter filter = new HashFilter(EmployeeAccessRoleReadable.FIELD_ACCESS_ROLE_ID, accessRoleId);
        employeeAccessRoleReadableResource.forEach(filter, employeeAccessRole ->
                        serviceModeSessionRemover.clearSessionForEmployeeIfNeed(
                                frontendSubsystem, employeeAccessRole.getEmployeeId(), context),
                context.getTransaction());
    }

    @Override
    public void onAfterEraseAccessRoleForEmployee(long accessRoleId, long employeeId, ContextTransaction context) throws PlatformException {
        clearSessionForEmployeeIfNeedForServiceMode(employeeId, context);
    }

    @Override
    public void onAfterReplaceAccessRolesAtEmployee(HashSet<Long> newAccessRoleIds, long employeeId, ContextTransaction context) throws PlatformException {
        clearSessionForEmployeeIfNeedForServiceMode(employeeId, context);
    }

    private FrontendSubsystem getFrontendSubsystem() {
        return Subsystems.getInstance().getCluster().getAnyLocalComponent(FrontendSubsystem.class);
    }

    private void clearSessionForEmployeeIfNeedForServiceMode(long employeeId,
                                                             ContextTransaction<?> context) throws PlatformException {
        FrontendSubsystem frontendSubsystem = getFrontendSubsystem();
        if (frontendSubsystem.getConfig().isServiceMode()) {
            serviceModeSessionRemover.clearSessionForEmployeeIfNeed(frontendSubsystem, employeeId, context);
        }
    }
}
