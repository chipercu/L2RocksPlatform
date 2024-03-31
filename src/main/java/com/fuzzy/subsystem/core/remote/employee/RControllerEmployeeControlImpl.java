package com.fuzzy.subsystem.core.remote.employee;

import com.fuzzy.database.domainobject.filter.HashFilter;
import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.*;
import com.fuzzy.platform.querypool.iterator.IteratorEntity;
import com.fuzzy.platform.sdk.context.Context;
import com.fuzzy.platform.sdk.context.ContextTransaction;
import com.fuzzy.platform.sdk.function.Consumer;
import com.fuzzy.subsystem.core.CoreSubsystem;
import com.fuzzy.subsystem.core.access.CorePrivilege;
import com.fuzzy.subsystem.core.accessroleprivileges.EmployeePrivilegesGetter;
import com.fuzzy.subsystem.core.config.AuthenticationConfig;
import com.fuzzy.subsystem.core.config.CoreConfigDescription;
import com.fuzzy.subsystem.core.config.CoreConfigGetter;
import com.fuzzy.subsystem.core.domainobject.department.DepartmentReadable;
import com.fuzzy.subsystem.core.domainobject.employee.EmployeeEditable;
import com.fuzzy.subsystem.core.domainobject.employee.EmployeeReadable;
import com.fuzzy.subsystem.core.domainobject.employeeauthorizationhistory.EmployeeAuthorizationHistoryEditable;
import com.fuzzy.subsystem.core.domainobject.employeephone.EmployeePhoneEditable;
import com.fuzzy.subsystem.core.domainobject.employeephone.EmployeePhoneReadable;
import com.fuzzy.subsystem.core.domainobject.usedpassword.UsedPasswordEditable;
import com.fuzzy.subsystem.core.employeeaccess.ManagerEmployeeAccessSetter;
import com.fuzzy.subsystem.core.logoninfo.LogonInfoManager;
import com.fuzzy.subsystem.core.securitylog.CoreEvent;
import com.fuzzy.subsystem.core.securitylog.CoreParameter;
import com.fuzzy.subsystem.core.securitylog.CoreTarget;
import com.fuzzy.subsystem.core.subscription.employee.GEmployeeUpdateEvent;
import com.fuzzy.subsystem.core.utils.EmployeePasswordSecurityChecker;
import com.fuzzy.subsystem.core.utils.LastAdministratorValidator;
import com.fuzzy.subsystems.access.AccessOperation;
import com.fuzzy.subsystems.exception.GeneralExceptionBuilder;
import com.fuzzy.subsystems.remote.RCExecutor;
import com.fuzzy.subsystems.remote.RemovalData;
import com.fuzzy.subsystems.security.SecurityLog;
import com.fuzzy.subsystems.security.struct.data.event.SyslogStructDataEvent;
import com.fuzzy.subsystems.security.struct.data.target.SyslogStructDataTarget;
import com.fuzzy.subsystems.utils.DomainObjectValidator;
import com.fuzzy.subsystems.utils.PrimaryKeyValidator;
import org.apache.commons.lang3.StringUtils;

import java.time.Instant;
import java.util.*;
import java.util.regex.Pattern;

public class RControllerEmployeeControlImpl extends AbstractQueryRController<CoreSubsystem>
        implements RControllerEmployeeControl {

    private static final String regexEmail = ".+@.+";
    private final CoreSubsystem component;
    private final ReadableResource<DepartmentReadable> departmentReadableResource;
    private final ReadableResource<EmployeeReadable> employeeReadableResource;
    private final RemovableResource<EmployeeEditable> employeeEditableResource;
    private final RemovableResource<UsedPasswordEditable> usedPasswordEditableResource;
    private final RemovableResource<EmployeeAuthorizationHistoryEditable> employeeAuthorizationHistoryRemovableResource;
    private final RemovableResource<EmployeePhoneEditable> employeePhoneRemovableResource;
    private final CoreConfigGetter coreConfigGetter;
    private final RCExecutor<RControllerEmployeeNotification> rControllerEmployeeNotifications;
    private final RCExecutor<RControllerEmployeeLogNotification> rControllerEmployeeLogNotifications;
    private final EmployeePasswordSecurityChecker employeePasswordSecurityChecker;
    private final LastAdministratorValidator lastAdministratorValidator;
    private final ManagerEmployeeAccessSetter employeeAccessSetter;
    private final LogonInfoManager logonInfoManager;
    private final EmployeePrivilegesGetter employeePrivilegesGetter;


    public RControllerEmployeeControlImpl(CoreSubsystem subSystem, ResourceProvider resources) {
        super(subSystem, resources);
        component = subSystem;
        departmentReadableResource = resources.getReadableResource(DepartmentReadable.class);
        employeeReadableResource = resources.getReadableResource(EmployeeReadable.class);
        employeeEditableResource = resources.getRemovableResource(EmployeeEditable.class);
        usedPasswordEditableResource = resources.getRemovableResource(UsedPasswordEditable.class);
        employeeAuthorizationHistoryRemovableResource =
                resources.getRemovableResource(EmployeeAuthorizationHistoryEditable.class);
        employeePhoneRemovableResource = resources.getRemovableResource(EmployeePhoneEditable.class);
        coreConfigGetter = new CoreConfigGetter(resources);
        rControllerEmployeeNotifications = new RCExecutor<>(resources, RControllerEmployeeNotification.class);
        rControllerEmployeeLogNotifications = new RCExecutor<>(resources, RControllerEmployeeLogNotification.class);
        employeePasswordSecurityChecker = new EmployeePasswordSecurityChecker(resources);
        lastAdministratorValidator = new LastAdministratorValidator(subSystem, resources);
        employeeAccessSetter = new ManagerEmployeeAccessSetter(subSystem, resources);
        logonInfoManager = new LogonInfoManager(resources);
        employeePrivilegesGetter = new EmployeePrivilegesGetter(resources);
    }

    @Override
    public EmployeeReadable create(EmployeeBuilder employeeBuilder, ContextTransaction context)
            throws PlatformException {
        QueryTransaction transaction = context.getTransaction();
        if (employeeBuilder.isContainEmail()) {
            checkEmail(employeeBuilder.getEmail(), null, transaction);
        }
        if (employeeBuilder.isContainLogin()) {
            checkLogin(employeeBuilder.getLogin(), null, transaction);
        }
        if (employeeBuilder.isContainPersonnelNumber()) {
            checkPersonnelNumber(employeeBuilder.getPersonnelNumber(), null, transaction);
        }
        checkPhoneNumbers(employeeBuilder);
        EmployeeEditable employee = employeeEditableResource.create(transaction);
        employeeEditableResource.save(employee, transaction);
        setEmployeeFields(
                employee,
                employeeBuilder,
                true,
                context
        );
        employeeEditableResource.save(employee, transaction);
        setPhoneNumbers(employee.getId(), employeeBuilder, transaction);
        employeeAccessSetter.set(
                employee.getId(),
                Collections.emptyList(),
                List.of(employee.getId()),
                Collections.emptyList(),
                Collections.emptyList(),
                transaction);
        rControllerEmployeeNotifications.exec(rc -> rc.onAfterCreateEmployee(employee.getId(), context));
        logEventOnCreate(employee, context);
        notifyLog(rControllerEmployeeLogNotification ->
                rControllerEmployeeLogNotification.endCreateEmployee(employee.getId(), context));
        return employee;
    }

    @Override
    public EmployeeReadable update(final long employeeId, EmployeeBuilder employeeBuilder, ContextTransaction context)
            throws PlatformException {
        QueryTransaction transaction = context.getTransaction();
        EmployeeEditable employee = employeeEditableResource.get(employeeId, transaction);
        if (employee == null) {
            throw GeneralExceptionBuilder.buildNotFoundDomainObjectException(EmployeeReadable.class, employeeId);
        }
        checkAccessSendSystemEvents(employeeBuilder, employee.getId(), context);
        if (employeeBuilder.isContainEmail()) {
            checkEmail(employeeBuilder.getEmail(), employee.getId(), transaction);
        }
        if (employeeBuilder.isContainLogin()) {
            checkLogin(employeeBuilder.getLogin(), employee.getId(), transaction);
        }
        if (employeeBuilder.isContainPersonnelNumber()) {
            checkPersonnelNumber(employeeBuilder.getPersonnelNumber(), employee.getId(), transaction);
        }
        checkPhoneNumbers(employeeBuilder);
        if (employeeBuilder.isContainPasswordHash()) {
            AuthenticationConfig authenticationConfig = coreConfigGetter.getAuthenticationConfig(transaction);
            if (authenticationConfig.getComplexPassword() != null) {
                employeePasswordSecurityChecker.checkUsedPasswords(
                        employee, employeeBuilder.getPasswordHash(), transaction);
            }
            byte[] saltyPasswordHash = employee.getPasswordHash();
            if (saltyPasswordHash != null) {
                UsedPasswordEditable usedPasswordEditable = usedPasswordEditableResource.create(transaction);
                usedPasswordEditable.setEmployeeId(employeeId);
                usedPasswordEditable.setSalt(employee.getSalt());
                usedPasswordEditable.setSaltyPasswordHash(saltyPasswordHash);
                usedPasswordEditableResource.save(usedPasswordEditable, transaction);
            }
        }
        boolean parentChanged = employeeBuilder.isContainDepartmentId() &&
                !Objects.equals(employeeBuilder.getDepartmentId(), employee.getDepartmentId());
        rControllerEmployeeNotifications.exec(rc -> rc.onBeforeUpdateEmployee(employee, employeeBuilder, context));
        EmployeeUpdateLogger employeeUpdateLogger = new EmployeeUpdateLogger(employee);
        setEmployeeFields(
                employee,
                employeeBuilder,
                false,
                context
        );
        if (parentChanged) {
            notifyLog(rControllerEmployeeLogNotification ->
                    rControllerEmployeeLogNotification.startChangeParentDepartment(
                            employee.getId(), context));
        }
        employeeEditableResource.save(employee, transaction);
        setPhoneNumbers(employeeId, employeeBuilder, transaction);
        rControllerEmployeeNotifications.exec(rc -> rc.onAfterUpdateEmployee(employee, context));
        if (parentChanged) {
            notifyLog(rControllerEmployeeLogNotification ->
                    rControllerEmployeeLogNotification.endChangeParentDepartment(
                            employee.getId(), context));
        }
        if (needCheckLastAdmin(employeeBuilder)) {
            lastAdministratorValidator.validate(context);
        }

        employeeUpdateLogger.execute(employee.getId(), employeeBuilder, context);
        return employee;
    }

    @Override
    public RemovalData removeWithCauses(HashSet<Long> employeeIds, ContextTransaction context) throws PlatformException {
        QueryTransaction transaction = context.getTransaction();
        RemovalData removalData = new RemovalData();
        HashSet<Long> notFound = new HashSet<>();
        for (Long employeeId : employeeIds) {
            if (employeeId == null) {
                continue;
            }
            EmployeeEditable employeeEditable = employeeEditableResource.get(employeeId, transaction);
            if (employeeEditable == null) {
                notFound.add(employeeId);
                continue;
            }
            rControllerEmployeeNotifications.exec(rc -> rc.onBeforeRemoveEmployee(employeeId, context));
            notifyLog(rControllerEmployeeLogNotification ->
                    rControllerEmployeeLogNotification.startRemoveEmployee(employeeId, context));
            employeeEditableResource.remove(employeeEditable, transaction);
            notifyLog(rControllerEmployeeLogNotification ->
                    rControllerEmployeeLogNotification.endRemoveEmployee(employeeId, context));
            removalData.getRemoved().add(employeeId);

            SecurityLog.info(
                    new SyslogStructDataEvent(CoreEvent.Employee.TYPE_REMOVE),
                    new SyslogStructDataTarget(CoreTarget.TYPE_EMPLOYEE, employeeEditable.getId())
                            .withParam(CoreParameter.Employee.LOGIN, employeeEditable.getLogin()),
                    context
            );
        }
        lastAdministratorValidator.validate(context);
        removalData.addNonRemoved(GeneralExceptionBuilder.NOT_FOUND_DOMAIN_OBJECT_CODE, notFound);
        return removalData;
    }

    @Override
    public void merge(long mainEmployeeId, HashSet<Long> secondaryEmployees, ContextTransaction context) throws PlatformException {
        QueryTransaction transaction = context.getTransaction();
        EmployeeReadable mainEmployee = new PrimaryKeyValidator(false)
                .validateAndGet(mainEmployeeId, employeeReadableResource, transaction);
        HashSet<Long> validSecondaryEmployees = new HashSet<>();
        for (Long secondaryEmployeeId : secondaryEmployees) {
            if (secondaryEmployeeId != null && !Objects.equals(secondaryEmployeeId, mainEmployeeId) &&
                    employeeReadableResource.get(secondaryEmployeeId, transaction) != null) {
                validSecondaryEmployees.add(secondaryEmployeeId);
            }
        }
        notifyLog(rControllerEmployeeLogNotification ->
                rControllerEmployeeLogNotification.startMergeEmployees(mainEmployeeId, validSecondaryEmployees, context));
        rControllerEmployeeNotifications.exec(rc ->
                rc.onBeforeMergeEmployees(mainEmployeeId, validSecondaryEmployees, context));
        notifyLog(rControllerEmployeeLogNotification ->
                rControllerEmployeeLogNotification.endMergeEmployees(mainEmployeeId, validSecondaryEmployees, context));
        for (Long secondaryEmployeeId : validSecondaryEmployees) {
            EmployeeEditable secondaryEmployee = employeeEditableResource.get(secondaryEmployeeId, transaction);
            employeeEditableResource.remove(secondaryEmployee, transaction);
            SecurityLog.info(
                    new SyslogStructDataEvent(CoreEvent.Employee.TYPE_MERGE),
                    new SyslogStructDataTarget(CoreTarget.TYPE_EMPLOYEE, mainEmployee.getId())
                            .withParam(CoreParameter.Employee.LOGIN, mainEmployee.getLogin())
                            .withParam(CoreParameter.Employee.MERGING_EMPLOYEE_ID, String.valueOf(secondaryEmployee.getId()))
                            .withParam(CoreParameter.Employee.MERGING_EMPLOYEE_LOGIN, secondaryEmployee.getLogin()),
                    context
            );
        }
        lastAdministratorValidator.validate(context);
    }

    private boolean needCheckLastAdmin(EmployeeBuilder employeeBuilder) {
        return employeeBuilder.isContainLogin() && employeeBuilder.getLogin() == null
                || employeeBuilder.isContainPasswordHash() && employeeBuilder.getPasswordHash() == null;
    }

    private void checkEmail(String email, Long employeeId, QueryTransaction transaction) throws PlatformException {
        if (email == null) {
            return;
        }
        if (email.isEmpty()) {
            throw GeneralExceptionBuilder.buildEmptyValueException(EmployeeReadable.class, EmployeeReadable.FIELD_EMAIL);
        }
        if (Boolean.FALSE.equals(email.matches(regexEmail))) {
            throw GeneralExceptionBuilder.buildInvalidValueException("email", email);
        }
        checkUnique(EmployeeReadable.FIELD_EMAIL, email, employeeId, transaction);
    }

    private void checkLogin(String login, Long employeeId, QueryTransaction transaction) throws PlatformException {
        if (login == null) {
            return;
        }
        if (login.isEmpty()) {
            throw GeneralExceptionBuilder.buildEmptyValueException(EmployeeReadable.class, EmployeeReadable.FIELD_LOGIN);
        }
        checkUnique(EmployeeReadable.FIELD_LOGIN, login, employeeId, transaction);
    }

    private void checkPersonnelNumber(String personnelNumber, Long employeeId, QueryTransaction transaction)
            throws PlatformException {
        if (!StringUtils.isEmpty(personnelNumber)) {
            checkUnique(EmployeeReadable.FIELD_PERSONNEL_NUMBER, personnelNumber, employeeId, transaction);
        }
    }

    private void checkAccessSendSystemEvents(EmployeeBuilder employeeBuilder, long employeeId, ContextTransaction context) throws PlatformException {
        if (employeeBuilder.isSendSystemEvents()) {
            final boolean isContainsAccess = employeePrivilegesGetter.checkPrivilegeAccessOperations(
                    employeeId,
                    CorePrivilege.EMPLOYEE_ACCESS.getUniqueKey(),
                    Arrays.asList(AccessOperation.WRITE, AccessOperation.EXECUTE),
                    context
            );
            if (!isContainsAccess) {
                throw GeneralExceptionBuilder.buildNotAccessSendEventsException(
                        String.format("Employee with id:%s doesn't have the necessary privileges from access roles", employeeId)
                );
            }
        }
    }

    private void checkUnique(int fieldNumber, Object fieldValue, Long employeeId, QueryTransaction transaction)
            throws PlatformException {
        DomainObjectValidator.validateUnique(fieldNumber, fieldValue, employeeId, employeeReadableResource, transaction);
    }

    private void setEmployeeFields(EmployeeEditable employee,
                                   EmployeeBuilder employeeBuilder,
                                   boolean isCreationAction,
                                   ContextTransaction<?> context) throws PlatformException {
        QueryTransaction transaction = context.getTransaction();
        boolean isNeedToSendEvent = false;
        if (employeeBuilder.isContainEmail()) {
            employee.setEmail(employeeBuilder.getEmail());
        }
        if (employeeBuilder.isContainLogin()) {
            employee.setLogin(employeeBuilder.getLogin());
        }
        if (employeeBuilder.isContainFirstName()) {
            isNeedToSendEvent = !isCreationAction && !Objects.equals(employeeBuilder.getFirstName(), employee.getFirstName());
            employee.setFirstName(employeeBuilder.getFirstName());
        }
        if (employeeBuilder.isContainPatronymic()) {
            isNeedToSendEvent = !isCreationAction &&
                    (isNeedToSendEvent || !Objects.equals(employeeBuilder.getPatronymic(), employee.getPatronymic()));
            employee.setPatronymic(employeeBuilder.getPatronymic());
        }
        if (employeeBuilder.isContainSecondName()) {
            isNeedToSendEvent = !isCreationAction &&
                    (isNeedToSendEvent || !Objects.equals(employeeBuilder.getSecondName(), employee.getSecondName()));
            employee.setSecondName(employeeBuilder.getSecondName());
        }
        if (employeeBuilder.isContainPasswordHash()) {
            employee.setPasswordHashWithSalt(employeeBuilder.getPasswordHash());
            HashFilter filter = new HashFilter(EmployeeAuthorizationHistoryEditable.FIELD_EMPLOYEE_ID, employee.getId());
            EmployeeAuthorizationHistoryEditable employeeAuthorizationHistory =
                    employeeAuthorizationHistoryRemovableResource.find(filter, transaction);
            if (employeeAuthorizationHistory == null) {
                employeeAuthorizationHistory = employeeAuthorizationHistoryRemovableResource.create(transaction);
                employeeAuthorizationHistory.setEmployeeId(employee.getId());
            }
            employeeAuthorizationHistory.setLastPasswordChangeUtcTime(Instant.now());
            employeeAuthorizationHistoryRemovableResource.save(employeeAuthorizationHistory, transaction);
        }
        if (employeeBuilder.isContainDepartmentId()) {
            Long departmentId = employeeBuilder.getDepartmentId();
            if (departmentId != null && departmentReadableResource.get(departmentId, transaction) == null) {
                throw GeneralExceptionBuilder.buildNotFoundDomainObjectException(DepartmentReadable.class, departmentId);
            }
            employee.setDepartmentId(departmentId);
        }
        if (employeeBuilder.isContainLanguage()) {
            isNeedToSendEvent = !isCreationAction &&
                    (isNeedToSendEvent || !Objects.equals(employeeBuilder.getLanguage(), employee.getLanguage()));
            employee.setLanguage(employeeBuilder.getLanguage());
        } else if (isCreationAction) {
            employee.setLanguage(coreConfigGetter.get(CoreConfigDescription.SERVER_LANGUAGE, transaction));
        }
        if (employeeBuilder.isContainPersonnelNumber()) {
            employee.setPersonnelNumber(employeeBuilder.getPersonnelNumber());
        }
        if (employeeBuilder.isContainNeedToChangePassword()) {
            employee.setNeedToChangePassword(employeeBuilder.getNeedToChangePassword());
        } else if (isCreationAction) {
            employee.setNeedToChangePassword(false);
        }
        if (employeeBuilder.isContainLastLogonTime()) {
            logonInfoManager.setLastLogonTime(employee.getId(), employeeBuilder.getLastLogonTime(), context);
        }
        if (employeeBuilder.isSendSystemEvents()) {
            employee.setSendSystemEvents(employeeBuilder.getSendSystemEvents());
        }
        if (isNeedToSendEvent) {
            GEmployeeUpdateEvent.send(component, employee.getId(), transaction);
        }
    }

    private void checkPhoneNumbers(EmployeeBuilder builder) throws PlatformException {
        if (!builder.isContainPhoneNumbers()) {
            return;
        }
        Pattern pattern = Pattern.compile("\\+?\\d+");
        for (String phoneNumber : builder.getPhoneNumbers()) {
            if (phoneNumber == null || !pattern.matcher(phoneNumber).matches()) {
                throw GeneralExceptionBuilder.buildInvalidValueException("phone_number", phoneNumber);
            }
        }
    }

    private void setPhoneNumbers(long employeeId, EmployeeBuilder builder, QueryTransaction transaction) throws PlatformException {
        if (!builder.isContainPhoneNumbers()) {
            return;
        }
        Set<String> phoneNumbers = new HashSet<>(builder.getPhoneNumbers());
        HashFilter filter = new HashFilter(EmployeePhoneReadable.FIELD_EMPLOYEE_ID, employeeId);
        try (IteratorEntity<EmployeePhoneEditable> ie = employeePhoneRemovableResource.findAll(filter, transaction)) {
            while (ie.hasNext()) {
                EmployeePhoneEditable employeePhone = ie.next();
                if (!phoneNumbers.remove(employeePhone.getPhoneNumber())) {
                    employeePhoneRemovableResource.remove(employeePhone, transaction);
                }
            }
        }
        for (String phoneNumber : phoneNumbers) {
            EmployeePhoneEditable employeePhone = employeePhoneRemovableResource.create(transaction);
            employeePhone.setEmployeeId(employeeId);
            employeePhone.setPhoneNumber(phoneNumber);
            employeePhoneRemovableResource.save(employeePhone, transaction);
        }
    }

    private void notifyLog(Consumer<RControllerEmployeeLogNotification> handler) throws PlatformException {
        rControllerEmployeeLogNotifications.exec(handler);
    }

    private void logEventOnCreate(EmployeeReadable employeeReadable, Context<?> context) {
        SecurityLog.info(
                new SyslogStructDataEvent(CoreEvent.Employee.TYPE_CREATE)
                        .withParam(CoreParameter.Employee.FIRST_NAME, employeeReadable.getFirstName())
                        .withParam(CoreParameter.Employee.SECOND_NAME, employeeReadable.getSecondName())
                        .withParam(CoreParameter.Employee.PATRONYMIC, employeeReadable.getPatronymic())
                        .withParam(CoreParameter.Employee.PERSONNEL_NUMBER, employeeReadable.getPersonnelNumber())
                        .withParam(CoreParameter.Employee.LOGIN, employeeReadable.getLogin())
                        .withParam(CoreParameter.Employee.EMAIL, employeeReadable.getEmail()),
                new SyslogStructDataTarget(CoreTarget.TYPE_EMPLOYEE, employeeReadable.getId())
                        .withParam(CoreParameter.Employee.LOGIN, employeeReadable.getLogin()),
                context
        );
    }

    private static class EmployeeUpdateLogger {

        private final String oldFirstName;
        private final String oldSecondName;
        private final String oldPatronymic;
        private final String oldPersonnelNumber;
        private final String oldLogin;
        private final String oldEmail;
        private final Long oldDepartmentId;

        private EmployeeUpdateLogger(EmployeeReadable oldValue) {
            oldFirstName = oldValue.getFirstName();
            oldSecondName = oldValue.getSecondName();
            oldPatronymic = oldValue.getPatronymic();
            oldPersonnelNumber = oldValue.getPersonnelNumber();
            oldLogin = oldValue.getLogin();
            oldEmail = oldValue.getEmail();
            oldDepartmentId = oldValue.getDepartmentId();
        }

        private void execute(long employeeId, EmployeeBuilder newValue, Context<?> context) {
            SyslogStructDataEvent syslogStructDataEvent = new SyslogStructDataEvent(CoreEvent.Employee.TYPE_UPDATE);
            if (newValue.isContainFirstName() && !Objects.equals(oldFirstName, newValue.getFirstName())) {
                syslogStructDataEvent
                        .withParam(CoreParameter.Employee.OLD_FIRST_NAME, oldFirstName)
                        .withParam(CoreParameter.Employee.NEW_FIRST_NAME, newValue.getFirstName());
            }
            if (newValue.isContainSecondName() && !Objects.equals(oldSecondName, newValue.getSecondName())) {
                syslogStructDataEvent
                        .withParam(CoreParameter.Employee.OLD_SECOND_NAME, oldSecondName)
                        .withParam(CoreParameter.Employee.NEW_SECOND_NAME, newValue.getSecondName());
            }
            if (newValue.isContainPatronymic() && !Objects.equals(oldPatronymic, newValue.getPatronymic())) {
                syslogStructDataEvent
                        .withParam(CoreParameter.Employee.OLD_PATRONYMIC, oldPatronymic)
                        .withParam(CoreParameter.Employee.NEW_PATRONYMIC, newValue.getPatronymic());
            }
            if (newValue.isContainPersonnelNumber() && !Objects.equals(oldPersonnelNumber, newValue.getPersonnelNumber())) {
                syslogStructDataEvent
                        .withParam(CoreParameter.Employee.OLD_PERSONNEL_NUMBER, oldPersonnelNumber)
                        .withParam(CoreParameter.Employee.NEW_PERSONNEL_NUMBER, newValue.getPersonnelNumber());
            }
            if (newValue.isContainLogin() && !Objects.equals(oldLogin, newValue.getLogin())) {
                syslogStructDataEvent
                        .withParam(CoreParameter.Employee.OLD_LOGIN, oldLogin)
                        .withParam(CoreParameter.Employee.NEW_LOGIN, newValue.getLogin());
            }
            if (newValue.isContainEmail() && !Objects.equals(oldEmail, newValue.getEmail())) {
                syslogStructDataEvent
                        .withParam(CoreParameter.Employee.OLD_EMAIL, oldEmail)
                        .withParam(CoreParameter.Employee.NEW_EMAIL, newValue.getEmail());
            }
            if (newValue.isContainDepartmentId() && !Objects.equals(oldDepartmentId, newValue.getDepartmentId())) {
                syslogStructDataEvent
                        .withParam(CoreParameter.Employee.OLD_DEPARTMENT_ID, String.valueOf(oldDepartmentId))
                        .withParam(CoreParameter.Employee.NEW_DEPARTMENT_ID, String.valueOf(newValue.getDepartmentId()));
            }

            if (!syslogStructDataEvent.getData().isEmpty()) {
                SecurityLog.info(
                        syslogStructDataEvent,
                        new SyslogStructDataTarget(CoreTarget.TYPE_EMPLOYEE, employeeId)
                                .withParam(CoreParameter.Employee.LOGIN, oldLogin),
                        context
                );
            }
        }
    }
}
