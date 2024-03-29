package com.fuzzy.subsystem.core.graphql.mutation;

import com.infomaximum.cluster.core.remote.struct.RemoteObject;
import com.infomaximum.cluster.graphql.anotation.GraphQLDescription;
import com.infomaximum.cluster.graphql.anotation.GraphQLField;
import com.infomaximum.cluster.graphql.anotation.GraphQLName;
import com.infomaximum.cluster.graphql.anotation.GraphQLTypeOutObject;
import com.infomaximum.cluster.graphql.struct.GOptional;
import com.infomaximum.platform.component.frontend.context.ContextTransactionRequest;
import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.QueryTransaction;
import com.infomaximum.platform.querypool.ReadableResource;
import com.infomaximum.platform.querypool.ResourceProvider;
import com.infomaximum.platform.sdk.graphql.annotation.GraphQLAuthControl;
import com.infomaximum.platform.sdk.graphql.customfield.graphqlquery.GraphQLQuery;
import com.fuzzy.subsystem.core.CoreSubsystem;
import com.fuzzy.subsystem.core.CoreSubsystemConsts;
import com.fuzzy.subsystem.core.access.CorePrivilege;
import com.fuzzy.subsystem.core.config.AuthenticationConfig;
import com.fuzzy.subsystem.core.config.ComplexPassword;
import com.fuzzy.subsystem.core.config.CoreConfigGetter;
import com.fuzzy.subsystem.core.config.CoreConfigSetter;
import com.fuzzy.subsystem.core.domainobject.authentication.AuthenticationReadable;
import com.fuzzy.subsystem.core.exception.CoreExceptionBuilder;
import com.fuzzy.subsystem.core.graphql.input.GComplexPasswordInput;
import com.fuzzy.subsystem.core.graphql.query.authentication.GAuthentication;
import com.fuzzy.subsystem.core.remote.authentication.AuthenticationCreatingBuilder;
import com.fuzzy.subsystem.core.remote.authentication.AuthenticationUpdatingBuilder;
import com.fuzzy.subsystem.core.remote.authentication.RCAuthentication;
import com.fuzzy.subsystem.frontend.authcontext.AuthorizedContext;
import com.fuzzy.subsystems.access.AccessOperation;
import com.fuzzy.subsystems.exception.GeneralExceptionBuilder;
import com.fuzzy.subsystems.graphql.query.GAccessQuery;
import com.fuzzy.subsystems.utils.PrimaryKeyValidator;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.time.Duration;
import java.util.Objects;

@GraphQLTypeOutObject("mutation_authentication")
public class GMutationAuthentication {

    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String TYPE = "type";
    private static final String COMPLEX_PASSWORD = "complex_password";
    private static final String PASSWORD_EXPIRATION_TIME = "password_expiration_time";
    private static final String MAX_INVALID_LOGON_COUNT = "max_invalid_logon_count";

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Создание аутентификации")
    public static GraphQLQuery<RemoteObject, GAuthentication> create(
            @GraphQLDescription("Название")
            @NonNull @GraphQLName(NAME) final String name,
            @GraphQLDescription("Тип")
            @NonNull @GraphQLName(TYPE) final String type
    ) {
        GraphQLQuery<RemoteObject, GAuthentication> query = new GraphQLQuery<RemoteObject, GAuthentication>() {

            private RCAuthentication rcAuthentication;

            @Override
            public void prepare(ResourceProvider resources) {
                rcAuthentication = resources.getQueryRemoteController(CoreSubsystem.class, RCAuthentication.class);
            }

            @Override
            public GAuthentication execute(RemoteObject source, ContextTransactionRequest context) throws PlatformException {
                AuthenticationCreatingBuilder builder = new AuthenticationCreatingBuilder(name, type);
                AuthenticationReadable authentication = rcAuthentication.create(builder, context);
                return new GAuthentication(authentication);
            }
        };
        return new GAccessQuery<>(query, CorePrivilege.AUTHENTICATION, AccessOperation.CREATE);
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Обновление аутентификации")
    public static GraphQLQuery<RemoteObject, GAuthentication> update(
            @GraphQLDescription("Идентификатор обновляемой аутентификации")
            @NonNull @GraphQLName(ID) final long authenticationId,
            @GraphQLDescription("Новое название")
            @GraphQLName(NAME) final GOptional<String> name
    ) {
        GraphQLQuery<RemoteObject, GAuthentication> query = new GraphQLQuery<RemoteObject, GAuthentication>() {

            private RCAuthentication rcAuthentication;

            @Override
            public void prepare(ResourceProvider resources) {
                rcAuthentication = resources.getQueryRemoteController(CoreSubsystem.class, RCAuthentication.class);
            }

            @Override
            public GAuthentication execute(RemoteObject source, ContextTransactionRequest context) throws PlatformException {
                AuthenticationUpdatingBuilder builder = new AuthenticationUpdatingBuilder();
                if (name.isPresent()) {
                    builder.setName(name.get());
                }
                AuthenticationReadable authentication = rcAuthentication.update(authenticationId, builder, context);
                return new GAuthentication(authentication);
            }
        };
        return new GAccessQuery<>(query, CorePrivilege.AUTHENTICATION, AccessOperation.WRITE);
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Обновление аутентификации")
    public static GraphQLQuery<RemoteObject, GAuthentication> updateIntegrated(
            @GraphQLDescription("Идентификатор обновляемой аутентификации")
            @NonNull @GraphQLName(ID) final long authenticationId,
            @GraphQLDescription("Флаг сложности пароля")
            @GraphQLName(COMPLEX_PASSWORD) final GOptional<GComplexPasswordInput> complexPassword,
            @GraphQLDescription("Срок действия пароля")
            @GraphQLName(PASSWORD_EXPIRATION_TIME) final GOptional<Duration> passwordExpirationTime,
            @GraphQLDescription("Количество попыток входа")
            @GraphQLName(MAX_INVALID_LOGON_COUNT) final GOptional<Integer> maxInvalidLogonCount
    ) {
        GraphQLQuery<RemoteObject, GAuthentication> query = new GraphQLQuery<RemoteObject, GAuthentication>() {

            private ReadableResource<AuthenticationReadable> authenticationReadableResource;
            private CoreConfigGetter configGetter;
            private CoreConfigSetter configSetter;

            @Override
            public void prepare(ResourceProvider resources) {
                authenticationReadableResource = resources.getReadableResource(AuthenticationReadable.class);
                configGetter = new CoreConfigGetter(resources);
                configSetter = new CoreConfigSetter(resources);
            }

            @Override
            public GAuthentication execute(RemoteObject source, ContextTransactionRequest context) throws PlatformException {
                QueryTransaction transaction = context.getTransaction();
                AuthenticationReadable authentication = new PrimaryKeyValidator(false)
                        .validateAndGet(authenticationId, authenticationReadableResource, transaction);
                if (!Objects.equals(authentication.getType(), CoreSubsystemConsts.AuthenticationTypes.INTEGRATED)) {
                    throw CoreExceptionBuilder.buildInvalidAuthenticationTypeException();
                }
                AuthenticationConfig authenticationConfig = configGetter.getAuthenticationConfig(context.getTransaction());
                if (complexPassword.isPresent()) {
                    updateComplexPassword(
                            authentication,
                            complexPassword.get(),
                            authenticationConfig.getComplexPassword(),
                            context
                    );
                }
                if (passwordExpirationTime.isPresent() &&
                        !Objects.equals(passwordExpirationTime.get(), authenticationConfig.getPasswordExpirationTime())) {
                    if (passwordExpirationTime.get() != null && passwordExpirationTime.get().toNanos() < 0) {
                        throw GeneralExceptionBuilder.buildInvalidValueException(
                                PASSWORD_EXPIRATION_TIME, passwordExpirationTime.get());
                    }
                    configSetter.setPasswordExpirationTime(
                            authentication,
                            passwordExpirationTime.get() != null ? passwordExpirationTime.get().getSeconds() : null,
                            context
                    );
                }
                if (maxInvalidLogonCount.isPresent() &&
                        !Objects.equals(maxInvalidLogonCount.get(), authenticationConfig.getMaxInvalidLogonCount())) {
                    if (maxInvalidLogonCount.get() != null && maxInvalidLogonCount.get() < 0) {
                        throw GeneralExceptionBuilder.buildInvalidValueException(
                                MAX_INVALID_LOGON_COUNT, maxInvalidLogonCount.get());
                    }
                    configSetter.setMaxInvalidLogonCount(authentication, maxInvalidLogonCount.get(), context);
                }
                return new GAuthentication(authentication);
            }

            private void updateComplexPassword(
                    AuthenticationReadable authentication,
                    GComplexPasswordInput gComplexPasswordInput,
                    ComplexPassword currentComplexPassword,
                    ContextTransactionRequest context
            ) throws PlatformException {
                if (gComplexPasswordInput != null) {
                    if (gComplexPasswordInput.getMinPasswordLength() < 0) {
                        throw GeneralExceptionBuilder.buildInvalidValueException(
                                COMPLEX_PASSWORD, gComplexPasswordInput.getMinPasswordLength());
                    }
                    if (currentComplexPassword == null) {
                        configSetter.setComplexPassword(authentication, true, context);
                    }
                    if (currentComplexPassword == null || !Objects.equals(
                            gComplexPasswordInput.getMinPasswordLength(),
                            currentComplexPassword.getMinPasswordLength())) {
                        configSetter.setMinPasswordLength(
                                authentication,
                                gComplexPasswordInput.getMinPasswordLength(),
                                context
                        );
                    }
                } else if (currentComplexPassword != null) {
                    configSetter.setComplexPassword(authentication, false, context);
                    configSetter.setMinPasswordLength(authentication, null, context);
                }
            }
        };
        return new GAccessQuery<>(query, CorePrivilege.AUTHENTICATION, AccessOperation.WRITE);
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Удаление аутентификации")
    public static GraphQLQuery<RemoteObject, Boolean> remove(
            @GraphQLDescription("Идентификатор аутентификации")
            @NonNull @GraphQLName(ID) final long authenticationId
    ) {
        GraphQLQuery<RemoteObject, Boolean> query = new GraphQLQuery<RemoteObject, Boolean>() {

            private RCAuthentication rcAuthentication;

            @Override
            public void prepare(ResourceProvider resources) {
                rcAuthentication = resources.getQueryRemoteController(CoreSubsystem.class, RCAuthentication.class);
            }

            @Override
            public Boolean execute(RemoteObject source, ContextTransactionRequest context) throws PlatformException {
                return rcAuthentication.remove(authenticationId, context);
            }
        };
        return new GAccessQuery<>(query, CorePrivilege.AUTHENTICATION, AccessOperation.DELETE);
    }
}

