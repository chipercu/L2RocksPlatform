package com.fuzzy.subsystem.core.remote.employee;

import com.infomaximum.database.domainobject.DomainObject;
import com.infomaximum.database.domainobject.DomainObjectEditable;
import com.infomaximum.database.domainobject.filter.HashFilter;
import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.*;
import com.infomaximum.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystem.core.CoreSubsystem;
import com.fuzzy.subsystem.core.domainobject.additionalfield.AdditionalFieldReadable;
import com.fuzzy.subsystem.core.domainobject.additionalfieldvalue.AdditionalFieldValueEditable;
import com.fuzzy.subsystem.core.domainobject.additionalfieldvalue.AdditionalFieldValueReadable;
import com.fuzzy.subsystem.core.domainobject.employee.EmployeeReadable;
import com.fuzzy.subsystem.core.domainobject.employeeaccessrole.EmployeeAccessRoleEditable;
import com.fuzzy.subsystem.core.domainobject.employeeauthorizationhistory.EmployeeAuthorizationHistoryEditable;
import com.fuzzy.subsystem.core.domainobject.employeedata.EmployeeDataEditable;
import com.fuzzy.subsystem.core.domainobject.employeeinvitationtoken.EmployeeInvitationTokenEditable;
import com.fuzzy.subsystem.core.domainobject.employeephone.EmployeePhoneEditable;
import com.fuzzy.subsystem.core.domainobject.employeesystemnotification.EmployeeSystemNotificationEditable;
import com.fuzzy.subsystem.core.domainobject.employeesystemnotification.EmployeeSystemNotificationReadable;
import com.fuzzy.subsystem.core.domainobject.employeetokenrestoreaccess.EmployeeTokenRestoreAccessEditable;
import com.fuzzy.subsystem.core.domainobject.managerallaccess.ManagerAllAccessEditable;
import com.fuzzy.subsystem.core.domainobject.managerallaccess.ManagerAllAccessReadable;
import com.fuzzy.subsystem.core.domainobject.managerdepartmentaccess.ManagerDepartmentAccessEditable;
import com.fuzzy.subsystem.core.domainobject.manageremployeeaccess.ManagerEmployeeAccessEditable;
import com.fuzzy.subsystem.core.domainobject.usedpassword.UsedPasswordEditable;
import com.fuzzy.subsystem.core.enums.FieldDataType;
import com.fuzzy.subsystem.core.logoninfo.LogonInfoManager;
import com.fuzzy.subsystem.core.remote.employeeauthentication.RCEmployeeAuthentication;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class RControllerEmployeeNotificationImpl extends AbstractQueryRController<CoreSubsystem>
        implements RControllerEmployeeNotification {

    private final ReadableResource<AdditionalFieldReadable> additionalFieldReadableResource;
    private final RemovableResource<EmployeeTokenRestoreAccessEditable> employeeTokenRestoreAccessRemovableResource;
    private final RemovableResource<EmployeeInvitationTokenEditable> employeeInvitationTokenRemovableResource;
    private final RemovableResource<UsedPasswordEditable> usedPasswordRemovableResource;
    private final RemovableResource<EmployeeAuthorizationHistoryEditable> employeeAuthorizationHistoryRemovableResource;
    private final RemovableResource<EmployeeAccessRoleEditable> employeeAccessRoleRemovableResource;
    private final RemovableResource<ManagerAllAccessEditable> managerAllAccessRemovableResource;
    private final RemovableResource<ManagerDepartmentAccessEditable> managerDepartmentAccessRemovableResource;
    private final RemovableResource<ManagerEmployeeAccessEditable> managerEmployeeAccessRemovableResource;
    private final RemovableResource<EmployeeDataEditable> employeeDataRemovableResource;
    private final RemovableResource<EmployeePhoneEditable> employeePhoneRemovableResource;
    private final RemovableResource<AdditionalFieldValueEditable> additionalFieldValueRemovableResource;
    private final RemovableResource<EmployeeSystemNotificationEditable> employeeSystemNotificationRemovableResource;
    private final RCEmployeeAuthentication rcEmployeeAuthentication;
    private final LogonInfoManager logonInfoManager;

    private boolean isParentDepartmentUpdated = false;

    public RControllerEmployeeNotificationImpl(CoreSubsystem component, ResourceProvider resources) {
        super(component, resources);
        additionalFieldReadableResource = resources.getReadableResource(AdditionalFieldReadable.class);
        employeeTokenRestoreAccessRemovableResource =
                resources.getRemovableResource(EmployeeTokenRestoreAccessEditable.class);
        employeeInvitationTokenRemovableResource =
                resources.getRemovableResource(EmployeeInvitationTokenEditable.class);
        usedPasswordRemovableResource = resources.getRemovableResource(UsedPasswordEditable.class);
        employeeAuthorizationHistoryRemovableResource =
                resources.getRemovableResource(EmployeeAuthorizationHistoryEditable.class);
        employeeAccessRoleRemovableResource = resources.getRemovableResource(EmployeeAccessRoleEditable.class);
        managerAllAccessRemovableResource = resources.getRemovableResource(ManagerAllAccessEditable.class);
        managerDepartmentAccessRemovableResource = resources.getRemovableResource(ManagerDepartmentAccessEditable.class);
        managerEmployeeAccessRemovableResource = resources.getRemovableResource(ManagerEmployeeAccessEditable.class);
        employeeDataRemovableResource = resources.getRemovableResource(EmployeeDataEditable.class);
        employeePhoneRemovableResource = resources.getRemovableResource(EmployeePhoneEditable.class);
        additionalFieldValueRemovableResource = resources.getRemovableResource(AdditionalFieldValueEditable.class);
        rcEmployeeAuthentication =
                resources.getQueryRemoteController(CoreSubsystem.class, RCEmployeeAuthentication.class);
        employeeSystemNotificationRemovableResource =
                resources.getRemovableResource(EmployeeSystemNotificationEditable.class);
        logonInfoManager = new LogonInfoManager(resources);
    }

    @Override
    public void onBeforeRemoveEmployee(Long employeeId, ContextTransaction context) throws PlatformException {
        if (employeeId == null) {
            return;
        }
        QueryTransaction transaction = context.getTransaction();
        employeeTokenRestoreAccessRemovableResource.removeAll(new HashFilter(
                        EmployeeTokenRestoreAccessEditable.FIELD_EMPLOYEE_ID,
                        employeeId),
                transaction
        );
        employeeInvitationTokenRemovableResource.removeAll(new HashFilter(
                        EmployeeInvitationTokenEditable.FIELD_EMPLOYEE_ID,
                        employeeId),
                transaction
        );
        usedPasswordRemovableResource.removeAll(new HashFilter(
                        UsedPasswordEditable.FIELD_EMPLOYEE_ID,
                        employeeId),
                transaction
        );
        employeeAuthorizationHistoryRemovableResource.removeAll(new HashFilter(
                        EmployeeAuthorizationHistoryEditable.FIELD_EMPLOYEE_ID,
                        employeeId),
                transaction
        );
        employeeAccessRoleRemovableResource.removeAll(new HashFilter(
                        EmployeeAccessRoleEditable.FIELD_EMPLOYEE_ID,
                        employeeId),
                transaction);
        managerAllAccessRemovableResource.removeAll(new HashFilter(
                        ManagerAllAccessEditable.FIELD_MANAGER_ID,
                        employeeId),
                transaction
        );
        managerDepartmentAccessRemovableResource.removeAll(new HashFilter(
                        ManagerDepartmentAccessEditable.FIELD_MANAGER_ID,
                        employeeId),
                transaction
        );
        managerEmployeeAccessRemovableResource.removeAll(new HashFilter(
                        ManagerEmployeeAccessEditable.FIELD_MANAGER_ID,
                        employeeId),
                transaction
        );
        managerEmployeeAccessRemovableResource.removeAll(new HashFilter(
                        ManagerEmployeeAccessEditable.FIELD_EMPLOYEE_ID,
                        employeeId),
                transaction
        );
        employeeDataRemovableResource.removeAll(new HashFilter(
                        EmployeeDataEditable.FIELD_EMPLOYEE_ID,
                        employeeId),
                transaction
        );
        employeePhoneRemovableResource.removeAll(new HashFilter(
                        EmployeePhoneEditable.FIELD_EMPLOYEE_ID,
                        employeeId),
                transaction
        );
        employeeSystemNotificationRemovableResource.removeAll(new HashFilter(
                        EmployeeSystemNotificationReadable.FIELD_ID_EMPLOYEE,
                        employeeId),
                transaction
        );
        logonInfoManager.removeEmployee(employeeId, transaction);
        removeAdditionalFieldValues(List.of(employeeId), transaction);
        rcEmployeeAuthentication.clearAuthenticationsForEmployee(employeeId, context);
    }

    @Override
    public void onAfterCreateEmployee(Long employeeId, ContextTransaction context) throws PlatformException {

    }

    @Override
    public void onBeforeUpdateEmployee(EmployeeReadable employee, EmployeeBuilder changes, ContextTransaction context) {
        isParentDepartmentUpdated =
                changes.isContainDepartmentId() && !Objects.equals(employee.getDepartmentId(), changes.getDepartmentId());
    }

    @Override
    public void onAfterUpdateEmployee(EmployeeReadable employee, ContextTransaction context) throws PlatformException {

    }

    @Override
    public void onBeforeMergeEmployees(long mainEmployeeId, HashSet<Long> secondaryEmployees, ContextTransaction context)
            throws PlatformException {
        QueryTransaction transaction = context.getTransaction();
        removeEmployees(employeeTokenRestoreAccessRemovableResource, EmployeeTokenRestoreAccessEditable.FIELD_EMPLOYEE_ID,
                secondaryEmployees, transaction);
        removeEmployees(employeeInvitationTokenRemovableResource, EmployeeInvitationTokenEditable.FIELD_EMPLOYEE_ID,
                secondaryEmployees, transaction);
        removeEmployees(usedPasswordRemovableResource, UsedPasswordEditable.FIELD_EMPLOYEE_ID,
                secondaryEmployees, transaction);
        removeEmployees(employeeAuthorizationHistoryRemovableResource, EmployeeAuthorizationHistoryEditable.FIELD_EMPLOYEE_ID,
                secondaryEmployees, transaction);
        replaceEmployee(
                employeeAccessRoleRemovableResource,
                EmployeeAccessRoleEditable.FIELD_EMPLOYEE_ID,
                mainEmployeeId,
                secondaryEmployees,
                EmployeeAccessRoleEditable::getAccessRoleId,
                EmployeeAccessRoleEditable::setEmployeeId,
                transaction
        );
        removeEmployees(employeeDataRemovableResource, EmployeeDataEditable.FIELD_EMPLOYEE_ID,
                secondaryEmployees, transaction);
        removeEmployees(employeePhoneRemovableResource, EmployeePhoneEditable.FIELD_EMPLOYEE_ID,
                secondaryEmployees, transaction);
        removeEmployees(employeeSystemNotificationRemovableResource, EmployeeSystemNotificationReadable.FIELD_ID_EMPLOYEE,
                secondaryEmployees, transaction);
        mergeEmployeeAccess(mainEmployeeId, secondaryEmployees, transaction);
        for (Long secondaryEmployeeId : secondaryEmployees) {
            logonInfoManager.removeEmployee(secondaryEmployeeId, transaction);
            rcEmployeeAuthentication.clearAuthenticationsForEmployee(secondaryEmployeeId, context);
        }
        removeAdditionalFieldValues(secondaryEmployees, transaction);
    }

    private void removeAdditionalFieldValues(Collection<Long> employees, QueryTransaction transaction) throws PlatformException {
        for (Long employeeId : employees) {
            HashFilter filter = new HashFilter(AdditionalFieldReadable.FIELD_OBJECT_TYPE, EmployeeReadable.class.getName());
            additionalFieldReadableResource.forEach(filter, additionalField -> {
                {
                    HashFilter vFilter = new HashFilter(AdditionalFieldValueReadable.FIELD_ADDITIONAL_FIELD_ID, additionalField.getId())
                            .appendField(AdditionalFieldValueReadable.FIELD_OBJECT_ID, employeeId);
                    additionalFieldValueRemovableResource.removeAll(vFilter, transaction);
                }
                {
                    if (FieldDataType.ID == additionalField.getDataType() &&
                            Objects.equals(EmployeeReadable.class.getName(), additionalField.getListSource())) {

                        HashFilter vFilter = new HashFilter(AdditionalFieldValueReadable.FIELD_ADDITIONAL_FIELD_ID, additionalField.getId())
                                .appendField(AdditionalFieldValueReadable.FIELD_LONG_VALUE, employeeId)
                                .appendField(AdditionalFieldValueReadable.FIELD_INDEX, 0);
                        additionalFieldValueRemovableResource.removeAll(vFilter, transaction);
                    }
                }
            }, transaction);
        }
    }

    private <T extends DomainObject & DomainObjectEditable> void removeEmployees(RemovableResource<T> removableResource,
                                                                                 int fieldNumber,
                                                                                 HashSet<Long> employees,
                                                                                 QueryTransaction transaction) throws PlatformException {
        for (Long employeeId : employees) {
            removableResource.removeAll(new HashFilter(fieldNumber, employeeId), transaction);
        }
    }

    private <T extends DomainObject & DomainObjectEditable> void replaceEmployee(RemovableResource<T> editableResource,
                                                                                 int fieldNumber,
                                                                                 long mainEmployeeId,
                                                                                 HashSet<Long> secondaryEmployees,
                                                                                 Function<T, Long> valueGetter,
                                                                                 BiConsumer<T, Long> employeeSetter,
                                                                                 QueryTransaction transaction) throws PlatformException {
        Set<Long> values = new HashSet<>();
        HashFilter filter = new HashFilter(fieldNumber, mainEmployeeId);
        editableResource.forEach(filter, object -> values.add(valueGetter.apply(object)), transaction);
        for (Long secondaryEmployeeId : secondaryEmployees) {
            filter = new HashFilter(fieldNumber, secondaryEmployeeId);
            editableResource.forEach(filter, object -> {
                Long value = valueGetter.apply(object);
                if (values.add(value)) {
                    employeeSetter.accept(object, mainEmployeeId);
                    editableResource.save(object, transaction);
                } else {
                    editableResource.remove(object, transaction);
                }
            }, transaction);
        }
    }

    private void mergeEmployeeAccess(long mainEmployeeId,
                                     HashSet<Long> secondaryEmployees,
                                     QueryTransaction transaction) throws PlatformException {
        HashFilter filter = new HashFilter(ManagerAllAccessReadable.FIELD_MANAGER_ID, mainEmployeeId);
        boolean mainFullAccess = managerAllAccessRemovableResource.find(filter, transaction) != null;
        boolean secondaryFullAccess = false;
        for (Long secondaryEmployeeId : secondaryEmployees) {
            filter = new HashFilter(ManagerAllAccessReadable.FIELD_MANAGER_ID, secondaryEmployeeId);
            ManagerAllAccessEditable managerAllAccess = managerAllAccessRemovableResource.find(filter, transaction);
            if (managerAllAccess != null) {
                managerAllAccessRemovableResource.remove(managerAllAccess, transaction);
                secondaryFullAccess = true;
            }
        }
        if (!mainFullAccess && secondaryFullAccess) {
            ManagerAllAccessEditable managerAllAccess = managerAllAccessRemovableResource.create(transaction);
            managerAllAccess.setManagerId(mainEmployeeId);
            managerAllAccessRemovableResource.save(managerAllAccess, transaction);
        }
        replaceEmployee(
                managerDepartmentAccessRemovableResource,
                ManagerDepartmentAccessEditable.FIELD_MANAGER_ID,
                mainEmployeeId,
                secondaryEmployees,
                ManagerDepartmentAccessEditable::getDepartmentId,
                ManagerDepartmentAccessEditable::setManagerId,
                transaction
        );
        replaceEmployee(
                managerEmployeeAccessRemovableResource,
                ManagerEmployeeAccessEditable.FIELD_MANAGER_ID,
                mainEmployeeId,
                secondaryEmployees,
                ManagerEmployeeAccessEditable::getEmployeeId,
                ManagerEmployeeAccessEditable::setManagerId,
                transaction
        );
        removeEmployees(
                managerEmployeeAccessRemovableResource,
                ManagerEmployeeAccessEditable.FIELD_EMPLOYEE_ID,
                secondaryEmployees,
                transaction
        );
    }
}
