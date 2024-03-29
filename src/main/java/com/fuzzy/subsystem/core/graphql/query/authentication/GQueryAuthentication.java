package com.fuzzy.subsystem.core.graphql.query.authentication;

import com.fuzzy.main.cluster.core.remote.struct.RemoteObject;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLDescription;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLField;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLName;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLTypeOutObject;
import com.fuzzy.main.cluster.graphql.struct.GOptional;
import com.fuzzy.main.rdao.database.domainobject.filter.HashFilter;
import com.fuzzy.main.platform.component.frontend.authcontext.UnauthorizedContext;
import com.fuzzy.main.platform.component.frontend.context.ContextTransactionRequest;
import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.QueryTransaction;
import com.fuzzy.main.platform.querypool.ReadableResource;
import com.fuzzy.main.platform.querypool.ResourceProvider;
import com.fuzzy.main.platform.sdk.graphql.annotation.GraphQLAuthControl;
import com.fuzzy.main.platform.sdk.graphql.customfield.graphqlquery.GraphQLQuery;
import com.fuzzy.subsystem.core.CoreSubsystemConsts;
import com.fuzzy.subsystem.core.access.CorePrivilege;
import com.fuzzy.subsystem.core.autocomplete.AuthenticationAutocomplete;
import com.fuzzy.subsystem.core.config.AuthenticationConfig;
import com.fuzzy.subsystem.core.config.CoreConfigGetter;
import com.fuzzy.subsystem.core.domainobject.authentication.AuthenticationReadable;
import com.fuzzy.subsystem.core.enums.AuthenticationSortingColumn;
import com.fuzzy.subsystem.core.graphql.query.authentication.autocomplete.GAuthenticationAutocompleteResult;
import com.fuzzy.subsystem.core.graphql.query.authentication.list.GAuthenticationListResult;
import com.fuzzy.subsystem.core.graphql.query.authentication.queries.AuthenticationTypesQuery;
import com.fuzzy.subsystem.core.graphql.query.authentication.queries.UsedAuthenticationTypesQuery;
import com.fuzzy.subsystem.core.graphql.query.config.GComplexPasswordOutput;
import com.fuzzy.subsystem.core.list.AuthenticationListBuilder;
import com.fuzzy.subsystem.core.remote.authenticationtype.RCAuthenticationType;
import com.fuzzy.subsystem.frontend.authcontext.AuthorizedContext;
import com.fuzzy.subsystems.access.AccessOperation;
import com.fuzzy.subsystems.autocomplete.LightAutocompleteResult;
import com.fuzzy.subsystems.graphql.enums.SortingDirection;
import com.fuzzy.subsystems.graphql.input.GInputItems;
import com.fuzzy.subsystems.graphql.input.GPaging;
import com.fuzzy.subsystems.graphql.input.GTextFilter;
import com.fuzzy.subsystems.graphql.query.GAccessQuery;
import com.fuzzy.subsystems.list.ListParam;
import com.fuzzy.subsystems.list.ListResult;
import com.fuzzy.subsystems.remote.RCExecutor;
import com.fuzzy.subsystems.utils.PrimaryKeyValidator;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@GraphQLTypeOutObject("authentication_query")
public class GQueryAuthentication {

    private static final String ID = "id";
    private static final String SORTING_COLUMN = "sorting_column";
    private static final String SORTING_DIRECTION = "sorting_direction";
    private static final String TYPE_FILTER = "type_filter";
    private static final String TEXT_FILTER = "text_filter";
    private static final String PAGING = "paging";
    private static final String EXCLUDED_AUTHENTICATIONS = "excluded_authentications";
    private static final String TOP_AUTHENTICATIONS = "top_authentications";
    private static final String ALWAYS_COMING_DATA = "always_coming_data";

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Аутентификация по идентификатору")
    public static GraphQLQuery<RemoteObject, GAuthentication> getAuthentication(
            @GraphQLDescription("Идентификатор аутентификации")
            @NonNull @GraphQLName(ID) final long id
    ) {
        GraphQLQuery<RemoteObject, GAuthentication> query = new GraphQLQuery<>() {

            private ReadableResource<AuthenticationReadable> authenticationReadableResource;

            @Override
            public void prepare(ResourceProvider resources) {
                authenticationReadableResource = resources.getReadableResource(AuthenticationReadable.class);
            }

            @Override
            public GAuthentication execute(RemoteObject source,
                                           ContextTransactionRequest context) throws PlatformException {
                AuthenticationReadable authentication = authenticationReadableResource.get(id, context.getTransaction());
                return authentication != null ? new GAuthentication(authentication) : null;
            }
        };
        return new GAccessQuery<>(query, CorePrivilege.AUTHENTICATION, AccessOperation.READ);
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Список типов аутентификаций")
    public static GraphQLQuery<RemoteObject, ArrayList<GAuthenticationType>> getAuthenticationTypes() {
        return new GAccessQuery<>(new AuthenticationTypesQuery(), CorePrivilege.AUTHENTICATION, AccessOperation.READ);
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Список аутентификаций")
    public static GraphQLQuery<RemoteObject, GAuthenticationListResult> getAuthenticationList(
            @GraphQLDescription("Колонка сортировки")
            @NonNull @GraphQLName(SORTING_COLUMN) final AuthenticationSortingColumn sortingColumn,
            @GraphQLDescription("Направление сортировки")
            @NonNull @GraphQLName(SORTING_DIRECTION) final SortingDirection sortingDirection,
            @GraphQLDescription("Фильтр по типу")
            @GraphQLName(TYPE_FILTER) final HashSet<String> typeFilter,
            @GraphQLDescription("Текстовый фильтр")
            @GraphQLName(TEXT_FILTER) final GTextFilter textFilter,
            @GraphQLDescription("Параметры пейджинга")
            @GraphQLName(PAGING) final GPaging paging,
            @GraphQLDescription("Aутентификации, обязательно присутствующие в списке")
            @GraphQLName(ALWAYS_COMING_DATA) final GInputItems alwaysComingData,
            @GraphQLDescription("Aутентификации, отображающиеся в начале списка")
            @GraphQLName(TOP_AUTHENTICATIONS) final GOptional<HashSet<Long>> topAuthentications
    ) {
        GraphQLQuery<RemoteObject, GAuthenticationListResult> query =
                new GraphQLQuery<>() {

                    private ReadableResource<AuthenticationReadable> authenticationReadableResource;
                    private AuthenticationListBuilder listBuilder;

                    @Override
                    public void prepare(ResourceProvider resources) {
                        listBuilder = new AuthenticationListBuilder(resources);
                        authenticationReadableResource = resources.getReadableResource(AuthenticationReadable.class);
                    }

                    @Override
                    public GAuthenticationListResult execute(RemoteObject source,
                                                             ContextTransactionRequest context) throws PlatformException {
                        Set<Long> alwaysComingItems = new PrimaryKeyValidator(true).validate(
                                alwaysComingData, authenticationReadableResource, context.getTransaction());
                        ListParam<Long> param = new ListParam.Builder<Long>()
                                .withTextFilter(textFilter)
                                .withPaging(paging)
                                .withAlwaysComingItems(alwaysComingItems)
                                .withTopItems(topAuthentications.isPresent() ? topAuthentications.get() : null)
                                .build();
                        ListResult<AuthenticationReadable> result =
                                listBuilder.build(param, typeFilter, sortingColumn, sortingDirection, context);
                        return new GAuthenticationListResult(result);
                    }
                };
        return new GAccessQuery<>(query, CorePrivilege.AUTHENTICATION, AccessOperation.READ);
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Autocomplete по аутентификациям")
    public static GraphQLQuery<RemoteObject, GAuthenticationAutocompleteResult> getAuthenticationAutocomplete(
            @GraphQLDescription("Текстовый фильтр")
            @GraphQLName(TEXT_FILTER) final GTextFilter textFilter,
            @GraphQLDescription("Идентификаторы исключаемых из выдачи аутентификаций")
            @GraphQLName(EXCLUDED_AUTHENTICATIONS) final HashSet<Long> excludedAuthentications,
            @GraphQLDescription("Параметры пейджинга")
            @GraphQLName(PAGING) final GPaging paging
    ) {
        GraphQLQuery<RemoteObject, GAuthenticationAutocompleteResult> query =
                new GraphQLQuery<>() {

                    private ReadableResource<AuthenticationReadable> authenticationReadableResource;
                    private AuthenticationAutocomplete authenticationAutocomplete;

                    @Override
                    public void prepare(ResourceProvider resources) {
                        authenticationReadableResource = resources.getReadableResource(AuthenticationReadable.class);
                        authenticationAutocomplete = new AuthenticationAutocomplete(resources);
                    }

                    @Override
                    public GAuthenticationAutocompleteResult execute(RemoteObject source, ContextTransactionRequest context)
                            throws PlatformException {
                        HashSet<Long> validExcludedAuthentications = new PrimaryKeyValidator(true).validate(
                                excludedAuthentications, authenticationReadableResource, context.getTransaction());
                        LightAutocompleteResult<AuthenticationReadable> result = authenticationAutocomplete.execute(
                                textFilter, validExcludedAuthentications, paging, context);
                        return new GAuthenticationAutocompleteResult(result);
                    }
                };
        return new GAccessQuery<>(query, CorePrivilege.AUTHENTICATION, AccessOperation.READ);
    }

    @GraphQLField
    @GraphQLAuthControl({ UnauthorizedContext.class })
    @GraphQLDescription("Список используемых для входа в систему типов аутентификаций")
    public static GraphQLQuery<RemoteObject, ArrayList<GAuthenticationType>> getUsedAuthenticationTypes() {
        return new UsedAuthenticationTypesQuery();
    }

    @GraphQLField
    @GraphQLAuthControl(UnauthorizedContext.class)
    @GraphQLDescription("Сложность пароля")
    public static GraphQLQuery<RemoteObject, GComplexPasswordOutput> getComplexPassword() {
        return new GraphQLQuery<>() {

            private ReadableResource<AuthenticationReadable> authenticationReadableResource;
            private CoreConfigGetter configGetter;

            @Override
            public void prepare(ResourceProvider resources) {
                authenticationReadableResource = resources.getReadableResource(AuthenticationReadable.class);
                configGetter = new CoreConfigGetter(resources);
            }

            @Override
            public GComplexPasswordOutput execute(RemoteObject source,
                                                  ContextTransactionRequest context) throws PlatformException {
                QueryTransaction transaction = context.getTransaction();
                HashFilter filter = new HashFilter(AuthenticationReadable.FIELD_TYPE,
                        CoreSubsystemConsts.AuthenticationTypes.INTEGRATED);
                if (authenticationReadableResource.find(filter, transaction) == null) {
                    return null;
                }
                AuthenticationConfig authenticationConfig = configGetter.getAuthenticationConfig(transaction);
                return authenticationConfig.getComplexPassword() != null ?
                        new GComplexPasswordOutput(authenticationConfig.getComplexPassword()) : null;
            }
        };
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Количество аутентификаций")
    public static GraphQLQuery<RemoteObject, Integer> getAuthenticationCount() {
        GraphQLQuery<RemoteObject, Integer> query = new GraphQLQuery<>() {

            private ReadableResource<AuthenticationReadable> authenticationReadableResource;

            @Override
            public void prepare(ResourceProvider resources) {
                authenticationReadableResource = resources.getReadableResource(AuthenticationReadable.class);
            }

            @Override
            public Integer execute(RemoteObject source, ContextTransactionRequest context) throws PlatformException {
                int[] count = new int[]{ 0 };
                authenticationReadableResource.forEach(authentication -> count[0]++, context.getTransaction());
                return count[0];
            }
        };
        return new GAccessQuery<>(query, CorePrivilege.AUTHENTICATION, AccessOperation.READ);
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Список доступных для создания типов аутентификации")
    public static GraphQLQuery<RemoteObject, ArrayList<GAuthenticationType>> getAvailableForCreatingAuthenticationTypes() {
        return new GAccessQuery<>(new GraphQLQuery<>() {

            private RCExecutor<RCAuthenticationType> authenticationTypeRCExecutor;

            @Override
            public void prepare(ResourceProvider resources) {
                authenticationTypeRCExecutor = new RCExecutor<>(resources, RCAuthenticationType.class);
            }

            @Override
            public ArrayList<GAuthenticationType> execute(RemoteObject source, ContextTransactionRequest context) throws PlatformException {
                ArrayList<String> availableForCreatingTypes = new ArrayList<>();
                authenticationTypeRCExecutor.exec(ctrl -> availableForCreatingTypes.addAll(ctrl.getAvailableForCreatingType(context)));
                return availableForCreatingTypes.stream()
                        .map(GAuthenticationType::new)
                        .collect(Collectors.toCollection(ArrayList::new));

            }
        }, CorePrivilege.AUTHENTICATION, AccessOperation.READ);
    }
}
