package com.fuzzy.subsystem.core.graphql.query.authentication.queries;

import com.fuzzy.cluster.core.remote.struct.RemoteObject;
import com.fuzzy.platform.component.frontend.context.ContextTransactionRequest;
import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.ResourceProvider;
import com.fuzzy.platform.sdk.graphql.customfield.graphqlquery.GraphQLQuery;
import com.fuzzy.subsystem.core.config.Language;
import com.fuzzy.subsystem.core.graphql.query.authentication.GAuthenticationType;
import com.fuzzy.subsystem.core.remote.authenticationtype.RCAuthenticationType;
import com.fuzzy.subsystem.core.utils.LanguageGetter;
import com.fuzzy.subsystems.remote.RCExecutor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

public class AuthenticationTypesQuery extends GraphQLQuery<RemoteObject, ArrayList<GAuthenticationType>> {

    private RCExecutor<RCAuthenticationType> rcAuthenticationType;
    private LanguageGetter languageGetter;

    @Override
    public void prepare(ResourceProvider resources) {
        rcAuthenticationType = new RCExecutor<>(resources, RCAuthenticationType.class);
        languageGetter = new LanguageGetter(resources);
    }

    @Override
    public ArrayList<GAuthenticationType> execute(RemoteObject source,
                                                  ContextTransactionRequest context) throws PlatformException {
        Language language = languageGetter.get(context);
        ArrayList<String> types = rcAuthenticationType.apply(rc -> rc.getTypes(context), ArrayList::new);
        ArrayList<TypePair> typePairs = new ArrayList<>(types.size());
        for (String type : types) {
            String displayName = rcAuthenticationType.getFirstNotNull(rc -> rc.getLocalization(type, language, context));
            typePairs.add(new TypePair(type, displayName));
        }
        typePairs.sort(Comparator.comparing(typePair -> typePair.displayName));
        return typePairs.stream()
                .map(typePair -> new GAuthenticationType(typePair.type))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private record TypePair(String type, String displayName) {}
}
