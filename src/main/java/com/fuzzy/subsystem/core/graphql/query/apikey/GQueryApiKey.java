package com.fuzzy.subsystem.core.graphql.query.apikey;

import com.fuzzy.cluster.core.remote.struct.RemoteObject;
import com.fuzzy.cluster.graphql.anotation.GraphQLDescription;
import com.fuzzy.cluster.graphql.anotation.GraphQLField;
import com.fuzzy.cluster.graphql.anotation.GraphQLName;
import com.fuzzy.cluster.graphql.anotation.GraphQLTypeOutObject;
import com.fuzzy.cluster.graphql.struct.GOptional;
import com.fuzzy.platform.component.frontend.context.ContextTransactionRequest;
import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.ResourceProvider;
import com.fuzzy.platform.sdk.graphql.annotation.GraphQLAuthControl;
import com.fuzzy.platform.sdk.graphql.customfield.graphqlquery.GraphQLQuery;
import com.fuzzy.subsystem.core.access.CorePrivilege;
import com.fuzzy.subsystem.core.autocomplete.ApiKeyAtomicAutocomplete;
import com.fuzzy.subsystem.core.domainobject.apikey.ApiKeyReadable;
import com.fuzzy.subsystem.core.graphql.query.apikey.autocomplete.GApikeyAutocompleteResult;
import com.fuzzy.subsystem.core.graphql.query.apikey.list.GApiKeyListResult;
import com.fuzzy.subsystem.core.graphql.query.privilege.GInputPrivilege;
import com.fuzzy.subsystem.core.list.ApiKeyListBuilder;
import com.fuzzy.subsystem.core.list.ApiKeyListParam;
import com.fuzzy.subsystem.frontend.authcontext.AuthorizedContext;
import com.fuzzy.subsystems.access.AccessOperation;
import com.fuzzy.subsystems.autocomplete.LightAutocomplete;
import com.fuzzy.subsystems.graphql.input.GPaging;
import com.fuzzy.subsystems.graphql.input.GTextFilter;
import com.fuzzy.subsystems.graphql.query.GAccessQuery;
import com.fuzzy.subsystems.graphql.query.GPrimaryKeyQueryImpl;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.HashSet;

@GraphQLTypeOutObject("api_key_query")
public class GQueryApiKey {

    private static final String ID = "id";
    private static final String TEXT_FILTER = "text_filter";
    private static final String PAGING = "paging";
    private static final String PRIVILEGE_FILTER = "privilege_filter";
    private static final String TOP_API_KEYS = "top_api_keys";

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Ключ API по идентификатору")
    public static GraphQLQuery<RemoteObject, GApiKey> getApiKey(
            @GraphQLDescription("Идентификатор ключа API")
            @NonNull @GraphQLName(ID) final long apiKeyId
    ) {
        GraphQLQuery<RemoteObject, GApiKey> query =
                new GPrimaryKeyQueryImpl<>(ApiKeyReadable.class, GApiKey::new, apiKeyId);
        return new GAccessQuery<>(query, CorePrivilege.API_KEYS, AccessOperation.READ);
    }

    @GraphQLField(value = "api_key_autocomplete")
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Autocomplete по ключам API")
    public static GraphQLQuery<RemoteObject, GApikeyAutocompleteResult> getApiKeyAutocomplete(
            @GraphQLDescription("Текстовый фильтр")
            @GraphQLName(TEXT_FILTER) final GTextFilter textFilter,
            @GraphQLDescription("Фильтр по привилегиям")
            @GraphQLName(PRIVILEGE_FILTER) final ArrayList<GInputPrivilege> privilegeFilter,
            @GraphQLDescription("Параметры пейджинга")
            @GraphQLName(PAGING) final GPaging paging
    ) {
        GraphQLQuery<RemoteObject, GApikeyAutocompleteResult> query =
                new GraphQLQuery<>() {

                    private ApiKeyAtomicAutocomplete apikeyAutocomplete;

                    @Override
                    public void prepare(ResourceProvider resources) {
                        apikeyAutocomplete = new ApiKeyAtomicAutocomplete(resources);
                    }

                    @Override
                    public GApikeyAutocompleteResult execute(RemoteObject source, ContextTransactionRequest context)
                            throws PlatformException {
                        apikeyAutocomplete.setPrivilegeFilter(privilegeFilter);

                        return new GApikeyAutocompleteResult(
                                new LightAutocomplete<>(apikeyAutocomplete).execute(textFilter, null, paging, context));
                    }
                };
        return new GAccessQuery<>(query, CorePrivilege.API_KEYS, AccessOperation.READ);
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Ключи API")
    public static GraphQLQuery<RemoteObject, GApiKeyListResult> getApiKeyList(
            @GraphQLDescription("Текстовый фильтр")
            @GraphQLName(TEXT_FILTER) final GTextFilter textFilter,
            @GraphQLDescription("Фильтр по привилегиям")
            @GraphQLName(PRIVILEGE_FILTER) final ArrayList<GInputPrivilege> privilegeFilter,
            @GraphQLDescription("Параметры пейджинга")
            @GraphQLName(PAGING) final GPaging paging,
            @GraphQLDescription("Ключи доступа, отображающиеся в начале списка")
            @GraphQLName(TOP_API_KEYS) final GOptional<HashSet<Long>> topApiKeys
    ) {
        GraphQLQuery<RemoteObject, GApiKeyListResult> query = new GraphQLQuery<>() {

            private ApiKeyListBuilder listBuilder;

            @Override
            public void prepare(ResourceProvider resources) {
                listBuilder = new ApiKeyListBuilder(resources);
            }

            @Override
            public GApiKeyListResult execute(RemoteObject source, ContextTransactionRequest context) throws PlatformException {
                ApiKeyListParam listParam = new ApiKeyListParam.Builder()
                        .withTextFilter(textFilter)
                        .withPrivilegeFilter(privilegeFilter)
                        .withPaging(paging)
                        .withTopItems(topApiKeys.isPresent() ? topApiKeys.get() : null)
                        .build();
                return new GApiKeyListResult(listBuilder.build(listParam, context));
            }
        };
        return new GAccessQuery<>(query, CorePrivilege.API_KEYS, AccessOperation.READ);
    }
}