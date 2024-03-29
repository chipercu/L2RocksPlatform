package com.fuzzy.subsystem.core.graphql.query.authentication;

import com.fuzzy.main.cluster.core.remote.struct.RemoteObject;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLDescription;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLField;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLTypeOutObject;
import com.fuzzy.main.platform.component.frontend.authcontext.UnauthorizedContext;
import com.fuzzy.main.platform.component.frontend.context.ContextTransactionRequest;
import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.ResourceProvider;
import com.fuzzy.main.platform.sdk.graphql.annotation.GraphQLAuthControl;
import com.fuzzy.main.platform.sdk.graphql.customfield.graphqlquery.GraphQLQuery;
import com.fuzzy.subsystem.core.remote.authenticationtype.RCAuthenticationType;
import com.fuzzy.subsystem.core.utils.LanguageGetter;
import com.fuzzy.subsystems.remote.RCExecutor;

import java.util.Objects;

@GraphQLTypeOutObject("authentication_type")
public class GAuthenticationType implements RemoteObject {

    private final String value;

    public GAuthenticationType(String value) {
        this.value = value;
    }

    @GraphQLField
    @GraphQLAuthControl({ UnauthorizedContext.class })
    @GraphQLDescription("Значение")
    public String getValue() {
        return value;
    }

    @GraphQLField
    @GraphQLAuthControl({ UnauthorizedContext.class })
    @GraphQLDescription("Отображаемое имя")
    public static GraphQLQuery<GAuthenticationType, String> getDisplayName() {
        return new GraphQLQuery<GAuthenticationType, String>() {

            private RCExecutor<RCAuthenticationType> rcAuthenticationType;
            private LanguageGetter languageGetter;

            @Override
            public void prepare(ResourceProvider resources) {
                rcAuthenticationType = new RCExecutor<>(resources, RCAuthenticationType.class);
                languageGetter = new LanguageGetter(resources);
            }

            @Override
            public String execute(GAuthenticationType source,
                                  ContextTransactionRequest context) throws PlatformException {
                return rcAuthenticationType.getFirstNotNull(rc ->
                        rc.getLocalization(source.value, languageGetter.get(context), context));
            }
        };
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GAuthenticationType that = (GAuthenticationType) o;
        return Objects.equals(value, that.value);
    }
}
