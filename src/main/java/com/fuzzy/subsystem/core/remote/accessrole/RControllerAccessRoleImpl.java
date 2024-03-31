package com.fuzzy.subsystem.core.remote.accessrole;

import com.fuzzy.database.domainobject.filter.EmptyFilter;
import com.fuzzy.database.domainobject.filter.HashFilter;
import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.*;
import com.fuzzy.platform.querypool.iterator.IteratorEntity;
import com.fuzzy.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystem.core.CoreSubsystem;
import com.fuzzy.subsystem.core.CoreSubsystemConsts;
import com.fuzzy.subsystem.core.domainobject.accessrole.AccessRoleEditable;
import com.fuzzy.subsystem.core.domainobject.accessrole.AccessRoleReadable;
import com.fuzzy.subsystem.core.domainobject.employee.EmployeeReadable;
import com.fuzzy.subsystem.core.domainobject.employeeaccessrole.EmployeeAccessRoleEditable;
import com.fuzzy.subsystem.core.employeeaccess.ManagerEmployeeAccessGetter;
import com.fuzzy.subsystem.core.exception.CoreExceptionBuilder;
import com.fuzzy.subsystem.core.license.enums.BusinessRoleLimit;
import com.fuzzy.subsystem.core.license.enums.LicenseParameter;
import com.fuzzy.subsystem.core.remote.domainobjectnotifications.RCEmployeeAccessRoleNotifications;
import com.fuzzy.subsystem.core.remote.licensebusinessrolechecker.RCLicenseBusinessRoleChecker;
import com.fuzzy.subsystem.core.remote.licensedemployee.RCLicensedEmployeeGetter;
import com.fuzzy.subsystem.core.remote.liscense.RCLicenseGetter;
import com.fuzzy.subsystem.core.securitylog.CoreEvent;
import com.fuzzy.subsystem.core.securitylog.CoreTarget;
import com.fuzzy.subsystem.core.securitylog.EmployeeAccessRoleSecurityLogger;
import com.fuzzy.subsystem.core.subscription.employee.GEmployeeUpdateEvent;
import com.fuzzy.subsystem.core.utils.LastAdministratorValidator;
import com.fuzzy.subsystems.exception.GeneralExceptionBuilder;
import com.fuzzy.subsystems.remote.RCExecutor;
import com.fuzzy.subsystems.remote.RemovalData;
import com.fuzzy.subsystems.resourceswithnotifications.RemovableResourceWithNotifications;
import com.fuzzy.subsystems.security.SecurityLog;
import com.fuzzy.subsystems.security.struct.data.event.GeneralEvent;
import com.fuzzy.subsystems.security.struct.data.event.SyslogStructDataEvent;
import com.fuzzy.subsystems.security.struct.data.target.SyslogStructDataTarget;
import com.fuzzy.subsystems.utils.DomainObjectValidator;
import com.fuzzy.subsystems.utils.PrimaryKeyValidator;

import java.util.Collections;
import java.util.HashSet;

public class RControllerAccessRoleImpl extends AbstractQueryRController<CoreSubsystem> implements RControllerAccessRole {

    private final CoreSubsystem component;
    private final ReadableResource<EmployeeReadable> employeeReadableResource;
    private final RemovableResource<AccessRoleEditable> accessRoleRemovableResource;
    private final RemovableResourceWithNotifications<EmployeeAccessRoleEditable, RCEmployeeAccessRoleNotifications> employeeAccessRoleRemovableResource;
    private final RCExecutor<RControllerAccessRoleNotification> rControllerAccessRoleNotificationExecutor;
    private final LastAdministratorValidator lastAdministratorValidator;
    private final ManagerEmployeeAccessGetter managerEmployeeAccessGetter;
    private final EmployeeAccessRoleSecurityLogger employeeAccessRoleSecurityLogger;
    private final RCLicenseBusinessRoleChecker rcLicenseBusinessRoleChecker;
    private final RCLicensedEmployeeGetter licensedEmployeeGetter;
    private final RCLicenseGetter rcLicenseGetter;

    public RControllerAccessRoleImpl(CoreSubsystem component, ResourceProvider resources) {
        super(component, resources);
        this.component = component;
        employeeReadableResource = resources.getReadableResource(EmployeeReadable.class);
        accessRoleRemovableResource = resources.getRemovableResource(AccessRoleEditable.class);
        employeeAccessRoleRemovableResource = new RemovableResourceWithNotifications<>(
                resources, EmployeeAccessRoleEditable.class, RCEmployeeAccessRoleNotifications.class);
        rControllerAccessRoleNotificationExecutor = new RCExecutor<>(resources, RControllerAccessRoleNotification.class);
        lastAdministratorValidator = new LastAdministratorValidator(component, resources);
        managerEmployeeAccessGetter = new ManagerEmployeeAccessGetter(resources);
        employeeAccessRoleSecurityLogger = new EmployeeAccessRoleSecurityLogger(resources);
        licensedEmployeeGetter = resources.getQueryRemoteController(CoreSubsystem.class, RCLicensedEmployeeGetter.class);
        rcLicenseBusinessRoleChecker = resources.getQueryRemoteController(CoreSubsystem.class, RCLicenseBusinessRoleChecker.class);
        rcLicenseGetter = component.getRemotes().get(CoreSubsystem.class, RCLicenseGetter.class);
    }

    @Override
    public AccessRoleReadable create(AccessRoleBuilder builder, ContextTransaction context) throws PlatformException {
        QueryTransaction transaction = context.getTransaction();
        if (!builder.isContainName()) {
            throw GeneralExceptionBuilder.buildEmptyValueException(
                    AccessRoleReadable.class, AccessRoleReadable.FIELD_NAME);
        }
        AccessRoleEditable accessRole = accessRoleRemovableResource.create(transaction);
        setFieldsFor(accessRole, builder, transaction);
        accessRoleRemovableResource.save(accessRole, transaction);

        SecurityLog.info(
                new SyslogStructDataEvent(GeneralEvent.TYPE_CREATE)
                        .withParam(GeneralEvent.PARAM_NAME, builder.getName()),
                new SyslogStructDataTarget(CoreTarget.TYPE_ACCESS_ROLE, accessRole.getId())
                        .withParam(GeneralEvent.PARAM_NAME, accessRole.getName()),
                context
        );
        return accessRole;
    }

    @Override
    public AccessRoleReadable update(long accessRoleId, AccessRoleBuilder builder, ContextTransaction context)
            throws PlatformException {
        QueryTransaction transaction = context.getTransaction();
        AccessRoleEditable accessRole = new PrimaryKeyValidator(false)
                .validateAndGet(accessRoleId, accessRoleRemovableResource, transaction);
        String prevName = accessRole.getName();
        setFieldsFor(accessRole, builder, transaction);
        accessRoleRemovableResource.save(accessRole, transaction);
        if (builder.isContainName() && !prevName.equals(builder.getName())) {
            SecurityLog.info(
                    new SyslogStructDataEvent(GeneralEvent.TYPE_UPDATE)
                            .withParam(GeneralEvent.PARAM_OLD_NAME, prevName)
                            .withParam(GeneralEvent.PARAM_NEW_NAME, builder.getName()),
                    new SyslogStructDataTarget(CoreTarget.TYPE_ACCESS_ROLE, accessRole.getId())
                            .withParam(GeneralEvent.PARAM_NAME, prevName),
                    context
            );
        }
        return accessRole;
    }

    @Override
    public RemovalData removeWithCauses(HashSet<Long> accessRoleIds, ContextTransaction context) throws PlatformException {
        QueryTransaction transaction = context.getTransaction();
        RemovalData removalData = new RemovalData();
        HashSet<Long> notFound = new HashSet<>();
        HashSet<Long> assignedToEmployees = new HashSet<>();
        for (Long accessRoleId : accessRoleIds) {
            if (accessRoleId == null) {
                continue;
            }
            AccessRoleEditable accessRole = accessRoleRemovableResource.get(accessRoleId, transaction);
            if (accessRole == null) {
                notFound.add(accessRoleId);
                continue;
            }
            HashFilter filter = new HashFilter(EmployeeAccessRoleEditable.FIELD_ACCESS_ROLE_ID, accessRoleId);
            if (employeeAccessRoleRemovableResource.find(filter, transaction) != null) {
                assignedToEmployees.add(accessRoleId);
                continue;
            }
            rControllerAccessRoleNotificationExecutor.exec(rControllerAccessRoleNotification ->
                    rControllerAccessRoleNotification.onBeforeRemoveAccessRole(accessRoleId, context));
            accessRoleRemovableResource.remove(accessRole, transaction);
            rControllerAccessRoleNotificationExecutor.exec(rControllerAccessRoleNotification ->
                    rControllerAccessRoleNotification.onAfterRemoveAccessRole(accessRoleId, context));
            removalData.getRemoved().add(accessRoleId);

            SecurityLog.info(
                    new SyslogStructDataEvent(GeneralEvent.TYPE_REMOVE),
                    new SyslogStructDataTarget(CoreTarget.TYPE_ACCESS_ROLE, accessRole.getId())
                            .withParam(GeneralEvent.PARAM_NAME, accessRole.getName()),
                    context
            );
        }
        removalData.addNonRemoved(GeneralExceptionBuilder.NOT_FOUND_DOMAIN_OBJECT_CODE, notFound);
        removalData.addNonRemoved(CoreExceptionBuilder.ASSIGNED_TO_EMPLOYEES_ACCESS_ROLE_CODE, assignedToEmployees);
        return removalData;
    }

    @Override
    public void assignAccessRoleToEmployee(long accessRoleId, long employeeId, ContextTransaction context)
            throws PlatformException {
        QueryTransaction transaction = context.getTransaction();
        PrimaryKeyValidator primaryKeyValidator = new PrimaryKeyValidator(false);
        primaryKeyValidator.validate(employeeId, employeeReadableResource, transaction);
        AccessRoleReadable accessRole =
                primaryKeyValidator.validateAndGet(accessRoleId, accessRoleRemovableResource, transaction);
        BusinessRoleLimit oldEmployeeBusinessRole = rcLicenseBusinessRoleChecker.getEmployeeBusinessRole(employeeId, context);
        BusinessRoleLimit assigningBusinessRole = rcLicenseBusinessRoleChecker.getAccessRoleBusinessRole(accessRoleId, context);
        if (accessRole.isAdmin() && !managerEmployeeAccessGetter.getAccess(employeeId, transaction).isAll()) {
            throw CoreExceptionBuilder.buildRequireAllEmployeeAccessException();
        }
        long usersLimit = rcLicenseGetter.getModuleParameterLimit(CoreSubsystemConsts.UUID, LicenseParameter.USERS_WITH_ROLE);
        if(usersLimit!=-1) {
            try(IteratorEntity<EmployeeAccessRoleEditable> ie = employeeAccessRoleRemovableResource.findAll(EmptyFilter.INSTANCE, context.getTransaction())) {
                HashSet<Long> employees = new HashSet<>();
                while (ie.hasNext()) {
                    EmployeeAccessRoleEditable employeeAccessRoleEditable = ie.next();
                    employees.add(employeeAccessRoleEditable.getEmployeeId());
                }
                employees.add(employeeId);
                rcLicenseGetter.checkLicenseParameterRestrictions(CoreSubsystemConsts.UUID, LicenseParameter.USERS_WITH_ROLE, employees.size());
            }
        }
        HashFilter filter = new HashFilter(EmployeeAccessRoleEditable.FIELD_ACCESS_ROLE_ID, accessRoleId)
                .appendField(EmployeeAccessRoleEditable.FIELD_EMPLOYEE_ID, employeeId);
        EmployeeAccessRoleEditable employeeAccessRole = employeeAccessRoleRemovableResource.find(filter, transaction);
        if (employeeAccessRole == null) {
            employeeAccessRole = employeeAccessRoleRemovableResource.create(transaction);
            employeeAccessRole.setAccessRoleId(accessRoleId);
            employeeAccessRole.setEmployeeId(employeeId);
            employeeAccessRoleRemovableResource.saveCreation(employeeAccessRole, context);
            if (assigningBusinessRole != null) {
                if (!assigningBusinessRole.equals(oldEmployeeBusinessRole)) {
                    licensedEmployeeGetter.validateLicensedEmployeesCount(assigningBusinessRole, context);
                }
            }
            employeeAccessRoleSecurityLogger.logEmployeeAccessRole(
                    CoreEvent.Employee.TYPE_ADDING_ACCESS_ROLE, employeeId, accessRoleId, context);
            GEmployeeUpdateEvent.send(component, employeeId, transaction);
        }
    }

    @Override
    public void eraseAccessRoleForEmployee(long accessRoleId, long employeeId, ContextTransaction context)
            throws PlatformException {
        QueryTransaction transaction = context.getTransaction();
        PrimaryKeyValidator primaryKeyValidator = new PrimaryKeyValidator(false);
        primaryKeyValidator.validate(accessRoleId, accessRoleRemovableResource, transaction);
        primaryKeyValidator.validate(employeeId, employeeReadableResource, transaction);
        HashFilter filter = new HashFilter(EmployeeAccessRoleEditable.FIELD_ACCESS_ROLE_ID, accessRoleId)
                .appendField(EmployeeAccessRoleEditable.FIELD_EMPLOYEE_ID, employeeId);
        EmployeeAccessRoleEditable employeeAccessRole = employeeAccessRoleRemovableResource.find(filter, transaction);
        if (employeeAccessRole != null) {
            employeeAccessRoleRemovableResource.remove(employeeAccessRole, context);
            employeeAccessRoleSecurityLogger.logEmployeeAccessRole(
                    CoreEvent.Employee.TYPE_REMOVING_ACCESS_ROLE, employeeId, accessRoleId, context);
            GEmployeeUpdateEvent.send(component, employeeId, transaction);
            rControllerAccessRoleNotificationExecutor.exec(rControllerAccessRoleNotification ->
                    rControllerAccessRoleNotification.onAfterEraseAccessRoleForEmployee(accessRoleId, employeeId, context));
            lastAdministratorValidator.validate(context);
        }
    }

    @Override
    public void replaceAccessRolesAtEmployee(HashSet<Long> newAccessRoleIds,
                                             long employeeId,
                                             ContextTransaction context) throws PlatformException {
        QueryTransaction transaction = context.getTransaction();
        HashSet<Long> accessRoleIds = new HashSet<>(newAccessRoleIds != null ? newAccessRoleIds : Collections.emptySet());
        HashFilter filter = new HashFilter(EmployeeAccessRoleEditable.FIELD_EMPLOYEE_ID, employeeId);
        employeeAccessRoleRemovableResource.forEach(filter, employeeAccessRole -> {
            if (!accessRoleIds.remove(employeeAccessRole.getAccessRoleId())) {
                employeeAccessRoleRemovableResource.remove(employeeAccessRole, context);
                employeeAccessRoleSecurityLogger.logEmployeeAccessRole(
                        CoreEvent.Employee.TYPE_REMOVING_ACCESS_ROLE, employeeId, employeeAccessRole.getAccessRoleId(), context);
                GEmployeeUpdateEvent.send(component, employeeId, transaction);
            }
        }, transaction);
        for (Long accessRoleId : accessRoleIds) {
            assignAccessRoleToEmployee(accessRoleId, employeeId, context);
        }
        rControllerAccessRoleNotificationExecutor.exec(rControllerAccessRoleNotification ->
                rControllerAccessRoleNotification.onAfterReplaceAccessRolesAtEmployee(accessRoleIds, employeeId, context));
        lastAdministratorValidator.validate(context);
    }

    private void setFieldsFor(
            AccessRoleEditable targetAccessRole,
            AccessRoleBuilder builder,
            QueryTransaction transaction
    ) throws PlatformException {
        if (builder.isContainName()) {
            DomainObjectValidator.validateNonEmptyAndUnique(
                    AccessRoleReadable.FIELD_NAME,
                    builder.getName(),
                    targetAccessRole.getId(),
                    accessRoleRemovableResource,
                    transaction
            );
            targetAccessRole.setName(builder.getName());
        }
        if (builder.isContainAdmin()) {
            targetAccessRole.setAdmin(builder.isAdmin());
        }
    }
}
