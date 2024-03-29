package com.fuzzy.subsystem.core.graphql.mutation;

import com.fuzzy.main.cluster.core.remote.struct.RemoteObject;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLDescription;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLField;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLName;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLTypeOutObject;
import com.fuzzy.main.platform.component.frontend.authcontext.UnauthorizedContext;
import com.fuzzy.main.platform.component.frontend.context.ContextTransactionRequest;
import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.ResourceProvider;
import com.fuzzy.main.platform.sdk.context.ContextTransaction;
import com.fuzzy.main.platform.sdk.graphql.annotation.GraphQLAuthControl;
import com.fuzzy.main.platform.sdk.graphql.customfield.graphqlquery.GraphQLQuery;
import com.fuzzy.subsystem.core.CoreSubsystem;
import com.fuzzy.subsystem.core.CoreSubsystemConsts;
import com.fuzzy.subsystem.core.config.*;
import com.fuzzy.subsystem.core.employeeaccess.ManagerEmployeeAccessSetter;
import com.fuzzy.subsystem.core.exception.CoreExceptionBuilder;
import com.fuzzy.subsystem.core.graphql.mutation.employee.GMutationEmployee;
import com.fuzzy.subsystem.core.remote.accessrole.AccessRoleBuilder;
import com.fuzzy.subsystem.core.remote.accessrole.RControllerAccessRole;
import com.fuzzy.subsystem.core.remote.employee.EmployeeBuilder;
import com.fuzzy.subsystem.core.remote.employee.RControllerEmployeeControl;
import com.fuzzy.subsystem.core.remote.logon.RControllerEmployeeLogon;
import com.fuzzy.subsystem.core.remote.serverinit.RControllerServerInitNotification;
import com.fuzzy.subsystem.core.remote.serverinit.ServerInitData;
import com.fuzzy.subsystems.exception.GeneralExceptionBuilder;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Set;

@GraphQLTypeOutObject("mutation")
public class GMutation {

    private static final String EMAIL = "email";
    private static final String LOGIN = "login";
    private static final String PASSWORD_HASH = "password_hash";
    private static final String SERVER_LANGUAGE = "server_language";
    private static final String FIRST_NAME = "first_name";
    private static final String SECOND_NAME = "second_name";
    private static final String PATRONYMIC = "patronymic";

    @GraphQLField
    @GraphQLDescription("Модификация сотрудников")
    public static Class<GMutationEmployee> employee() {
        return GMutationEmployee.class;
    }

    @GraphQLField
    @GraphQLDescription("Модификация отделов")
    public static Class<GMutationDepartment> department() {
        return GMutationDepartment.class;
    }

    @GraphQLField
    @GraphQLDescription("Модификация ролей доступа")
    public static Class<GMutationAccessRole> accessRole() {
        return GMutationAccessRole.class;
    }

    @GraphQLField
    @GraphQLDescription("Модификация ключей api")
    public static Class<GMutationApiKey> apiKey() {
        return GMutationApiKey.class;
    }

    @GraphQLField
    @GraphQLDescription("Модификация конфигурации системы")
    public static Class<GMutationAppConfig> getAppConfig() {
        return GMutationAppConfig.class;
    }

    @GraphQLField
    @GraphQLDescription("Модификация конфигурации базы данных")
    public static Class<GMutationDatabase> getDatabase() {
        return GMutationDatabase.class;
    }

    @GraphQLField
    @GraphQLDescription("Модификация тегов")
    public static Class<GMutationTag> tag() {
        return GMutationTag.class;
    }

    @GraphQLField
    @GraphQLDescription("Модификация лицензии")
    public static Class<GMutationLicense> license() {
        return GMutationLicense.class;
    }

    @GraphQLField
    @GraphQLDescription("Модификация дополнительных полей")
    public static Class<GMutationAdditionalField> additionalField() {
        return GMutationAdditionalField.class;
    }

    @GraphQLField
    @GraphQLAuthControl(UnauthorizedContext.class)
    @GraphQLDescription("Первоначальная инициализация системы")
    public static GraphQLQuery<RemoteObject, Boolean> init(
            CoreSubsystem coreSubsystem,
            @GraphQLName(LOGIN) final String login,
            @NonNull @GraphQLName(EMAIL) final String email,
            @NonNull @GraphQLName(PASSWORD_HASH) final String passwordHash,
            @NonNull @GraphQLName(SERVER_LANGUAGE) final Language serverLanguage,
            @GraphQLName(FIRST_NAME) final String firstName,
            @GraphQLName(SECOND_NAME) final String secondName,
            @GraphQLName(PATRONYMIC) final String patronymic
    ) {
        return new GraphQLQuery<RemoteObject, Boolean>() {

            private RControllerEmployeeControl rControllerEmployeeControl;
            private RControllerEmployeeLogon rControllerEmployeeLogon;
            private RControllerAccessRole rControllerAccessRole;
            private CoreConfigSetter coreConfigSetter;
            private CoreConfigGetter coreConfigGetter;
            private ManagerEmployeeAccessSetter managerEmployeeAccessSetter;
            private Set<RControllerServerInitNotification> rControllerServerInitNotifications;

            @Override
            public void prepare(ResourceProvider resources) {
                rControllerEmployeeControl =
                        resources.getQueryRemoteController(CoreSubsystem.class, RControllerEmployeeControl.class);
                rControllerEmployeeLogon =
                        resources.getQueryRemoteController(CoreSubsystem.class, RControllerEmployeeLogon.class);
                rControllerAccessRole =
                        resources.getQueryRemoteController(CoreSubsystem.class, RControllerAccessRole.class);
                coreConfigSetter = new CoreConfigSetter(resources);
                coreConfigGetter = new CoreConfigGetter(resources);
                managerEmployeeAccessSetter = new ManagerEmployeeAccessSetter(coreSubsystem, resources);
                rControllerServerInitNotifications =
                        resources.getQueryRemoteControllers(RControllerServerInitNotification.class);
            }

            @Override
            public Boolean execute(RemoteObject source, ContextTransactionRequest context) throws PlatformException {
                ServerStatus serverStatus = coreConfigGetter.get(CoreConfigDescription.SERVER_STATUS, context.getTransaction());
                if (serverStatus != ServerStatus.NOT_INIT) {
                    throw CoreExceptionBuilder.buildAlreadyActiveServerException();
                }
                if (firstName != null && firstName.isEmpty()) {
                    throw GeneralExceptionBuilder.buildEmptyValueException(FIRST_NAME);
                }
                if (secondName != null && secondName.isEmpty()) {
                    throw GeneralExceptionBuilder.buildEmptyValueException(SECOND_NAME);
                }
                if (patronymic != null && patronymic.isEmpty()) {
                    throw GeneralExceptionBuilder.buildEmptyValueException(PATRONYMIC);
                }
                if (login != null && login.isEmpty() ||
                        rControllerEmployeeLogon.getLogonType() == LogonType.LOGIN && login == null) {
                    throw GeneralExceptionBuilder.buildEmptyValueException(LOGIN);
                }
                coreConfigSetter.set(CoreConfigDescription.SERVER_LANGUAGE, serverLanguage, context);
                EmployeeBuilder employeeBuilder = new EmployeeBuilder()
                        .withEmail(email)
                        .withLogin(login)
                        .withPasswordHash(passwordHash)
                        .withFirstName(firstName != null ? firstName : "Administrator")
                        .withSecondName(secondName)
                        .withPatronymic(patronymic);
                long employeeId = rControllerEmployeeControl.create(
                        employeeBuilder,
                        context
                ).getId();
                coreConfigSetter.set(CoreConfigDescription.SERVER_STATUS, ServerStatus.ACTIVE, context);

                long administratorAccessRoleId = createAccessRole(
                        CoreSubsystemConsts.Localization.AccessRole.ADMINISTRATOR,
                        true,
                        serverLanguage,
                        context
                );
                long securityAdministratorAccessRoleId = createAccessRole(
                        CoreSubsystemConsts.Localization.AccessRole.SECURITY_ADMINISTRATOR,
                        false,
                        serverLanguage,
                        context
                );
                managerEmployeeAccessSetter.setAll(employeeId, context.getTransaction());
                rControllerAccessRole.assignAccessRoleToEmployee(
                        administratorAccessRoleId,
                        employeeId,
                        context
                );

                ServerInitData serverInitData = new ServerInitData(
                        employeeId,
                        serverLanguage,
                        administratorAccessRoleId,
                        securityAdministratorAccessRoleId
                );
                for (RControllerServerInitNotification rControllerServerInitNotification :
                        rControllerServerInitNotifications) {
                    rControllerServerInitNotification.onServerInit(serverInitData, context);
                }
                return true;
            }

            private Long createAccessRole(String locKey, boolean admin, Language language, ContextTransaction<?> context)
                    throws PlatformException {
                String name = coreSubsystem.getMessageSource().getString(locKey, language);
                return rControllerAccessRole.create(new AccessRoleBuilder().withName(name).withAdmin(admin), context).getId();
            }
        };
    }

    @GraphQLField
    @GraphQLDescription("Модификация аутентификаций")
    public static Class<GMutationAuthentication> authentication() {
        return GMutationAuthentication.class;
    }

    @GraphQLField
    @GraphQLDescription("Модификация системных уведомлений")
    public static Class<GMutationSystemNotification> systemNotification() {
        return GMutationSystemNotification.class;
    }
}