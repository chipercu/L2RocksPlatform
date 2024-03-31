package com.fuzzy.subsystem.core.graphql.mutation.employee;

import com.fuzzy.cluster.core.remote.struct.RemoteObject;
import com.fuzzy.cluster.graphql.anotation.GraphQLDescription;
import com.fuzzy.cluster.graphql.anotation.GraphQLField;
import com.fuzzy.cluster.graphql.anotation.GraphQLName;
import com.fuzzy.cluster.graphql.anotation.GraphQLTypeOutObject;
import com.fuzzy.cluster.graphql.struct.GOptional;
import com.fuzzy.database.domainobject.filter.HashFilter;
import com.fuzzy.platform.component.frontend.authcontext.UnauthorizedContext;
import com.fuzzy.platform.component.frontend.context.ContextTransactionRequest;
import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.*;
import com.fuzzy.platform.querypool.iterator.IteratorEntity;
import com.fuzzy.platform.sdk.context.Context;
import com.fuzzy.platform.sdk.context.ContextTransaction;
import com.fuzzy.platform.sdk.graphql.annotation.GraphQLAuthControl;
import com.fuzzy.platform.sdk.graphql.customfield.graphqlquery.GraphQLQuery;
import com.fuzzy.subsystem.core.CoreSubsystem;
import com.fuzzy.subsystem.core.CoreSubsystemConsts;
import com.fuzzy.subsystem.core.access.CorePrivilege;
import com.fuzzy.subsystem.core.authcontext.employee.EmployeeAuthContext;
import com.fuzzy.subsystem.core.config.Language;
import com.fuzzy.subsystem.core.config.LogonType;
import com.fuzzy.subsystem.core.domainobject.accessrole.AccessRoleReadable;
import com.fuzzy.subsystem.core.domainobject.employee.EmployeeReadable;
import com.fuzzy.subsystem.core.domainobject.employeeaccessrole.EmployeeAccessRoleReadable;
import com.fuzzy.subsystem.core.domainobject.employeedata.EmployeeDataEditable;
import com.fuzzy.subsystem.core.domainobject.employeedata.EmployeeDataReadable;
import com.fuzzy.subsystem.core.domainobject.usedpassword.UsedPasswordReadable;
import com.fuzzy.subsystem.core.emailmessages.EmployeeDisplayNameForMessageBuilder;
import com.fuzzy.subsystem.core.emailmessages.InvitationMessage;
import com.fuzzy.subsystem.core.emailmessages.PasswordChangeMessageSender;
import com.fuzzy.subsystem.core.emailmessages.PasswordRecoveryMessage;
import com.fuzzy.subsystem.core.employeeaccess.ManagerEmployeeAccess;
import com.fuzzy.subsystem.core.employeeaccess.ManagerEmployeeAccessGetter;
import com.fuzzy.subsystem.core.employeeaccess.ManagerEmployeeAccessSetter;
import com.fuzzy.subsystem.core.employeetoken.EmployeeTokenManager;
import com.fuzzy.subsystem.core.employeetoken.EmployeeTokenManagerFactory;
import com.fuzzy.subsystem.core.employeetoken.EmployeeTokenReadable;
import com.fuzzy.subsystem.core.enums.FieldDataType;
import com.fuzzy.subsystem.core.exception.CoreExceptionBuilder;
import com.fuzzy.subsystem.core.graphql.mutation.employee.queries.AdditionalFieldArrayUpdateQuery;
import com.fuzzy.subsystem.core.graphql.mutation.employee.queries.AdditionalFieldValueUpdateQuery;
import com.fuzzy.subsystem.core.graphql.mutation.employee.queries.AuthenticationAssigningQuery;
import com.fuzzy.subsystem.core.graphql.mutation.employee.queries.AuthenticationSettingQuery;
import com.fuzzy.subsystem.core.graphql.query.config.HelpDeskConfig;
import com.fuzzy.subsystem.core.graphql.query.employee.GEmployee;
import com.fuzzy.subsystem.core.remote.accessrole.RControllerAccessRole;
import com.fuzzy.subsystem.core.remote.additionalfieldvaluesetter.RCAdditionalFieldValueSetter;
import com.fuzzy.subsystem.core.remote.employee.ChangePasswordCause;
import com.fuzzy.subsystem.core.remote.employee.EmployeeBuilder;
import com.fuzzy.subsystem.core.remote.employee.RControllerEmployeeControl;
import com.fuzzy.subsystem.core.remote.employeeauthenticationchecker.RCEmployeeAuthenticationChecker;
import com.fuzzy.subsystem.core.remote.integrations.RCIntegrationsExecutor;
import com.fuzzy.subsystem.core.remote.logon.RControllerEmployeeLogon;
import com.fuzzy.subsystem.core.remote.mail.Mail;
import com.fuzzy.subsystem.core.remote.mail.RControllerHelpDeskGetterWrapper;
import com.fuzzy.subsystem.core.remote.mail.RControllerMailSenderWrapper;
import com.fuzzy.subsystem.core.securitylog.CoreEvent;
import com.fuzzy.subsystem.core.securitylog.CoreParameter;
import com.fuzzy.subsystem.core.securitylog.CoreTarget;
import com.fuzzy.subsystem.core.securitylog.ManagerEmployeeAccessSecurityLogger;
import com.fuzzy.subsystem.core.utils.LanguageGetter;
import com.fuzzy.subsystem.frontend.FrontendSubsystem;
import com.fuzzy.subsystem.frontend.authcontext.AuthorizedContext;
import com.fuzzy.subsystem.frontend.remote.info.RControllerFrontendInfo;
import com.fuzzy.subsystems.access.AccessOperation;
import com.fuzzy.subsystems.access.AccessUtils;
import com.fuzzy.subsystems.exception.GeneralExceptionBuilder;
import com.fuzzy.subsystems.graphql.input.GInputKeyValue;
import com.fuzzy.subsystems.graphql.input.datetime.GInputDateTime;
import com.fuzzy.subsystems.graphql.input.datetime.GInputLocalDate;
import com.fuzzy.subsystems.graphql.out.GRemovalData;
import com.fuzzy.subsystems.graphql.query.GAccessQuery;
import com.fuzzy.subsystems.remote.RemovalData;
import com.fuzzy.subsystems.security.SecurityLog;
import com.fuzzy.subsystems.security.struct.data.event.SyslogStructDataEvent;
import com.fuzzy.subsystems.security.struct.data.target.SyslogStructDataTarget;
import com.fuzzy.subsystems.utils.PrimaryKeyValidator;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.fuzzy.subsystem.core.CoreSubsystemConsts.Mail.INVITATION_TOKEN_EXPIRATION_DAY;

@GraphQLTypeOutObject("mutation_employee")
public class GMutationEmployee {

    private static final Logger log = LoggerFactory.getLogger(GMutationEmployee.class);
    private static final String ID = "id";
    private static final String IDS = "ids";
    private static final String TARGET_DEPARTMENT_IDS = "target_department_ids";
    private static final String TARGET_EMPLOYEE_IDS = "target_employee_ids";
    private static final String TARGET_ALL = "target_all";
    private static final String DEPARTMENT_ID = "department_id";
    private static final String EMAIL = "email";
    private static final String LOGIN = "login";
    private static final String FIRST_NAME = "first_name";
    private static final String PATRONYMIC = "patronymic";
    private static final String SECOND_NAME = "second_name";
    private static final String PASSWORD_HASH = "password_hash";
    private static final String CURRENT_PASSWORD_HASH = "current_password_hash";
    private static final String TOKEN = "token";
    private static final String LANGUAGE = "language";
    private static final String ACCESS_ROLE_IDS = "access_role_ids";
    private static final String NOTIFICATIONS_OF_DISABLED_LOGON = "notifications_of_disabled_logon";
    private static final String PERSONNEL_NUMBER = "personnel_number";
    private static final String EMPLOYEE_ID = "employee_id";
    private static final String ALL = "all";
    private static final String INSERTED_DEPARTMENTS = "inserted_departments";
    private static final String INSERTED_EMPLOYEES = "inserted_employees";
    private static final String REMOVED_DEPARTMENTS = "removed_departments";
    private static final String REMOVED_EMPLOYEES = "removed_employees";
    private static final String PHONE_NUMBERS = "phone_numbers";
    private static final String ADDITIONAL_FIELD_ID = "additional_field_id";
    private static final String VALUE = "value";
    private static final String ERASED_AUTHENTICATION_IDS = "erased_authentication_ids";
    private static final String ASSIGNED_AUTHENTICATION_IDS = "assigned_authentication_ids";
    private static final String AUTHENTICATION_IDS = "authentication_ids";
    private static final String IS_SYSTEM_EVENTS = "is_system_events";


    private GMutationEmployee() {
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Создание сотрудника")
    public static GraphQLQuery<RemoteObject, GEmployee> create(
            @GraphQLName(EMAIL)
            @GraphQLDescription("Электронная почта") final GOptional<String> email,
            @GraphQLName(LOGIN)
            @GraphQLDescription("Догин") final GOptional<String> login,
            @GraphQLName(FIRST_NAME)
            @GraphQLDescription("Имя") final GOptional<String> firstName,
            @GraphQLName(PATRONYMIC)
            @GraphQLDescription("Отчество") final GOptional<String> patronymic,
            @GraphQLName(SECOND_NAME)
            @GraphQLDescription("Фамилия") final GOptional<String> secondName,
            @GraphQLName(DEPARTMENT_ID)
            @GraphQLDescription("Идентификатор отдела") final GOptional<Long> departmentId,
            @GraphQLName(PASSWORD_HASH)
            @GraphQLDescription("Хеш пароля") final GOptional<String> passwordHash,
            @GraphQLName(LANGUAGE)
            @GraphQLDescription("Язык") final GOptional<Language> language,
            @GraphQLName(PERSONNEL_NUMBER)
            @GraphQLDescription("Табельный номер") final GOptional<String> personnelNumber,
            @GraphQLName(ACCESS_ROLE_IDS)
            @GraphQLDescription("Идентификаторы ролей доступа") final HashSet<Long> accessRoleIds,
            @GraphQLName(PHONE_NUMBERS)
            @GraphQLDescription("Телефонные номера") final GOptional<HashSet<String>> phoneNumbers
    ) {
        return new GraphQLQuery<RemoteObject, GEmployee>() {

            private RControllerEmployeeControl rControllerEmployeeControl;
            private RControllerAccessRole rControllerAccessRole;

            @Override
            public void prepare(ResourceProvider resources) {
                rControllerEmployeeControl =
                        resources.getQueryRemoteController(CoreSubsystem.class, RControllerEmployeeControl.class);
                rControllerAccessRole =
                        resources.getQueryRemoteController(CoreSubsystem.class, RControllerAccessRole.class);
            }

            @Override
            public GEmployee execute(RemoteObject source, ContextTransactionRequest context)
                    throws PlatformException {

                AccessUtils.validateInputParameters(
                        context, CorePrivilege.EMPLOYEES, AccessOperation.CREATE,
                        email, firstName, secondName, patronymic, departmentId,
                        language, personnelNumber, phoneNumbers
                );
                AccessUtils.validateInputParameters(
                        context, CorePrivilege.EMPLOYEE_ACCESS, AccessOperation.WRITE,
                        login, passwordHash
                );

                //TODO: при необходимости добавить systemEvents в ветку создания сотрудника.
                EmployeeBuilder employeeBuilder = getEmployeeBuilder(
                        true,
                        email,
                        login,
                        firstName,
                        patronymic,
                        secondName,
                        departmentId,
                        passwordHash,
                        language,
                        personnelNumber,
                        phoneNumbers,
                        null
                );
                EmployeeReadable employee = rControllerEmployeeControl.create(employeeBuilder, context);
                if (accessRoleIds != null) {
                    for (Long accessRoleId : accessRoleIds) {
                        if (accessRoleId != null) {
                            rControllerAccessRole.assignAccessRoleToEmployee(
                                    accessRoleId,
                                    employee.getId(),
                                    context
                            );
                        }
                    }
                }
                return new GEmployee(employee);
            }
        };
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Обновление сотрудника")
    public static GraphQLQuery<RemoteObject, GEmployee> update(
            CoreSubsystem coreSubsystem,
            @NonNull @GraphQLName(ID)
            @GraphQLDescription("Идентификатор обновляемого сотрудника") final long employeeId,
            @GraphQLName(EMAIL)
            @GraphQLDescription("Новое значение электронной почты") final GOptional<String> email,
            @GraphQLName(LOGIN)
            @GraphQLDescription("Новое значение логина") final GOptional<String> login,
            @GraphQLName(FIRST_NAME)
            @GraphQLDescription("Новое значение имени") final GOptional<String> firstName,
            @GraphQLName(PATRONYMIC)
            @GraphQLDescription("Новое значение отчества") final GOptional<String> patronymic,
            @GraphQLName(SECOND_NAME)
            @GraphQLDescription("Новое значение фамилии") final GOptional<String> secondName,
            @GraphQLName(DEPARTMENT_ID)
            @GraphQLDescription("Новое значение идентификатора отдела") final GOptional<Long> departmentId,
            @GraphQLName(PASSWORD_HASH)
            @GraphQLDescription("Новое значение хеша пароля") final GOptional<String> passwordHash,
            @GraphQLName(CURRENT_PASSWORD_HASH)
            @GraphQLDescription("Хеш текущего пароля") final GOptional<String> currentPasswordHash,
            @GraphQLName(LANGUAGE)
            @GraphQLDescription("Новое значение языка") final GOptional<Language> language,
            @GraphQLName(PERSONNEL_NUMBER)
            @GraphQLDescription("Новое значение табельного номера") final GOptional<String> personnelNumber,
            @GraphQLName(PHONE_NUMBERS)
            @GraphQLDescription("Телефонные номера") final GOptional<HashSet<String>> phoneNumbers,
            @GraphQLName(IS_SYSTEM_EVENTS)
            @GraphQLDescription("Оповещения системы") final GOptional<Boolean> systemEvents
    ) {
        return new GraphQLQuery<RemoteObject, GEmployee>() {

            private RControllerEmployeeControl rControllerEmployeeControl;
            private ReadableResource<EmployeeReadable> employeeReadableResource;
            private ReadableResource<UsedPasswordReadable> usedPasswordReadableResource;
            private PasswordChangeMessageSender passwordChangeMessageSender;
            private ManagerEmployeeAccessGetter managerEmployeeAccessGetter;
            private RCIntegrationsExecutor rcIntegrations;

            @Override
            public void prepare(ResourceProvider resources) {
                rControllerEmployeeControl =
                        resources.getQueryRemoteController(CoreSubsystem.class, RControllerEmployeeControl.class);
                employeeReadableResource = resources.getReadableResource(EmployeeReadable.class);
                usedPasswordReadableResource = resources.getReadableResource(UsedPasswordReadable.class);
                passwordChangeMessageSender = new PasswordChangeMessageSender(coreSubsystem, resources);
                managerEmployeeAccessGetter = new ManagerEmployeeAccessGetter(resources);
                rcIntegrations = new RCIntegrationsExecutor(resources);
            }

            @Override
            public GEmployee execute(RemoteObject source, ContextTransactionRequest context) throws PlatformException {

                QueryTransaction transaction = context.getTransaction();
                EmployeeReadable employee = employeeReadableResource.get(employeeId, transaction);
                if (employee == null) {
                    throw GeneralExceptionBuilder.buildNotFoundDomainObjectException(EmployeeReadable.class, employeeId);
                }

                AuthorizedContext authorizedContext = (AuthorizedContext) context.getSource().getAuthContext();
                boolean accessibleEmployee = true;
                Long authEmployeeId = null;
                if (authorizedContext instanceof EmployeeAuthContext) {
                    EmployeeAuthContext employeeAuthContext = (EmployeeAuthContext) authorizedContext;
                    authEmployeeId = employeeAuthContext.getEmployeeId();
                    ManagerEmployeeAccess access = managerEmployeeAccessGetter.getAccess(authEmployeeId, transaction);
                    accessibleEmployee = access.checkEmployee(employeeId);
                }

                if (language.isPresent() || passwordHash.isPresent()) {
                    if (!Objects.equals(employeeId, authEmployeeId) ||
                            !authorizedContext.getOperations(CorePrivilege.PRIVATE_SETTINGS.getUniqueKey())
                                    .contains(AccessOperation.WRITE)) {
                        if (!accessibleEmployee) {
                            throw GeneralExceptionBuilder.buildAccessDeniedException();
                        }
                        AccessUtils.validateInputParameters(
                                context, CorePrivilege.EMPLOYEES, AccessOperation.WRITE,
                                language
                        );
                        AccessUtils.validateInputParameters(
                                context, CorePrivilege.EMPLOYEE_ACCESS, AccessOperation.WRITE,
                                passwordHash
                        );
                    }
                }
                if (firstName.isPresent() || secondName.isPresent() || patronymic.isPresent() ||
                        email.isPresent() || login.isPresent() || departmentId.isPresent() ||
                        personnelNumber.isPresent() || phoneNumbers.isPresent() || systemEvents.isPresent()) {
                    if (!accessibleEmployee) {
                        throw GeneralExceptionBuilder.buildAccessDeniedException();
                    }
                    AccessUtils.validateInputParameters(
                            context, CorePrivilege.EMPLOYEES, AccessOperation.WRITE,
                            firstName, secondName, patronymic, email, departmentId, personnelNumber, phoneNumbers,
                            systemEvents
                    );
                    AccessUtils.validateInputParameters(
                            context, CorePrivilege.EMPLOYEE_ACCESS, AccessOperation.WRITE,
                            login
                    );
                }

                if (passwordHash.isPresent() && Objects.equals(employeeId, authEmployeeId)
                        && employee.hasPassword()
                        && (!currentPasswordHash.isPresent() || !employee.checkPasswordHash(currentPasswordHash.get()))) {
                    throw GeneralExceptionBuilder.buildInvalidValueException(
                            CURRENT_PASSWORD_HASH, currentPasswordHash.get());
                }

                validateSynchronizedField(rcIntegrations, employeeId, email,
                        CoreSubsystemConsts.EmployeeSystemFields.EMAIL_KEY, context);
                validateSynchronizedField(rcIntegrations, employeeId, firstName,
                        CoreSubsystemConsts.EmployeeSystemFields.FIRST_NAME_KEY, context);
                validateSynchronizedField(rcIntegrations, employeeId, patronymic,
                        CoreSubsystemConsts.EmployeeSystemFields.PATRONYMIC_KEY, context);
                validateSynchronizedField(rcIntegrations, employeeId, secondName,
                        CoreSubsystemConsts.EmployeeSystemFields.SECOND_NAME_KEY, context);
                validateSynchronizedField(rcIntegrations, employeeId, departmentId,
                        CoreSubsystemConsts.EmployeeSystemFields.DEPARTMENT_ID_KEY, context);
                validateSynchronizedField(rcIntegrations, employeeId, language,
                        CoreSubsystemConsts.EmployeeSystemFields.LANGUAGE_KEY, context);
                validateSynchronizedField(rcIntegrations, employeeId, personnelNumber,
                        CoreSubsystemConsts.EmployeeSystemFields.PERSONNEL_NUMBER_KEY, context);
                validateSynchronizedField(rcIntegrations, employeeId, phoneNumbers,
                        CoreSubsystemConsts.EmployeeSystemFields.PHONE_NUMBER_KEY, context);

                EmployeeBuilder employeeBuilder = getEmployeeBuilder(
                        authEmployeeId == null || !authEmployeeId.equals(employeeId),
                        email,
                        login,
                        firstName,
                        patronymic,
                        secondName,
                        departmentId,
                        passwordHash,
                        language,
                        personnelNumber,
                        phoneNumbers,
                        systemEvents);
                employee = rControllerEmployeeControl.update(employeeId, employeeBuilder, context);
                if (passwordHash.isPresent()) {
                    HashFilter filter = new HashFilter(UsedPasswordReadable.FIELD_EMPLOYEE_ID, employeeId);
                    if (usedPasswordReadableResource.find(filter, transaction) != null) {
                        boolean changeByAdmin = !Objects.equals(employeeId, authEmployeeId);
                        passwordChangeMessageSender.sendAsync(employee, changeByAdmin, transaction);
                    }
                    logPasswordChange(ChangePasswordCause.EMPLOYEE_UPDATE, employee, context);
                }
                return new GEmployee(employeeReadableResource.get(employeeId, transaction));
            }
        };
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Пакетное обновление сотрудников")
    public static GraphQLQuery<RemoteObject, Boolean> multiUpdate(
            @GraphQLName(TARGET_ALL)
            @GraphQLDescription("Флаг обновления всех сотрудников") final boolean targetAll,
            @GraphQLName(TARGET_DEPARTMENT_IDS)
            @GraphQLDescription("Идентификаторы отделов, сотрудники которых обновляются") final HashSet<Long> targetDepartmentIds,
            @GraphQLName(TARGET_EMPLOYEE_IDS)
            @GraphQLDescription("Идентификаторы обновляемых сотрудников") final HashSet<Long> targetEmployeeIds,
            @GraphQLName(DEPARTMENT_ID)
            @GraphQLDescription("Новое значение идентификатора отдела") final GOptional<Long> departmentId,
            @GraphQLName(LANGUAGE)
            @GraphQLDescription("Новое значение языка") final GOptional<Language> language
    ) {
        return new GraphQLQuery<RemoteObject, Boolean>() {

            private RControllerEmployeeControl rControllerEmployeeControl;
            private RCIntegrationsExecutor rcIntegrations;
            private EmployeeFilterProcessor employeeFilterProcessor;

            @Override
            public void prepare(ResourceProvider resources) {
                rControllerEmployeeControl =
                        resources.getQueryRemoteController(CoreSubsystem.class, RControllerEmployeeControl.class);
                rcIntegrations = new RCIntegrationsExecutor(resources);
                employeeFilterProcessor = new EmployeeFilterProcessor(resources);
            }

            @Override
            public Boolean execute(RemoteObject source, ContextTransactionRequest context) throws PlatformException {

                AccessUtils.validateInputParameters(
                        context, CorePrivilege.EMPLOYEES, AccessOperation.WRITE,
                        departmentId, language
                );

                Set<Long> targetEmployees = employeeFilterProcessor.getEmployees(
                        targetAll,
                        TARGET_ALL,
                        targetDepartmentIds,
                        targetEmployeeIds,
                        context
                );
                for (Long employeeId : targetEmployees) {
                    EmployeeBuilder employeeBuilder = getEmployeeBuilder(
                            false,
                            null,
                            null,
                            null,
                            null,
                            null,
                            getValue(departmentId, employeeId,
                                    CoreSubsystemConsts.EmployeeSystemFields.DEPARTMENT_ID_KEY, context),
                            null,
                            getValue(language, employeeId,
                                    CoreSubsystemConsts.EmployeeSystemFields.LANGUAGE_KEY, context),
                            null,
                            null,
                            null);
                    rControllerEmployeeControl.update(employeeId, employeeBuilder, context);
                }
                return true;
            }

            private <T> GOptional<T> getValue(GOptional<T> value,
                                              long employeeId,
                                              String fieldKey,
                                              ContextTransaction<?> context) throws PlatformException {
                if (value == null || !value.isPresent() ||
                        !rcIntegrations.isSynchronized(employeeId, EmployeeReadable.class.getName(), fieldKey, context)) {
                    return value;
                }
                return null;
            }
        };
    }

    @GraphQLField
    @GraphQLAuthControl(UnauthorizedContext.class)
    @GraphQLDescription("Установка нового пароля, взамен устаревшего")
    public static GraphQLQuery<RemoteObject, Boolean> changeExpirationPassword(
            CoreSubsystem coreSubsystem,
            @NonNull @GraphQLName(LOGIN)
            @GraphQLDescription("Логин сотрудника") final String login,
            @NonNull @GraphQLName(CURRENT_PASSWORD_HASH)
            @GraphQLDescription("Хеш текущего пароля сотрудника") final String currentPasswordHash,
            @NonNull @GraphQLName(PASSWORD_HASH)
            @GraphQLDescription("Хеш нового пароля сотрудника") final String newPasswordHash
    ) {
        return new GraphQLQuery<RemoteObject, Boolean>() {

            private ReadableResource<EmployeeReadable> employeeReadableResource;
            private RControllerEmployeeControl rControllerEmployeeControl;
            private RControllerEmployeeLogon rControllerEmployeeLogon;
            private RCEmployeeAuthenticationChecker rcEmployeeAuthenticationChecker;
            private PasswordChangeMessageSender passwordChangeMessageSender;

            @Override
            public void prepare(ResourceProvider resources) {
                employeeReadableResource = resources.getReadableResource(EmployeeReadable.class);
                rControllerEmployeeControl =
                        resources.getQueryRemoteController(CoreSubsystem.class, RControllerEmployeeControl.class);
                rControllerEmployeeLogon =
                        resources.getQueryRemoteController(CoreSubsystem.class, RControllerEmployeeLogon.class);
                rcEmployeeAuthenticationChecker =
                        resources.getQueryRemoteController(CoreSubsystem.class, RCEmployeeAuthenticationChecker.class);
                passwordChangeMessageSender = new PasswordChangeMessageSender(coreSubsystem, resources);
            }

            @Override
            public Boolean execute(RemoteObject source, ContextTransactionRequest context)
                    throws PlatformException {
                if (currentPasswordHash.equals(newPasswordHash)) {
                    throw CoreExceptionBuilder.buildNewPasswordEqualsCurrentPasswordException();
                }
                EmployeeReadable employee = getEmployeeByLogin(
                        login,
                        rControllerEmployeeLogon.getLogonType(),
                        employeeReadableResource,
                        context.getTransaction()
                );
                if (employee == null) {
                    throw GeneralExceptionBuilder.buildInvalidCredentialsException();
                }
                if (!rcEmployeeAuthenticationChecker.isAssigned(
                        CoreSubsystemConsts.AuthenticationTypes.INTEGRATED, employee.getId(), context)) {
                    throw CoreExceptionBuilder.buildIntegratedAuthenticationNotFoundException();
                }
                if (!employee.checkPasswordHash(currentPasswordHash)) {
                    throw GeneralExceptionBuilder.buildInvalidCredentialsException();
                }
                EmployeeBuilder employeeBuilder = new EmployeeBuilder()
                        .withPasswordHash(newPasswordHash)
                        .withNeedToChangePassword(false);
                rControllerEmployeeControl.update(employee.getId(), employeeBuilder, context);
                if (employeeBuilder.isContainPasswordHash()) {
                    logPasswordChange(ChangePasswordCause.CHANGE_EXPIRATION_PASSWORD, employee, context);
                }
                passwordChangeMessageSender.sendAsync(employee, false, context.getTransaction());

                return true;
            }
        };
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Удаление сотрудников")
    public static GraphQLQuery<RemoteObject, GRemovalData> remove(
            @GraphQLDescription("Идентификаторы удаляемых сотрудников")
            @NonNull @GraphQLName(IDS) final HashSet<Long> employeeIds
    ) {
        GraphQLQuery<RemoteObject, GRemovalData> query = new GraphQLQuery<RemoteObject, GRemovalData>() {

            private ReadableResource<AccessRoleReadable> accessRoleReadableResource;
            private ReadableResource<EmployeeAccessRoleReadable> employeeAccessRoleReadableResource;
            private RControllerEmployeeControl rControllerEmployeeControl;
            private ManagerEmployeeAccessGetter managerEmployeeAccessGetter;

            @Override
            public void prepare(ResourceProvider resources) {
                accessRoleReadableResource = resources.getReadableResource(AccessRoleReadable.class);
                employeeAccessRoleReadableResource = resources.getReadableResource(EmployeeAccessRoleReadable.class);
                rControllerEmployeeControl =
                        resources.getQueryRemoteController(CoreSubsystem.class, RControllerEmployeeControl.class);
                managerEmployeeAccessGetter = new ManagerEmployeeAccessGetter(resources);
            }

            @Override
            public GRemovalData execute(RemoteObject source, ContextTransactionRequest context)
                    throws PlatformException {
                QueryTransaction transaction = context.getTransaction();
                ManagerEmployeeAccess access = null;
                Long currentEmployeeId = null;
                UnauthorizedContext authContext = context.getSource().getAuthContext();
                if (authContext instanceof EmployeeAuthContext) {
                    currentEmployeeId = ((EmployeeAuthContext) authContext).getEmployeeId();
                    access = managerEmployeeAccessGetter.getAccess(currentEmployeeId, context.getTransaction());
                }
                Set<Long> adminRoles = getAdminRoles(transaction);
                HashSet<Long> removingEmployees = new HashSet<>();
                boolean isHimselfEmployee = false;
                HashSet<Long> accessDenied = new HashSet<>();
                HashSet<Long> admins = new HashSet<>();
                for (Long employeeId : employeeIds) {
                    if (employeeId == null) {
                        continue;
                    }
                    if (Objects.equals(employeeId, currentEmployeeId)) {
                        isHimselfEmployee = true;
                        continue;
                    }
                    if (access != null && !access.checkEmployee(employeeId)) {
                        accessDenied.add(employeeId);
                        continue;
                    }
                    if (isAdmin(employeeId, adminRoles, transaction)) {
                        admins.add(employeeId);
                        continue;
                    }
                    removingEmployees.add(employeeId);
                }
                RemovalData removalData = rControllerEmployeeControl.removeWithCauses(removingEmployees, context);
                if (isHimselfEmployee) {
                    removalData.addNonRemoved(CoreExceptionBuilder.EMPLOYEE_SELF_REMOVE_CODE, List.of(currentEmployeeId));
                }
                removalData.addNonRemoved(GeneralExceptionBuilder.ACCESS_DENIED_CODE, accessDenied);
                removalData.addNonRemoved(CoreExceptionBuilder.ADMINISTRATOR_EMPLOYEE_CODE, admins);
                return new GRemovalData(removalData);
            }

            private Set<Long> getAdminRoles(QueryTransaction transaction) throws PlatformException {
                Set<Long> adminRoles = new HashSet<>();
                HashFilter filter = new HashFilter(AccessRoleReadable.FIELD_ADMIN, true);
                accessRoleReadableResource.forEach(filter, accessRole -> adminRoles.add(accessRole.getId()), transaction);
                return adminRoles;
            }

            private boolean isAdmin(long employeeId, Set<Long> adminRoles, QueryTransaction transaction) throws PlatformException {
                HashFilter filter = new HashFilter(EmployeeAccessRoleReadable.FIELD_EMPLOYEE_ID, employeeId);
                try (IteratorEntity<EmployeeAccessRoleReadable> ie = employeeAccessRoleReadableResource.findAll(filter, transaction)) {
                    while (ie.hasNext()) {
                        if (adminRoles.contains(ie.next().getAccessRoleId())) {
                            return true;
                        }
                    }
                }
                return false;
            }
        };
        return new GAccessQuery<>(query, CorePrivilege.EMPLOYEES, AccessOperation.DELETE);
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Объединение сотрудников")
    public static GraphQLQuery<RemoteObject, Boolean> merge(
            @NonNull @GraphQLName("main_employee_id")
            @GraphQLDescription("Идентификатор основного сотрудника") final Long mainEmployeeId,
            @NonNull @GraphQLName("secondary_employee_ids")
            @GraphQLDescription("Идентификаторы сотрудников, объединяемых с основным") final HashSet<Long> secondaryEmployeeIds
    ) {
        GraphQLQuery<RemoteObject, Boolean> query = new GraphQLQuery<RemoteObject, Boolean>() {

            private RControllerEmployeeControl rControllerEmployeeControl;
            private ManagerEmployeeAccessGetter managerEmployeeAccessGetter;

            @Override
            public void prepare(ResourceProvider resources) {
                rControllerEmployeeControl =
                        resources.getQueryRemoteController(CoreSubsystem.class, RControllerEmployeeControl.class);
                managerEmployeeAccessGetter = new ManagerEmployeeAccessGetter(resources);
            }

            @Override
            public Boolean execute(RemoteObject source, ContextTransactionRequest context) throws PlatformException {
                ManagerEmployeeAccess access = null;
                UnauthorizedContext authContext = context.getSource().getAuthContext();
                if (authContext instanceof EmployeeAuthContext) {
                    long employeeId = ((EmployeeAuthContext) authContext).getEmployeeId();
                    if (secondaryEmployeeIds.contains(employeeId) && !Objects.equals(employeeId, mainEmployeeId)) {
                        throw CoreExceptionBuilder.buildEmployeeSelfMergerException(employeeId);
                    }
                    access = managerEmployeeAccessGetter.getAccess(employeeId, context.getTransaction());
                }
                HashSet<Long> checkedSecondaryEmployeeIds;
                if (access != null) {
                    if (!access.checkEmployee(mainEmployeeId)) {
                        throw GeneralExceptionBuilder.buildAccessDeniedException();
                    }
                    checkedSecondaryEmployeeIds = secondaryEmployeeIds.stream()
                            .filter(access::checkEmployee).collect(Collectors.toCollection(HashSet::new));
                } else {
                    checkedSecondaryEmployeeIds = secondaryEmployeeIds;
                }
                rControllerEmployeeControl.merge(mainEmployeeId, checkedSecondaryEmployeeIds, context);
                return true;
            }
        };
        return new GAccessQuery<>(query, CorePrivilege.EMPLOYEES, AccessOperation.WRITE);
    }


    @GraphQLField
    @GraphQLAuthControl(UnauthorizedContext.class)
    @GraphQLDescription("Сброс пароля")
    public static GraphQLQuery<RemoteObject, Boolean> restorePassword(
            CoreSubsystem coreSubsystem,
            @NonNull @GraphQLName(LOGIN)
            @GraphQLDescription("Логин сотрудника") final String login
    ) {
        return new GraphQLQuery<RemoteObject, Boolean>() {
            private RControllerHelpDeskGetterWrapper helpDeskGetterWrapper;
            private RControllerMailSenderWrapper mailSenderWrapper;
            private ReadableResource<EmployeeReadable> employeeReadableResource;
            private RControllerFrontendInfo rControllerFrontendInfo;
            private RControllerEmployeeLogon rControllerEmployeeLogon;
            private RCEmployeeAuthenticationChecker rcEmployeeAuthenticationChecker;
            private LanguageGetter languageGetter;
            private EmployeeTokenManager<?> employeeTokenManager;


            @Override
            public void prepare(ResourceProvider resources) {
                employeeReadableResource = resources.getReadableResource(EmployeeReadable.class);
                rControllerFrontendInfo =
                        resources.getQueryRemoteController(FrontendSubsystem.class, RControllerFrontendInfo.class);
                rControllerEmployeeLogon =
                        resources.getQueryRemoteController(CoreSubsystem.class, RControllerEmployeeLogon.class);
                rcEmployeeAuthenticationChecker =
                        resources.getQueryRemoteController(CoreSubsystem.class, RCEmployeeAuthenticationChecker.class);
                languageGetter = new LanguageGetter(resources);
                employeeTokenManager = EmployeeTokenManagerFactory.newEmployeeTokenRestoreAccessManager(coreSubsystem, resources);
                mailSenderWrapper = coreSubsystem.getRemotes().get(CoreSubsystem.class, RControllerMailSenderWrapper.class);
                helpDeskGetterWrapper = coreSubsystem.getRemotes().get(CoreSubsystem.class, RControllerHelpDeskGetterWrapper.class);
            }

            @Override
            public Boolean execute(RemoteObject source, ContextTransactionRequest context) throws PlatformException {
                QueryTransaction transaction = context.getTransaction();
                EmployeeReadable employee = getEmployeeByLogin(login, rControllerEmployeeLogon.getLogonType(), employeeReadableResource, transaction);
                if (employee != null && employee.getEmail() != null
                        && rcEmployeeAuthenticationChecker.isAssigned(
                        CoreSubsystemConsts.AuthenticationTypes.INTEGRATED, employee.getId(), context)) {
                    EmployeeTokenReadable employeeToken = employeeTokenManager.createToken(employee.getId(), transaction);
                    Language language = languageGetter.get(employee, transaction);
                    String serverUrl = rControllerFrontendInfo.getServerUrl();
                    HelpDeskConfig helpDeskConfig = helpDeskGetterWrapper.getConfig();
                    if (helpDeskConfig == null) {
                        throw GeneralExceptionBuilder.buildIncorrectMailTemplate();
                    }

                    PasswordRecoveryMessage message = PasswordRecoveryMessage.newInstance(coreSubsystem, language, helpDeskConfig);
                    mailSenderWrapper.sendMail(
                            new Mail()
                                    .withRecipients(employee.getEmail())
                                    .withTitle(message.getTitle())
                                    .withContentType(message.getContentType())
                                    .withBody(message.getBody(serverUrl, employeeToken.getToken()))
                                    .appendEmbeddedImageAttachments(message.getEmbeddedImageAttachments())
                                    .appendFileAttachments(message.getFileAttachments())
                    );
                }
                return true;
            }
        };
    }

    @GraphQLField
    @GraphQLAuthControl(UnauthorizedContext.class)
    @GraphQLDescription("Валидация токена восстановления пароля")
    public static GraphQLQuery<RemoteObject, Boolean> validateRestorePasswordToken(
            CoreSubsystem coreSubsystem,
            @NonNull @GraphQLName(TOKEN)
            @GraphQLDescription("Токен восстановления пароля") final String token
    ) {
        return new GraphQLQuery<RemoteObject, Boolean>() {

            private RCEmployeeAuthenticationChecker rcEmployeeAuthenticationChecker;
            private EmployeeTokenManager<?> employeeTokenManager;

            @Override
            public void prepare(ResourceProvider resources) {
                rcEmployeeAuthenticationChecker =
                        resources.getQueryRemoteController(CoreSubsystem.class, RCEmployeeAuthenticationChecker.class);
                employeeTokenManager = EmployeeTokenManagerFactory.newEmployeeTokenRestoreAccessManager(coreSubsystem, resources);
            }

            @Override
            public Boolean execute(RemoteObject source, ContextTransactionRequest context) throws PlatformException {
                EmployeeTokenReadable employeeToken = employeeTokenManager.getByToken(token, context.getTransaction());
                return employeeToken != null && rcEmployeeAuthenticationChecker.isAssigned(
                        CoreSubsystemConsts.AuthenticationTypes.INTEGRATED, employeeToken.getEmployeeId(), context);
            }
        };
    }

    @GraphQLField
    @GraphQLAuthControl(UnauthorizedContext.class)
    @GraphQLDescription("Сброс пароля")
    public static GraphQLQuery<RemoteObject, String> resetPassword(
            CoreSubsystem coreSubsystem,
            @NonNull @GraphQLName(TOKEN)
            @GraphQLDescription("Токен восстановления пароля") final String token,
            @NonNull @GraphQLName(PASSWORD_HASH)
            @GraphQLDescription("Хеш нового пароля") final String newPasswordHash
    ) {
        return new GraphQLQuery<RemoteObject, String>() {

            private RControllerEmployeeControl rControllerEmployeeControl;
            private RCEmployeeAuthenticationChecker rcEmployeeAuthenticationChecker;
            private EmployeeTokenManager<?> employeeTokenManager;

            @Override
            public void prepare(ResourceProvider resources) {
                rControllerEmployeeControl =
                        resources.getQueryRemoteController(CoreSubsystem.class, RControllerEmployeeControl.class);
                rcEmployeeAuthenticationChecker =
                        resources.getQueryRemoteController(CoreSubsystem.class, RCEmployeeAuthenticationChecker.class);
                employeeTokenManager = EmployeeTokenManagerFactory.newEmployeeTokenRestoreAccessManager(
                        coreSubsystem, resources);
            }

            @Override
            public String execute(RemoteObject source, ContextTransactionRequest context) throws PlatformException {
                EmployeeTokenReadable employeeToken = employeeTokenManager.removeToken(token, context.getTransaction());
                if (employeeToken == null) {
                    throw GeneralExceptionBuilder.buildPasswordRecoveryLinkExpired();
                }
                if (!rcEmployeeAuthenticationChecker.isAssigned(
                        CoreSubsystemConsts.AuthenticationTypes.INTEGRATED, employeeToken.getEmployeeId(), context)) {
                    throw CoreExceptionBuilder.buildIntegratedAuthenticationNotFoundException();
                }
                EmployeeReadable employee = rControllerEmployeeControl.update(
                        employeeToken.getEmployeeId(),
                        new EmployeeBuilder()
                                .withPasswordHash(newPasswordHash)
                                .withNeedToChangePassword(false),
                        context);
                logPasswordChange(ChangePasswordCause.RESET_PASSWORD, employee, context);
                return getLogonValue(employee, coreSubsystem);
            }
        };
    }

    @GraphQLField
    @GraphQLAuthControl(EmployeeAuthContext.class)
    @GraphQLDescription("Отправка приглашения")
    public static GraphQLQuery<RemoteObject, Boolean> sendInvitation(
            CoreSubsystem coreSubsystem,
            @NonNull @GraphQLName(ID)
            @GraphQLDescription("Идентификатор сотрудника") final long employeeId
    ) {
        GraphQLQuery<RemoteObject, Boolean> query = new GraphQLQuery<RemoteObject, Boolean>() {

            private RControllerHelpDeskGetterWrapper helpDeskGetterWrapper;
            private RControllerMailSenderWrapper mailSenderWrapper;
            private ReadableResource<EmployeeReadable> employeeReadableResource;
            private RControllerFrontendInfo rControllerFrontendInfo;
            private RCEmployeeAuthenticationChecker rcEmployeeAuthenticationChecker;
            private LanguageGetter languageGetter;
            private EmployeeTokenManager<?> employeeTokenManager;
            private ManagerEmployeeAccessGetter managerEmployeeAccessGetter;
            private EmployeeDisplayNameForMessageBuilder employeeDisplayNameBuilder;


            @Override
            public void prepare(ResourceProvider resources) {
                employeeReadableResource = resources.getReadableResource(EmployeeReadable.class);
                rControllerFrontendInfo =
                        resources.getQueryRemoteController(FrontendSubsystem.class, RControllerFrontendInfo.class);
                rcEmployeeAuthenticationChecker =
                        resources.getQueryRemoteController(CoreSubsystem.class, RCEmployeeAuthenticationChecker.class);
                languageGetter = new LanguageGetter(resources);
                employeeTokenManager = EmployeeTokenManagerFactory.newEmployeeInvitationTokenManager(resources);
                managerEmployeeAccessGetter = new ManagerEmployeeAccessGetter(resources);
                employeeDisplayNameBuilder = new EmployeeDisplayNameForMessageBuilder(resources);
                mailSenderWrapper = coreSubsystem.getRemotes().get(CoreSubsystem.class, RControllerMailSenderWrapper.class);
                helpDeskGetterWrapper = coreSubsystem.getRemotes().get(CoreSubsystem.class, RControllerHelpDeskGetterWrapper.class);
            }

            @Override
            public Boolean execute(RemoteObject source, ContextTransactionRequest context)
                    throws PlatformException {
                QueryTransaction transaction = context.getTransaction();
                EmployeeReadable employee = employeeReadableResource.get(employeeId, transaction);
                if (employee == null) {
                    throw GeneralExceptionBuilder.buildNotFoundDomainObjectException(EmployeeReadable.class, employeeId);
                }
                if (!rcEmployeeAuthenticationChecker.isAssigned(
                        CoreSubsystemConsts.AuthenticationTypes.INTEGRATED, employeeId, context)) {
                    throw CoreExceptionBuilder.buildIntegratedAuthenticationNotFoundException();
                }
                if (StringUtils.isEmpty(employee.getEmail())) {
                    throw CoreExceptionBuilder.buildEmptyEmailException();
                }
                Language language = languageGetter.get(employee, transaction);
                HelpDeskConfig helpDeskConfig = helpDeskGetterWrapper.getConfig();
                if (Objects.nonNull(helpDeskConfig)) {
                    InvitationMessage messageTemplate = InvitationMessage.newInstance(coreSubsystem, language, helpDeskConfig);
                    String serverUrl = rControllerFrontendInfo.getServerUrl();
                    EmployeeTokenReadable employeeToken = employeeTokenManager.createToken(employee.getId(), transaction);
                    String employeeDisplayName = employeeDisplayNameBuilder.build(employee, transaction);
                    UnauthorizedContext authContext = context.getSource().getAuthContext();
                    long managerId = ((EmployeeAuthContext) authContext).getEmployeeId();
                    ManagerEmployeeAccess access = managerEmployeeAccessGetter.getAccess(managerId, context.getTransaction());
                    if (!access.checkEmployee(employeeId)) {
                        throw GeneralExceptionBuilder.buildAccessDeniedException();
                    }
                    mailSenderWrapper.sendMailAsync(
                            new Mail()
                                    .withRecipients(employee.getEmail())
                                    .withTitle(messageTemplate.getTitle())
                                    .withContentType(messageTemplate.getContentType())
                                    .withBody(messageTemplate.getBody(
                                            getLogonValue(employee, coreSubsystem), employeeDisplayName, serverUrl, employeeToken.getToken(), createInvitationTimeout(employeeToken)))
                                    .appendEmbeddedImageAttachments(messageTemplate.getEmbeddedImageAttachments())
                                    .appendFileAttachments(messageTemplate.getFileAttachments()));
                }
                return true;
            }

            private String createInvitationTimeout(EmployeeTokenReadable employeeToken) {
                Instant creationTime = employeeToken.getCreationTime();
                ;
                return LocalDateTime.ofInstant(creationTime, ZoneId.systemDefault()).plusDays(INVITATION_TOKEN_EXPIRATION_DAY)
                        .format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
            }
        };
        return new GAccessQuery<>(query, CorePrivilege.EMPLOYEE_ACCESS, AccessOperation.EXECUTE);
    }

    @GraphQLField
    @GraphQLAuthControl(UnauthorizedContext.class)
    @GraphQLDescription("Валидация токена приглашения")
    public static GraphQLQuery<RemoteObject, Boolean> validateInvitationToken(
            @NonNull @GraphQLName(TOKEN)
            @GraphQLDescription("Токен приглашения") final String token
    ) {
        return new GraphQLQuery<RemoteObject, Boolean>() {

            private RCEmployeeAuthenticationChecker rcEmployeeAuthenticationChecker;
            private EmployeeTokenManager<?> employeeTokenManager;

            @Override
            public void prepare(ResourceProvider resources) {
                rcEmployeeAuthenticationChecker =
                        resources.getQueryRemoteController(CoreSubsystem.class, RCEmployeeAuthenticationChecker.class);
                employeeTokenManager = EmployeeTokenManagerFactory.newEmployeeInvitationTokenManager(resources);
            }

            @Override
            public Boolean execute(RemoteObject source, ContextTransactionRequest context)
                    throws PlatformException {
                EmployeeTokenReadable employeeToken = employeeTokenManager.getByToken(token, context.getTransaction());
                return employeeToken != null && rcEmployeeAuthenticationChecker.isAssigned(
                        CoreSubsystemConsts.AuthenticationTypes.INTEGRATED, employeeToken.getEmployeeId(), context);
            }
        };
    }

    @GraphQLField
    @GraphQLAuthControl(UnauthorizedContext.class)
    @GraphQLDescription("Активация приглашения")
    public static GraphQLQuery<RemoteObject, String> applyInvitation(
            CoreSubsystem coreSubsystem,
            @NonNull @GraphQLName(TOKEN)
            @GraphQLDescription("Токен приглашения") final String token,
            @NonNull @GraphQLName(PASSWORD_HASH)
            @GraphQLDescription("Хеш пароля") final String passwordHash,
            @NonNull @GraphQLName(LANGUAGE)
            @GraphQLDescription("Язык") final Language language
    ) {
        return new GraphQLQuery<RemoteObject, String>() {

            private RControllerEmployeeControl rControllerEmployeeControl;
            private RCEmployeeAuthenticationChecker rcEmployeeAuthenticationChecker;
            private EmployeeTokenManager<?> employeeTokenManager;

            @Override
            public void prepare(ResourceProvider resources) {
                rControllerEmployeeControl =
                        resources.getQueryRemoteController(CoreSubsystem.class, RControllerEmployeeControl.class);
                rcEmployeeAuthenticationChecker =
                        resources.getQueryRemoteController(CoreSubsystem.class, RCEmployeeAuthenticationChecker.class);
                employeeTokenManager = EmployeeTokenManagerFactory.newEmployeeInvitationTokenManager(resources);
            }

            @Override
            public String execute(RemoteObject source, ContextTransactionRequest context) throws PlatformException {
                EmployeeTokenReadable employeeToken = employeeTokenManager.removeToken(token, context.getTransaction());
                if (employeeToken == null) {
                    throw GeneralExceptionBuilder.buildInvitationLinkExpired();
                }
                if (!rcEmployeeAuthenticationChecker.isAssigned(
                        CoreSubsystemConsts.AuthenticationTypes.INTEGRATED, employeeToken.getEmployeeId(), context)) {
                    throw CoreExceptionBuilder.buildIntegratedAuthenticationNotFoundException();
                }
                EmployeeBuilder employeeBuilder = new EmployeeBuilder()
                        .withPasswordHash(passwordHash)
                        .withLanguage(language)
                        .withNeedToChangePassword(false);
                EmployeeReadable employee =
                        rControllerEmployeeControl.update(employeeToken.getEmployeeId(), employeeBuilder, context);
                logPasswordChange(ChangePasswordCause.SET_PASSWORD_BY_INVITATION, employee, context);
                return getLogonValue(employee, coreSubsystem);
            }
        };
    }

    @GraphQLField(value = "set_access_role")
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Установка ролей доступа сотрудникам")
    public static GraphQLQuery<RemoteObject, Boolean> setAccessRole(
            @GraphQLName(TARGET_ALL)
            @GraphQLDescription("Флаг обновления всех сотрудников") final boolean targetAll,
            @GraphQLName(TARGET_DEPARTMENT_IDS)
            @GraphQLDescription("Идентификаторы отделов, сотрудники которых обновляются") final HashSet<Long> targetDepartmentIds,
            @GraphQLName(TARGET_EMPLOYEE_IDS)
            @GraphQLDescription("Идентификаторы обновляемых сотрудников") final HashSet<Long> targetEmployeeIds,
            @GraphQLDescription("Новые значения идентификаторов ролей доступа")
            @NonNull @GraphQLName(ACCESS_ROLE_IDS) final HashSet<Long> accessRoleIds
    ) {
        GraphQLQuery<RemoteObject, Boolean> query = new GraphQLQuery<RemoteObject, Boolean>() {

            private ReadableResource<AccessRoleReadable> accessRoleReadableResource;
            private RControllerAccessRole rControllerAccessRole;
            private EmployeeFilterProcessor employeeFilterProcessor;

            @Override
            public void prepare(ResourceProvider resources) {
                accessRoleReadableResource = resources.getReadableResource(AccessRoleReadable.class);
                rControllerAccessRole =
                        resources.getQueryRemoteController(CoreSubsystem.class, RControllerAccessRole.class);
                employeeFilterProcessor = new EmployeeFilterProcessor(resources);
            }

            @Override
            public Boolean execute(RemoteObject source, ContextTransactionRequest context)
                    throws PlatformException {
                QueryTransaction transaction = context.getTransaction();
                Set<Long> targetEmployees = employeeFilterProcessor.getEmployees(
                        targetAll,
                        TARGET_ALL,
                        targetDepartmentIds,
                        targetEmployeeIds,
                        context
                );
                for (Long employeeId : targetEmployees) {
                    for (Long accessRoleId : accessRoleIds) {
                        if (accessRoleId != null && accessRoleReadableResource.get(accessRoleId, transaction) != null) {
                            rControllerAccessRole.assignAccessRoleToEmployee(
                                    accessRoleId,
                                    employeeId,
                                    context
                            );
                        }
                    }
                }
                return true;
            }
        };
        return new GAccessQuery<>(query, CorePrivilege.EMPLOYEE_ACCESS, AccessOperation.WRITE);
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Сброс ролей доступа у сотрудников")
    public static GraphQLQuery<RemoteObject, Boolean> resetAccessRole(
            @GraphQLName(TARGET_ALL)
            @GraphQLDescription("Флаг обновления всех сотрудников") final boolean targetAll,
            @GraphQLName(TARGET_DEPARTMENT_IDS)
            @GraphQLDescription("Идентификаторы отделов, сотрудники которых обновляются") final HashSet<Long> targetDepartmentIds,
            @GraphQLName(TARGET_EMPLOYEE_IDS)
            @GraphQLDescription("Идентификаторы обновляемых сотрудников") final HashSet<Long> targetEmployeeIds,
            @NonNull @GraphQLName(ACCESS_ROLE_IDS)
            @GraphQLDescription("Идентификаторы сбрасываемых базовых ролей доступа") final HashSet<Long> accessRoleIds
    ) {
        GraphQLQuery<RemoteObject, Boolean> query = new GraphQLQuery<RemoteObject, Boolean>() {

            private ReadableResource<AccessRoleReadable> accessRoleReadableResource;
            private RControllerAccessRole rControllerAccessRole;
            private EmployeeFilterProcessor employeeFilterProcessor;

            @Override
            public void prepare(ResourceProvider resources) {
                accessRoleReadableResource = resources.getReadableResource(AccessRoleReadable.class);
                rControllerAccessRole =
                        resources.getQueryRemoteController(CoreSubsystem.class, RControllerAccessRole.class);
                employeeFilterProcessor = new EmployeeFilterProcessor(resources);
            }

            @Override
            public Boolean execute(RemoteObject source, ContextTransactionRequest context)
                    throws PlatformException {
                QueryTransaction transaction = context.getTransaction();
                Set<Long> targetEmployees = employeeFilterProcessor.getEmployees(
                        targetAll,
                        TARGET_ALL,
                        targetDepartmentIds,
                        targetEmployeeIds,
                        context
                );
                for (Long employeeId : targetEmployees) {
                    for (Long accessRoleId : accessRoleIds) {
                        if (accessRoleId != null && accessRoleReadableResource.get(accessRoleId, transaction) != null) {
                            rControllerAccessRole.eraseAccessRoleForEmployee(accessRoleId, employeeId, context);
                        }
                    }
                }
                return true;
            }
        };
        return new GAccessQuery<>(query, CorePrivilege.EMPLOYEE_ACCESS, AccessOperation.WRITE);
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Замена ролей доступа у сотрудников")
    public static GraphQLQuery<RemoteObject, Boolean> replaceAccessRole(
            @GraphQLName(TARGET_ALL)
            @GraphQLDescription("Флаг обновления всех сотрудников") final boolean targetAll,
            @GraphQLName(TARGET_DEPARTMENT_IDS)
            @GraphQLDescription("Идентификаторы отделов, сотрудники которых обновляются") final HashSet<Long> targetDepartmentIds,
            @GraphQLName(TARGET_EMPLOYEE_IDS)
            @GraphQLDescription("Идентификаторы обновляемых сотрудников") final HashSet<Long> targetEmployeeIds,
            @NonNull @GraphQLName(ACCESS_ROLE_IDS)
            @GraphQLDescription("Новые значения идентификаторов ролей доступа") final HashSet<Long> accessRoleIds
    ) {
        GraphQLQuery<RemoteObject, Boolean> query = new GraphQLQuery<RemoteObject, Boolean>() {

            private ReadableResource<AccessRoleReadable> accessRoleReadableResource;
            private RControllerAccessRole rControllerAccessRole;
            private EmployeeFilterProcessor employeeFilterProcessor;

            @Override
            public void prepare(ResourceProvider resources) {
                accessRoleReadableResource = resources.getReadableResource(AccessRoleReadable.class);
                rControllerAccessRole =
                        resources.getQueryRemoteController(CoreSubsystem.class, RControllerAccessRole.class);
                employeeFilterProcessor = new EmployeeFilterProcessor(resources);
            }

            @Override
            public Boolean execute(RemoteObject source, ContextTransactionRequest context)
                    throws PlatformException {
                QueryTransaction transaction = context.getTransaction();
                Set<Long> targetEmployees = employeeFilterProcessor.getEmployees(
                        targetAll,
                        TARGET_ALL,
                        targetDepartmentIds,
                        targetEmployeeIds,
                        context
                );
                HashSet<Long> validAccessRoleIds = new PrimaryKeyValidator(true).validate(
                        accessRoleIds, accessRoleReadableResource, transaction);
                for (Long employeeId : targetEmployees) {
                    rControllerAccessRole.replaceAccessRolesAtEmployee(validAccessRoleIds, employeeId, context);
                }

                return true;
            }
        };
        return new GAccessQuery<>(query, CorePrivilege.EMPLOYEE_ACCESS, AccessOperation.WRITE);
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Настройка доступа к сотрудникам")
    public static GraphQLQuery<RemoteObject, Boolean> setEmployeeAccess(
            CoreSubsystem component,
            @NonNull @GraphQLName(EMPLOYEE_ID)
            @GraphQLDescription("Идентификатор сотрудника, для которого настраивается доступ") final Long employeeId,
            @GraphQLName(ALL)
            @GraphQLDescription("Доступны все сотрудники") final GOptional<Boolean> all,
            @GraphQLName(INSERTED_DEPARTMENTS)
            @GraphQLDescription("Идентификаторы доступных отделов") final GOptional<HashSet<Long>> insertedDepartments,
            @GraphQLName(INSERTED_EMPLOYEES)
            @GraphQLDescription("Идентификаторы доступных сотрудников") final GOptional<HashSet<Long>> insertedEmployees,
            @GraphQLName(REMOVED_DEPARTMENTS)
            @GraphQLDescription("Идентификаторы недоступных отделов") final GOptional<HashSet<Long>> removedDepartments,
            @GraphQLName(REMOVED_EMPLOYEES)
            @GraphQLDescription("Идентификаторы недоступных сотрудников") final GOptional<HashSet<Long>> removedEmployees
    ) {
        GraphQLQuery<RemoteObject, Boolean> query = new GraphQLQuery<RemoteObject, Boolean>() {

            private ManagerEmployeeAccessSetter managerEmployeeAccessSetter;
            private ManagerEmployeeAccessSecurityLogger managerEmployeeAccessSecurityLogger;
            private ManagerEmployeeAccessGetter managerEmployeeAccessGetter;

            @Override
            public void prepare(ResourceProvider resources) {
                managerEmployeeAccessSetter = new ManagerEmployeeAccessSetter(component, resources);
                managerEmployeeAccessSecurityLogger = new ManagerEmployeeAccessSecurityLogger(resources);
                managerEmployeeAccessGetter = new ManagerEmployeeAccessGetter(resources);
            }

            @Override
            public Boolean execute(RemoteObject source, ContextTransactionRequest context) throws PlatformException {
                UnauthorizedContext authContext = context.getSource().getAuthContext();
                ManagerEmployeeAccess access = null;
                if (authContext instanceof EmployeeAuthContext) {
                    long currentEmployeeId = ((EmployeeAuthContext) authContext).getEmployeeId();
                    access = managerEmployeeAccessGetter.getAccess(currentEmployeeId, context.getTransaction());
                    if (!access.checkEmployee(employeeId)) {
                        throw GeneralExceptionBuilder.buildAccessDeniedException();
                    }
                }
                managerEmployeeAccessSecurityLogger.beforeAccessModifications(employeeId, context);
                if (all.isPresent() && all.get()) {
                    if (access != null && !access.isAll()) {
                        throw GeneralExceptionBuilder.buildAccessDeniedException();
                    }
                    managerEmployeeAccessSetter.setAll(employeeId, context.getTransaction());
                } else {
                    managerEmployeeAccessSetter.set(
                            employeeId,
                            insertedDepartments.isPresent() ? getAccessibleDepartments(insertedDepartments.get(), access) : null,
                            insertedEmployees.isPresent() ? getAccessibleEmployees(insertedEmployees.get(), access) : null,
                            removedDepartments.isPresent() ? getAccessibleDepartments(removedDepartments.get(), access) : null,
                            removedEmployees.isPresent() ? getAccessibleEmployees(removedEmployees.get(), access) : null,
                            context.getTransaction()
                    );
                }
                managerEmployeeAccessSecurityLogger.afterAccessModifications(employeeId, context);
                return true;
            }
        };
        return new GAccessQuery<>(query, CorePrivilege.EMPLOYEE_ACCESS, AccessOperation.WRITE);
    }

    @GraphQLField(value = "set_data_for_current_employee")
    @GraphQLAuthControl({ EmployeeAuthContext.class })
    @GraphQLDescription("Сохранение данных для текущего сотрудника")
    public static GraphQLQuery<RemoteObject, Boolean> setDataForCurrentEmployee(
            @GraphQLDescription("Данные")
            @NonNull @GraphQLName("data") final ArrayList<GInputKeyValue> data
    ) {
        return new GraphQLQuery<RemoteObject, Boolean>() {

            private EditableResource<EmployeeDataEditable> employeeDataEditableResource;

            @Override
            public void prepare(ResourceProvider resources) {
                employeeDataEditableResource = resources.getEditableResource(EmployeeDataEditable.class);
            }

            @Override
            public Boolean execute(RemoteObject source, ContextTransactionRequest context) throws PlatformException {
                QueryTransaction transaction = context.getTransaction();
                long employeeId = ((EmployeeAuthContext) context.getSource().getAuthContext()).getEmployeeId();
                for (GInputKeyValue keyValue : data) {
                    if (StringUtils.isEmpty(keyValue.getKey())) {
                        throw GeneralExceptionBuilder.buildEmptyValueException("key");
                    }
                    HashFilter filter = new HashFilter(EmployeeDataReadable.FIELD_EMPLOYEE_ID, employeeId)
                            .appendField(EmployeeDataReadable.FIELD_KEY, keyValue.getKey());
                    EmployeeDataEditable employeeData = employeeDataEditableResource.find(filter, transaction);
                    if (employeeData == null) {
                        employeeData = employeeDataEditableResource.create(transaction);
                        employeeData.setEmployeeId(employeeId);
                        employeeData.setKey(keyValue.getKey());
                    }
                    employeeData.setValue(keyValue.getValue());
                    employeeDataEditableResource.save(employeeData, transaction);
                }
                return true;
            }
        };
    }

    @GraphQLField()
    @GraphQLAuthControl({ EmployeeAuthContext.class })
    @GraphQLDescription("Очистка данных для текущего сотрудника")
    public static GraphQLQuery<RemoteObject, Boolean> clearDataForCurrentEmployee(
            @GraphQLDescription("Ключи, которые не нужно удалять")
            @NonNull @GraphQLName("exclusion_keys") final HashSet<String> exclusionKeys
    ) {
        return new GraphQLQuery<RemoteObject, Boolean>() {

            private RemovableResource<EmployeeDataEditable> employeeDataRemovableResource;

            @Override
            public void prepare(ResourceProvider resources) {
                employeeDataRemovableResource = resources.getRemovableResource(EmployeeDataEditable.class);
            }

            @Override
            public Boolean execute(RemoteObject source, ContextTransactionRequest context) throws PlatformException {
                QueryTransaction transaction = context.getTransaction();
                long employeeId = ((EmployeeAuthContext) context.getSource().getAuthContext()).getEmployeeId();
                HashFilter filter = new HashFilter(EmployeeDataReadable.FIELD_EMPLOYEE_ID, employeeId);
                employeeDataRemovableResource.forEach(filter, employeeData -> {
                    if (!exclusionKeys.contains(employeeData.getKey())) {
                        employeeDataRemovableResource.remove(employeeData, transaction);
                    }
                }, transaction);
                return true;
            }
        };
    }

    @GraphQLField()
    @GraphQLAuthControl({ EmployeeAuthContext.class })
    @GraphQLDescription("Обновление поля с типом данных \"Число\"")
    public static GraphQLQuery<RemoteObject, GEmployee> updateAdditionalFieldLongValue(
            @GraphQLDescription("Идентификатор сотрудника")
            @NonNull @GraphQLName(ID) final long employeeId,
            @GraphQLDescription("Идентификатор дополнительного поля")
            @NonNull @GraphQLName(ADDITIONAL_FIELD_ID) final long additionalFieldId,
            @GraphQLDescription("Значение")
            @GraphQLName(VALUE) final Long value
    ) {
        GraphQLQuery<RemoteObject, GEmployee> query = new AdditionalFieldValueUpdateQuery<Long>(
                employeeId, additionalFieldId, value, FieldDataType.LONG) {

            @Override
            protected void setValue(RCAdditionalFieldValueSetter rcAdditionalFieldValueSetter,
                                    long additionalFieldId,
                                    long objectId,
                                    Long value,
                                    ContextTransaction<?> context) throws PlatformException {
                rcAdditionalFieldValueSetter.setLongValue(additionalFieldId, employeeId, 0, value, context);
            }
        };
        return new GAccessQuery<>(query, CorePrivilege.EMPLOYEES, AccessOperation.WRITE);
    }

    @GraphQLField()
    @GraphQLAuthControl({ EmployeeAuthContext.class })
    @GraphQLDescription("Обновление поля с типом данных \"ID\"")
    public static GraphQLQuery<RemoteObject, GEmployee> updateAdditionalFieldIdValue(
            @GraphQLDescription("Идентификатор сотрудника")
            @NonNull @GraphQLName(ID) final long employeeId,
            @GraphQLDescription("Идентификатор дополнительного поля")
            @NonNull @GraphQLName(ADDITIONAL_FIELD_ID) final long additionalFieldId,
            @GraphQLDescription("Значение")
            @GraphQLName(VALUE) final Long value
    ) {
        GraphQLQuery<RemoteObject, GEmployee> query = new AdditionalFieldValueUpdateQuery<Long>(
                employeeId, additionalFieldId, value, FieldDataType.ID) {

            @Override
            protected void setValue(RCAdditionalFieldValueSetter rcAdditionalFieldValueSetter,
                                    long additionalFieldId,
                                    long objectId,
                                    Long value,
                                    ContextTransaction<?> context) throws PlatformException {
                rcAdditionalFieldValueSetter.setIdValue(additionalFieldId, employeeId, 0, value, context);
            }
        };
        return new GAccessQuery<>(query, CorePrivilege.EMPLOYEES, AccessOperation.WRITE);
    }

    @GraphQLField()
    @GraphQLAuthControl({ EmployeeAuthContext.class })
    @GraphQLDescription("Обновление поля с типом данных \"Строка\"")
    public static GraphQLQuery<RemoteObject, GEmployee> updateAdditionalFieldStringValue(
            @GraphQLDescription("Идентификатор сотрудника")
            @NonNull @GraphQLName(ID) final long employeeId,
            @GraphQLDescription("Идентификатор дополнительного поля")
            @NonNull @GraphQLName(ADDITIONAL_FIELD_ID) final long additionalFieldId,
            @GraphQLDescription("Значение")
            @GraphQLName(VALUE) final String value
    ) {
        GraphQLQuery<RemoteObject, GEmployee> query = new AdditionalFieldValueUpdateQuery<String>(
                employeeId, additionalFieldId, value, FieldDataType.STRING) {

            @Override
            protected void setValue(RCAdditionalFieldValueSetter rcAdditionalFieldValueSetter,
                                    long additionalFieldId,
                                    long objectId,
                                    String value,
                                    ContextTransaction<?> context) throws PlatformException {
                rcAdditionalFieldValueSetter.setStringValue(additionalFieldId, employeeId, 0, value, context);
            }
        };
        return new GAccessQuery<>(query, CorePrivilege.EMPLOYEES, AccessOperation.WRITE);
    }

    @GraphQLField()
    @GraphQLAuthControl({ EmployeeAuthContext.class })
    @GraphQLDescription("Обновление поля с типом данных \"Дата\"")
    public static GraphQLQuery<RemoteObject, GEmployee> updateAdditionalFieldDateValue(
            @GraphQLDescription("Идентификатор сотрудника")
            @NonNull @GraphQLName(ID) final long employeeId,
            @GraphQLDescription("Идентификатор дополнительного поля")
            @NonNull @GraphQLName(ADDITIONAL_FIELD_ID) final long additionalFieldId,
            @GraphQLDescription("Значение")
            @GraphQLName(VALUE) final GInputLocalDate value
    ) {
        GraphQLQuery<RemoteObject, GEmployee> query = new AdditionalFieldValueUpdateQuery<LocalDate>(
                employeeId, additionalFieldId, value != null ? value.getLocalDate() : null, FieldDataType.DATE) {

            @Override
            protected void setValue(RCAdditionalFieldValueSetter rcAdditionalFieldValueSetter,
                                    long additionalFieldId,
                                    long objectId,
                                    LocalDate value,
                                    ContextTransaction<?> context) throws PlatformException {
                rcAdditionalFieldValueSetter.setDateValue(additionalFieldId, employeeId, 0, value, context);
            }
        };
        return new GAccessQuery<>(query, CorePrivilege.EMPLOYEES, AccessOperation.WRITE);
    }

    @GraphQLField()
    @GraphQLAuthControl({ EmployeeAuthContext.class })
    @GraphQLDescription("Обновление поля с типом данных \"Дата и время\"")
    public static GraphQLQuery<RemoteObject, GEmployee> updateAdditionalFieldDateTimeValue(
            @GraphQLDescription("Идентификатор сотрудника")
            @NonNull @GraphQLName(ID) final long employeeId,
            @GraphQLDescription("Идентификатор дополнительного поля")
            @NonNull @GraphQLName(ADDITIONAL_FIELD_ID) final long additionalFieldId,
            @GraphQLDescription("Значение")
            @GraphQLName(VALUE) final GInputDateTime value
    ) {
        GraphQLQuery<RemoteObject, GEmployee> query = new AdditionalFieldValueUpdateQuery<Instant>(
                employeeId, additionalFieldId, value != null ? value.getInstant() : null, FieldDataType.DATETIME) {

            @Override
            protected void setValue(RCAdditionalFieldValueSetter rcAdditionalFieldValueSetter,
                                    long additionalFieldId,
                                    long objectId,
                                    Instant value,
                                    ContextTransaction<?> context) throws PlatformException {
                rcAdditionalFieldValueSetter.setDateTimeValue(additionalFieldId, employeeId, 0, value, context);
            }
        };
        return new GAccessQuery<>(query, CorePrivilege.EMPLOYEES, AccessOperation.WRITE);
    }

    @GraphQLField()
    @GraphQLAuthControl({ EmployeeAuthContext.class })
    @GraphQLDescription("Обновление поля с типом данных \"Число (Массив)\"")
    public static GraphQLQuery<RemoteObject, GEmployee> updateAdditionalFieldLongArrayValue(
            @GraphQLDescription("Идентификатор сотрудника")
            @NonNull @GraphQLName(ID) final long employeeId,
            @GraphQLDescription("Идентификатор дополнительного поля")
            @NonNull @GraphQLName(ADDITIONAL_FIELD_ID) final long additionalFieldId,
            @GraphQLDescription("Значение")
            @GraphQLName(VALUE) final ArrayList<Long> value
    ) {
        GraphQLQuery<RemoteObject, GEmployee> query = new AdditionalFieldArrayUpdateQuery<Long>(
                employeeId, additionalFieldId, value, FieldDataType.LONG_ARRAY) {
            @Override
            protected void addItem(RCAdditionalFieldValueSetter rcAdditionalFieldValueSetter,
                                   long additionalFieldId,
                                   long objectId,
                                   Long item,
                                   ContextTransaction<?> context) throws PlatformException {
                rcAdditionalFieldValueSetter.addLongValue(additionalFieldId, employeeId, item, context);
            }
        };
        return new GAccessQuery<>(query, CorePrivilege.EMPLOYEES, AccessOperation.WRITE);
    }

    @GraphQLField()
    @GraphQLAuthControl({ EmployeeAuthContext.class })
    @GraphQLDescription("Обновление поля с типом данных \"Строка (Массив)\"")
    public static GraphQLQuery<RemoteObject, GEmployee> updateAdditionalFieldStringArrayValue(
            @GraphQLDescription("Идентификатор сотрудника")
            @NonNull @GraphQLName(ID) final long employeeId,
            @GraphQLDescription("Идентификатор дополнительного поля")
            @NonNull @GraphQLName(ADDITIONAL_FIELD_ID) final long additionalFieldId,
            @GraphQLDescription("Значение")
            @GraphQLName(VALUE) final ArrayList<String> value
    ) {
        GraphQLQuery<RemoteObject, GEmployee> query = new AdditionalFieldArrayUpdateQuery<String>(
                employeeId, additionalFieldId, value, FieldDataType.STRING_ARRAY) {
            @Override
            protected void addItem(RCAdditionalFieldValueSetter rcAdditionalFieldValueSetter,
                                   long additionalFieldId,
                                   long objectId,
                                   String item,
                                   ContextTransaction<?> context) throws PlatformException {
                rcAdditionalFieldValueSetter.addStringValue(additionalFieldId, employeeId, item, context);
            }
        };
        return new GAccessQuery<>(query, CorePrivilege.EMPLOYEES, AccessOperation.WRITE);
    }

    @GraphQLField()
    @GraphQLAuthControl({ EmployeeAuthContext.class })
    @GraphQLDescription("Обновление поля с типом данных \"Дата (Массив)\"")
    public static GraphQLQuery<RemoteObject, GEmployee> updateAdditionalFieldDateArrayValue(
            @GraphQLDescription("Идентификатор сотрудника")
            @NonNull @GraphQLName(ID) final long employeeId,
            @GraphQLDescription("Идентификатор дополнительного поля")
            @NonNull @GraphQLName(ADDITIONAL_FIELD_ID) final long additionalFieldId,
            @GraphQLDescription("Значение")
            @GraphQLName(VALUE) final ArrayList<GInputLocalDate> value
    ) {
        ArrayList<LocalDate> items = value == null ? null :
                value.stream().map(o -> o != null ? o.getLocalDate() : null).collect(Collectors.toCollection(ArrayList::new));
        GraphQLQuery<RemoteObject, GEmployee> query = new AdditionalFieldArrayUpdateQuery<LocalDate>(
                employeeId, additionalFieldId, items, FieldDataType.DATE_ARRAY) {
            @Override
            protected void addItem(RCAdditionalFieldValueSetter rcAdditionalFieldValueSetter,
                                   long additionalFieldId,
                                   long objectId,
                                   LocalDate item,
                                   ContextTransaction<?> context) throws PlatformException {
                rcAdditionalFieldValueSetter.addDateValue(additionalFieldId, employeeId, item, context);
            }
        };
        return new GAccessQuery<>(query, CorePrivilege.EMPLOYEES, AccessOperation.WRITE);
    }

    @GraphQLField()
    @GraphQLAuthControl({ EmployeeAuthContext.class })
    @GraphQLDescription("Обновление поля с типом данных \"Дата и время (Массив)\"")
    public static GraphQLQuery<RemoteObject, GEmployee> updateAdditionalFieldDateTimeArrayValue(
            @GraphQLDescription("Идентификатор сотрудника")
            @NonNull @GraphQLName(ID) final long employeeId,
            @GraphQLDescription("Идентификатор дополнительного поля")
            @NonNull @GraphQLName(ADDITIONAL_FIELD_ID) final long additionalFieldId,
            @GraphQLDescription("Значение")
            @GraphQLName(VALUE) final ArrayList<GInputDateTime> value
    ) {
        ArrayList<Instant> items = value == null ? null :
                value.stream().map(o -> o != null ? o.getInstant() : null).collect(Collectors.toCollection(ArrayList::new));
        GraphQLQuery<RemoteObject, GEmployee> query = new AdditionalFieldArrayUpdateQuery<Instant>(
                employeeId, additionalFieldId, items, FieldDataType.DATETIME_ARRAY) {
            @Override
            protected void addItem(RCAdditionalFieldValueSetter rcAdditionalFieldValueSetter,
                                   long additionalFieldId,
                                   long objectId,
                                   Instant item,
                                   ContextTransaction<?> context) throws PlatformException {
                rcAdditionalFieldValueSetter.addDateTimeValue(additionalFieldId, employeeId, item, context);
            }
        };
        return new GAccessQuery<>(query, CorePrivilege.EMPLOYEES, AccessOperation.WRITE);
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Установка аутентификаций сотрудникам")
    public static GraphQLQuery<RemoteObject, Boolean> assignAuthentication(
            @GraphQLName(TARGET_ALL)
            @GraphQLDescription("Флаг обновления всех сотрудников") final boolean targetAll,
            @GraphQLName(TARGET_DEPARTMENT_IDS)
            @GraphQLDescription("Идентификаторы отделов, сотрудники которых обновляются") final HashSet<Long> targetDepartmentIds,
            @GraphQLName(TARGET_EMPLOYEE_IDS)
            @GraphQLDescription("Идентификаторы обновляемых сотрудников") final HashSet<Long> targetEmployeeIds,
            @GraphQLDescription("Удаляемые аутентификации")
            @NonNull @GraphQLName(ERASED_AUTHENTICATION_IDS) final HashSet<Long> erasedAuthenticationIds,
            @GraphQLDescription("Добавляемые аутентификации")
            @NonNull @GraphQLName(ASSIGNED_AUTHENTICATION_IDS) final HashSet<Long> assignedAuthenticationIds
    ) {
        return new GAccessQuery<>(new AuthenticationAssigningQuery(
                targetAll,
                TARGET_ALL,
                targetDepartmentIds,
                targetEmployeeIds,
                erasedAuthenticationIds,
                assignedAuthenticationIds
        ), GAccessQuery.Operator.AND)
                .with(CorePrivilege.EMPLOYEE_ACCESS, AccessOperation.WRITE)
                .with(CorePrivilege.AUTHENTICATION, AccessOperation.READ);
    }

    @GraphQLField(value = "set_authentication")
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Установка аутентификаций сотрудникам")
    public static GraphQLQuery<RemoteObject, Boolean> setAuthentication(
            @GraphQLName(TARGET_ALL)
            @GraphQLDescription("Флаг обновления всех сотрудников") final boolean targetAll,
            @GraphQLName(TARGET_DEPARTMENT_IDS)
            @GraphQLDescription("Идентификаторы отделов, сотрудники которых обновляются") final HashSet<Long> targetDepartmentIds,
            @GraphQLName(TARGET_EMPLOYEE_IDS)
            @GraphQLDescription("Идентификаторы обновляемых сотрудников") final HashSet<Long> targetEmployeeIds,
            @GraphQLDescription("Назначаемые аутентификации")
            @NonNull @GraphQLName(AUTHENTICATION_IDS) final HashSet<Long> authenticationIds
    ) {
        return new GAccessQuery<>(new AuthenticationSettingQuery(
                targetAll,
                TARGET_ALL,
                targetDepartmentIds,
                targetEmployeeIds,
                authenticationIds
        ), GAccessQuery.Operator.AND)
                .with(CorePrivilege.EMPLOYEE_ACCESS, AccessOperation.WRITE)
                .with(CorePrivilege.AUTHENTICATION, AccessOperation.READ);
    }

    private static EmployeeReadable getEmployeeByLogin(String login, LogonType logonType, ReadableResource<EmployeeReadable> employeeReadableResource, QueryTransaction transaction) throws PlatformException {
        if (logonType == LogonType.EMAIL) {
            return employeeReadableResource.find(new HashFilter(EmployeeReadable.FIELD_EMAIL, login), transaction);
        } else if (logonType == LogonType.LOGIN) {
            return employeeReadableResource.find(new HashFilter(EmployeeReadable.FIELD_LOGIN, login), transaction);
        }
        throw new InternalError("Invalid logon type");
    }

    private static HashSet<Long> getAccessibleEmployees(HashSet<Long> employeeIds, ManagerEmployeeAccess access) {
        return access == null ? employeeIds : employeeIds.stream().filter(access::checkEmployee).collect(Collectors.toCollection(HashSet::new));
    }

    private static HashSet<Long> getAccessibleDepartments(HashSet<Long> departmentIds, ManagerEmployeeAccess access) {
        return access == null ? departmentIds : departmentIds.stream().filter(access::checkDepartment).collect(Collectors.toCollection(HashSet::new));
    }

    private static EmployeeBuilder getEmployeeBuilder(
            final boolean isTempPassword,
            final GOptional<String> email,
            final GOptional<String> login,
            final GOptional<String> firstName,
            final GOptional<String> patronymic,
            final GOptional<String> secondName,
            final GOptional<Long> departmentId,
            final GOptional<String> passwordHash,
            final GOptional<Language> language,
            final GOptional<String> personnelNumber,
            final GOptional<HashSet<String>> phoneNumbers,
            final GOptional<Boolean> systemEvents) throws PlatformException {
        EmployeeBuilder employeeBuilder = new EmployeeBuilder();
        if (email != null && email.isPresent()) {
            employeeBuilder.withEmail(email.get());
        }
        if (login != null && login.isPresent()) {
            employeeBuilder.withLogin(login.get());
        }
        if (firstName != null && firstName.isPresent()) {
            employeeBuilder.withFirstName(firstName.get());
        }
        if (patronymic != null && patronymic.isPresent()) {
            employeeBuilder.withPatronymic(patronymic.get());
        }
        if (secondName != null && secondName.isPresent()) {
            employeeBuilder.withSecondName(secondName.get());
        }
        if (passwordHash != null && passwordHash.isPresent()) {
            employeeBuilder.withPasswordHash(passwordHash.get());
            employeeBuilder.withNeedToChangePassword(passwordHash.get() != null && isTempPassword);
        }
        if (departmentId != null && departmentId.isPresent()) {
            employeeBuilder.withDepartmentId(departmentId.get());
        }
        if (language != null && language.isPresent()) {
            employeeBuilder.withLanguage(language.get());
        }
        if (personnelNumber != null && personnelNumber.isPresent()) {
            employeeBuilder.withPersonnelNumber(personnelNumber.get());
        }
        if (phoneNumbers != null && phoneNumbers.isPresent()) {
            if (phoneNumbers.get() == null) {
                throw GeneralExceptionBuilder.buildEmptyValueException(PHONE_NUMBERS);
            }
            employeeBuilder.withPhoneNumbers(phoneNumbers.get());
        }
        if (systemEvents != null && systemEvents.isPresent()) {
            employeeBuilder.withSendSystemEvents(systemEvents.get());
        }
        return employeeBuilder;
    }

    private static void logPasswordChange(ChangePasswordCause cause, EmployeeReadable employeeReadable, Context<?> context) {
        SecurityLog.info(
                new SyslogStructDataEvent(CoreEvent.Employee.TYPE_CHANGE_PASSWORD)
                        .withParam(CoreParameter.Employee.CAUSE, cause.name().toLowerCase()),
                new SyslogStructDataTarget(CoreTarget.TYPE_EMPLOYEE, employeeReadable.getId())
                        .withParam(CoreParameter.Employee.LOGIN, employeeReadable.getLogin()),
                context
        );
    }

    private static String getLogonValue(EmployeeReadable employeeReadable, CoreSubsystem coreSubsystem) {
        LogonType logonType = coreSubsystem.getConfig().getLogonType();
        if (logonType == LogonType.LOGIN) {
            return employeeReadable.getLogin();
        } else if (logonType == LogonType.EMAIL) {
            return employeeReadable.getEmail();
        } else {
            throw new InternalError("Invalid logon type");
        }
    }

    private static void validateSynchronizedField(RCIntegrationsExecutor rcIntegrations,
                                                  long employeeId,
                                                  GOptional<?> value,
                                                  String fieldKey,
                                                  ContextTransaction<?> context) throws PlatformException {
        if (value.isPresent() &&
                rcIntegrations.isSynchronized(employeeId, EmployeeReadable.class.getName(), fieldKey, context)) {
            throw CoreExceptionBuilder.buildSynchronizedFieldException();
        }
    }
}