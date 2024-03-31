package com.fuzzy.subsystem.core.graphql.mutation;

import com.fuzzy.cluster.core.remote.struct.RemoteObject;
import com.fuzzy.cluster.graphql.anotation.GraphQLDescription;
import com.fuzzy.cluster.graphql.anotation.GraphQLField;
import com.fuzzy.cluster.graphql.anotation.GraphQLName;
import com.fuzzy.cluster.graphql.anotation.GraphQLTypeOutObject;
import com.fuzzy.platform.component.frontend.authcontext.UnauthorizedContext;
import com.fuzzy.platform.component.frontend.context.ContextTransactionRequest;
import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.ResourceProvider;
import com.fuzzy.platform.sdk.graphql.annotation.GraphQLAuthControl;
import com.fuzzy.platform.sdk.graphql.customfield.graphqlquery.GraphQLQuery;
import com.fuzzy.subsystem.core.CoreSubsystem;
import com.fuzzy.subsystem.core.domainobject.license.LicenseBuilder;
import com.fuzzy.subsystem.core.graphql.query.license.GLicense;
import com.fuzzy.subsystem.core.remote.liscense.LicenseLoadDisablerChecker;
import com.fuzzy.subsystem.core.remote.liscense.LicenseLoadDisablerCheckerImpl;
import com.fuzzy.subsystem.core.remote.liscense.RCLicense;
import org.checkerframework.checker.nullness.qual.NonNull;

@GraphQLTypeOutObject("mutation_license")
public class GMutationLicense {

    @GraphQLField
    @GraphQLAuthControl({ UnauthorizedContext.class })
    @GraphQLDescription("Загрузка лицензии")
    public static GraphQLQuery<RemoteObject, GLicense> loadLicense(
            CoreSubsystem component,
            @GraphQLDescription("Лицензия")
            @NonNull @GraphQLName("license") final String licenseBase64
    ) {
        return new GraphQLQuery<>() {
            private RCLicense rcLicense;
            LicenseLoadDisablerChecker licenseLoadDisablerChecker;

            @Override
            public void prepare(ResourceProvider resources) {
                rcLicense = resources.getQueryRemoteController(CoreSubsystem.class, RCLicense.class);
                licenseLoadDisablerChecker = new LicenseLoadDisablerCheckerImpl(component);
            }

            @Override
            public GLicense execute(RemoteObject source, ContextTransactionRequest context) throws PlatformException {
                licenseLoadDisablerChecker.checkLicenseLoadDisabled();
                return new GLicense(rcLicense.create(new LicenseBuilder().withLicenseKey(licenseBase64), context));
            }
        };
    }
}
