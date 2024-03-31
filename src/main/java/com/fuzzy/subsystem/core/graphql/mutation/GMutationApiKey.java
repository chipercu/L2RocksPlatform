package com.fuzzy.subsystem.core.graphql.mutation;

import com.fuzzy.cluster.core.remote.struct.RemoteObject;
import com.fuzzy.cluster.graphql.anotation.GraphQLDescription;
import com.fuzzy.cluster.graphql.anotation.GraphQLField;
import com.fuzzy.cluster.graphql.anotation.GraphQLName;
import com.fuzzy.cluster.graphql.anotation.GraphQLTypeOutObject;
import com.fuzzy.cluster.graphql.struct.GOptional;
import com.fuzzy.platform.component.frontend.context.ContextTransactionRequest;
import com.fuzzy.platform.component.frontend.request.GRequestHttp;
import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.ReadableResource;
import com.fuzzy.platform.querypool.ResourceProvider;
import com.fuzzy.platform.sdk.graphql.annotation.GraphQLAuthControl;
import com.fuzzy.platform.sdk.graphql.customfield.graphqlquery.GraphQLQuery;
import com.fuzzy.platform.sdk.struct.ClusterFile;
import com.fuzzy.subsystem.core.CoreSubsystem;
import com.fuzzy.subsystem.core.access.CorePrivilege;
import com.fuzzy.subsystem.core.domainobject.apikey.ApiKeyEditable;
import com.fuzzy.subsystem.core.domainobject.apikey.ApiKeyReadable;
import com.fuzzy.subsystem.core.domainobject.apikey.ApiKeyType;
import com.fuzzy.subsystem.core.domainobject.apikey.ApiKeyTypes;
import com.fuzzy.subsystem.core.graphql.query.apikey.GApiKey;
import com.fuzzy.subsystem.core.graphql.query.privilege.GInputPrivilege;
import com.fuzzy.subsystem.core.privilege.PrivilegeGetter;
import com.fuzzy.subsystem.core.remote.accessroleprivileges.PrivilegeValue;
import com.fuzzy.subsystem.core.remote.apikey.ApiKeyBuilder;
import com.fuzzy.subsystem.core.remote.apikey.RControllerApiKey;
import com.fuzzy.subsystem.core.remote.apikeyprivileges.RControllerApiKeyPrivileges;
import com.fuzzy.subsystem.frontend.authcontext.AuthorizedContext;
import com.fuzzy.subsystems.access.AccessOperation;
import com.fuzzy.subsystems.access.AccessOperationCollection;
import com.fuzzy.subsystems.exception.GeneralExceptionBuilder;
import com.fuzzy.subsystems.graphql.query.GAccessQuery;
import com.fuzzy.subsystems.utils.CertificateUtil;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@GraphQLTypeOutObject("mutation_api_key")
public class GMutationApiKey {

    private static final String ID = "id";
    private static final String IDS = "ids";
    private static final String NAME = "name";
    private static final String TYPE = "type";
    private static final String SUBSYSTEM_UUID = "subsystem_uuid";
    private static final String PRIVILEGES = "privileges";
    private static final long MAX_CERT_FILE_SIZE = (long) Math.pow(1024, 2);

    @GraphQLField
    @GraphQLAuthControl({AuthorizedContext.class})
    @GraphQLDescription("Создание ключа api")
    public static GraphQLQuery<RemoteObject, GApiKey> create(
            CoreSubsystem subsystem,
            GRequestHttp request,
            @NonNull
            @GraphQLName(NAME)
            @GraphQLDescription("Название")
            final String name,
            @NonNull
            @GraphQLName(TYPE)
            @GraphQLDescription("Тип авторизации. Доступно: unsafe, certificate, ad")
            String type,
            @NonNull
            @GraphQLName(SUBSYSTEM_UUID)
            @GraphQLDescription("UUID подсистемы ключа api")
            String subsystem_uuid,
            @GraphQLName(PRIVILEGES)
            @GraphQLDescription("Привилегии")
            final GOptional<ArrayList<GInputPrivilege>> privileges
    ) {
        GraphQLQuery<RemoteObject, GApiKey> query = new GraphQLQuery<>() {

            private RControllerApiKey rControllerApiKey;
            private Set<RControllerApiKeyPrivileges> rControllerApiKeyPrivilegesList;
            private PrivilegeGetter privilegeGetter;

            @Override
            public void prepare(ResourceProvider resources) {
                rControllerApiKey =
                        resources.getQueryRemoteController(CoreSubsystem.class, RControllerApiKey.class);
                rControllerApiKeyPrivilegesList =
                        resources.getQueryRemoteControllers(RControllerApiKeyPrivileges.class);
                privilegeGetter = new PrivilegeGetter(resources);
            }

            @Override
            public GApiKey execute(RemoteObject source, ContextTransactionRequest context)
                    throws PlatformException {
                ApiKeyType apiKeyType = ApiKeyTypes.instanceOf(type, subsystem_uuid);
                ApiKeyBuilder builder = new ApiKeyBuilder()
                        .withName(name)
                        .withType(apiKeyType);
                if (apiKeyType == ApiKeyTypes.CERTIFICATE) {
                    ArrayList<GRequestHttp.UploadFile> uploadFiles = request.getUploadFiles();
                    if (uploadFiles == null || uploadFiles.size() != 1) {
                        throw GeneralExceptionBuilder.buildInvalidValueException(
                                "certificate count", uploadFiles != null ? uploadFiles.size() : 0);
                    }

                    X509Certificate cert;
                    try {
                        Path temp = Files.createTempFile("temp_", ".tmp");
                        ClusterFile clusterFile = new ClusterFile(subsystem, uploadFiles.get(0).uri);
                        if (clusterFile.getSize() > MAX_CERT_FILE_SIZE) {
                            throw GeneralExceptionBuilder.buildInvalidCertificateException("file size is very large");
                        }
                        builder.withContent(clusterFile.getContent());
                        clusterFile.moveTo(temp, StandardCopyOption.REPLACE_EXISTING);
                        try (InputStream inputStream = Files.newInputStream(temp)) {
                            cert = CertificateUtil.buildCertificate(inputStream);
                        }
                        Files.delete(temp);
                    } catch (IOException e) {
                        throw GeneralExceptionBuilder.buildIOErrorException(e);
                    }
                    builder.withValue(CertificateUtil.getThumbprint(cert));
                }
                ApiKeyReadable apiKey = rControllerApiKey.create(builder, context);
                setPrivileges(
                        rControllerApiKeyPrivilegesList,
                        privilegeGetter,
                        apiKey.getId(),
                        privileges,
                        context

                );
                return new GApiKey(apiKey);
            }
        };
        return new GAccessQuery<>(query, CorePrivilege.API_KEYS, AccessOperation.CREATE);
    }

    @GraphQLField
    @GraphQLAuthControl({AuthorizedContext.class})
    @GraphQLDescription("Редактирование ключа api")
    public static GraphQLQuery<RemoteObject, GApiKey> update(
            @NonNull
            @GraphQLName(ID)
            @GraphQLDescription("Идентификатор обновляемого ключа API")
            final Long apiKeyId,
            @GraphQLName(NAME)
            @GraphQLDescription("Новое значение названия")
            final GOptional<String> name,
            @GraphQLName(PRIVILEGES)
            @GraphQLDescription("Новое значение привилегий")
            final GOptional<ArrayList<GInputPrivilege>> privileges
    ) {
        GraphQLQuery<RemoteObject, GApiKey> query = new GraphQLQuery<>() {

            private ReadableResource<ApiKeyReadable> apiKeyReadableResource;
            private RControllerApiKey rControllerApiKey;
            private Set<RControllerApiKeyPrivileges> rControllerApiKeyPrivilegesList;
            private PrivilegeGetter privilegeGetter;

            @Override
            public void prepare(ResourceProvider resources) {
                apiKeyReadableResource = resources.getReadableResource(ApiKeyReadable.class);
                rControllerApiKey =
                        resources.getQueryRemoteController(CoreSubsystem.class, RControllerApiKey.class);
                rControllerApiKeyPrivilegesList =
                        resources.getQueryRemoteControllers(RControllerApiKeyPrivileges.class);
                privilegeGetter = new PrivilegeGetter(resources);
            }

            @Override
            public GApiKey execute(RemoteObject source, ContextTransactionRequest context)
                    throws PlatformException {
                ApiKeyReadable apiKey = apiKeyReadableResource.get(apiKeyId, context.getTransaction());
                if (apiKey == null) {
                    throw GeneralExceptionBuilder.buildNotFoundDomainObjectException(ApiKeyEditable.class, apiKeyId);
                }
                ApiKeyBuilder builder = new ApiKeyBuilder();
                if (name != null && name.isPresent()) {
                    builder.withName(name.get());
                }
                apiKey = rControllerApiKey.update(apiKeyId, builder, context);
                setPrivileges(
                        rControllerApiKeyPrivilegesList,
                        privilegeGetter,
                        apiKey.getId(),
                        privileges,
                        context

                );
                return new GApiKey(apiKey);
            }
        };
        return new GAccessQuery<>(query, CorePrivilege.API_KEYS, AccessOperation.WRITE);
    }

    @GraphQLField
    @GraphQLAuthControl({AuthorizedContext.class})
    @GraphQLDescription("Удаление ключей api")
    public static GraphQLQuery<RemoteObject, HashSet<Long>> remove(
            @NonNull
            @GraphQLName(IDS)
            @GraphQLDescription("Идентификаторы удаляемых ключей api")
            final HashSet<Long> apiKeyIds
    ) {
        GraphQLQuery<RemoteObject, HashSet<Long>> query = new GraphQLQuery<>() {

            private RControllerApiKey rControllerApiKey;

            @Override
            public void prepare(ResourceProvider resources) {
                rControllerApiKey =
                        resources.getQueryRemoteController(CoreSubsystem.class, RControllerApiKey.class);
            }

            @Override
            public HashSet<Long> execute(RemoteObject source, ContextTransactionRequest context) throws PlatformException {
                return rControllerApiKey.remove(apiKeyIds, context);
            }
        };
        return new GAccessQuery<>(query, CorePrivilege.API_KEYS, AccessOperation.DELETE);
    }

    private static void setPrivileges(
            Set<RControllerApiKeyPrivileges> rControllerApiKeyPrivilegesList,
            PrivilegeGetter privilegeGetter,
            long apiKeyId,
            GOptional<ArrayList<GInputPrivilege>> privileges,
            ContextTransactionRequest context
    ) throws PlatformException {
        if (privileges != null && privileges.isPresent()) {
            if (privileges.get() == null) {
                throw GeneralExceptionBuilder.buildInvalidValueException(PRIVILEGES, null);
            }
            PrivilegeValue[] privilegeValues = GInputPrivilege.convert(privileges.get());
            for (PrivilegeValue privilegeValue : privilegeValues) {
                AccessOperationCollection availableOperation =
                        privilegeGetter.getAvailableOperations(privilegeValue.getKey(), context);
                for (AccessOperation operation : privilegeValue.getOperations().getOperations()) {
                    if (!availableOperation.contains(operation)) {
                        throw GeneralExceptionBuilder.buildInvalidValueException(PRIVILEGES, privilegeValue.getKey());
                    }
                }
            }
            for (RControllerApiKeyPrivileges rControllerApiKeyPrivileges : rControllerApiKeyPrivilegesList) {
                rControllerApiKeyPrivileges.setPrivilegesToApiKey(apiKeyId, privilegeValues, context);
            }
        }
    }
}
