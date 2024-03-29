package com.fuzzy.subsystem.core.graphql.query.apikey;

import com.fuzzy.main.cluster.graphql.anotation.GraphQLDescription;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLField;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLTypeOutObject;
import com.fuzzy.main.platform.component.frontend.context.ContextTransactionRequest;
import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.ResourceProvider;
import com.fuzzy.main.platform.sdk.graphql.annotation.GraphQLAuthControl;
import com.fuzzy.main.platform.sdk.graphql.customfield.graphqlquery.GraphQLQuery;
import com.fuzzy.subsystem.core.CoreSubsystem;
import com.fuzzy.subsystem.core.access.CorePrivilege;
import com.fuzzy.subsystem.core.apikeyprivileges.ApiKeyPrivilegesGetter;
import com.fuzzy.subsystem.core.domainobject.apikey.ApiKeyReadable;
import com.fuzzy.subsystem.core.graphql.query.privilege.GOutPrivilege;
import com.fuzzy.subsystem.core.remote.crypto.RCCrypto;
import com.fuzzy.subsystem.frontend.authcontext.AuthorizedContext;
import com.fuzzy.subsystems.access.AccessOperation;
import com.fuzzy.subsystems.access.AccessOperationCollection;
import com.fuzzy.subsystems.graphql.GDomainObject;
import com.fuzzy.subsystems.graphql.query.GAccessQuery;
import com.fuzzy.subsystems.utils.CertificateUtil;

import java.io.ByteArrayInputStream;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;

@GraphQLTypeOutObject("api_key")
public class GApiKey extends GDomainObject<ApiKeyReadable> {

    public GApiKey(ApiKeyReadable source) {
        super(source);
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Идентификатор")
    public long getId() {
        return super.getSource().getId();
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Название")
    public static GraphQLQuery<GApiKey, String> getName() {
        return new GAccessQuery<>(gApiKey -> gApiKey.getSource().getName(),
                CorePrivilege.API_KEYS, AccessOperation.READ);
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Значение")
    public static GraphQLQuery<GApiKey, String> getValue() {
        return new GAccessQuery<>(gApiKey -> gApiKey.getSource().getValue(),
                CorePrivilege.API_KEYS, AccessOperation.READ);
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Тип аутентификации")
    public static GraphQLQuery<GApiKey, String> getType() {
        return new GAccessQuery<>(gApiKey -> gApiKey.getSource().getType(),
                CorePrivilege.API_KEYS, AccessOperation.READ);
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Uuid подсистемы ключа API")
    public static GraphQLQuery<GApiKey, String> getSubsystemUuid() {
        return new GAccessQuery<>(gApiKey -> gApiKey.getSource().getSubsystemUuid(),
                CorePrivilege.API_KEYS, AccessOperation.READ);
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Отпечаток сертификата")
    public static GraphQLQuery<GApiKey, String> getThumbprint() {
        return new GAccessQuery<>(new GraphQLQuery<>() {

            private RCCrypto rcCrypto;

            @Override
            public void prepare(ResourceProvider resources) {
                rcCrypto = resources.getQueryRemoteController(CoreSubsystem.class, RCCrypto.class);
            }

            @Override
            public String execute(GApiKey source, ContextTransactionRequest context) throws PlatformException {
                final byte[] content = source.getSource().getContent();
                if (content == null) {
                    return "UNSUPPORTED";
                }

                final byte[] certContent = rcCrypto.decrypt(content, context);

                final X509Certificate cert = CertificateUtil.buildCertificate(new ByteArrayInputStream(certContent));
                return CertificateUtil.getThumbprintSHA1(cert);
            }
        }, CorePrivilege.API_KEYS, AccessOperation.READ);
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Привилегии")
    public static GraphQLQuery<GApiKey, ArrayList<GOutPrivilege>> getPrivileges() {
        GraphQLQuery<GApiKey, ArrayList<GOutPrivilege>> query = new GraphQLQuery<GApiKey, ArrayList<GOutPrivilege>>() {

            private ApiKeyPrivilegesGetter apiKeyPrivilegesGetter;

            @Override
            public void prepare(ResourceProvider resources) {
                apiKeyPrivilegesGetter = new ApiKeyPrivilegesGetter(resources);
            }

            @Override
            public ArrayList<GOutPrivilege> execute(GApiKey source, ContextTransactionRequest context)
                    throws PlatformException {
                HashMap<String, AccessOperationCollection> privilegeValues =
                        apiKeyPrivilegesGetter.getPrivileges(source.getId(), context);
                HashSet<String> privileges = apiKeyPrivilegesGetter.getPrivilegeCollection();
                ArrayList<GOutPrivilege> gPrivileges = new ArrayList<>();
                for (String privilege : privileges) {
                    gPrivileges.add(new GOutPrivilege(
                            privilege,
                            privilegeValues.getOrDefault(privilege, AccessOperationCollection.EMPTY)
                    ));
                }
                gPrivileges.sort(Comparator.comparing(GOutPrivilege::getKey));
                return gPrivileges;
            }
        };
        return new GAccessQuery<>(query, CorePrivilege.API_KEYS, AccessOperation.READ);
    }
}
