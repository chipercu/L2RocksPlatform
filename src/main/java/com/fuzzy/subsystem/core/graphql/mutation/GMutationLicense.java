package com.fuzzy.subsystem.core.graphql.mutation;

import com.infomaximum.cluster.core.remote.struct.RemoteObject;
import com.infomaximum.cluster.graphql.anotation.GraphQLDescription;
import com.infomaximum.cluster.graphql.anotation.GraphQLField;
import com.infomaximum.cluster.graphql.anotation.GraphQLName;
import com.infomaximum.cluster.graphql.anotation.GraphQLTypeOutObject;
import com.infomaximum.platform.component.frontend.authcontext.UnauthorizedContext;
import com.infomaximum.platform.component.frontend.context.ContextTransactionRequest;
import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.ResourceProvider;
import com.infomaximum.platform.sdk.graphql.annotation.GraphQLAuthControl;
import com.infomaximum.platform.sdk.graphql.customfield.graphqlquery.GraphQLQuery;
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
