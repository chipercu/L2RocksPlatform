package com.fuzzy.subsystem.core.graphql.query.accessrole;

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
import com.fuzzy.subsystem.core.access.CorePrivilege;
import com.fuzzy.subsystem.core.autocomplete.AccessRoleAutocomplete;
import com.fuzzy.subsystem.core.domainobject.accessrole.AccessRoleReadable;
import com.fuzzy.subsystem.core.domainobject.department.DepartmentReadable;
import com.fuzzy.subsystem.core.domainobject.employee.EmployeeReadable;
import com.fuzzy.subsystem.core.filterhandler.AccessibleEmployeeFilterHandler;
import com.fuzzy.subsystem.core.graphql.query.accessrole.autocomplete.GAccessRoleAutocompleteResult;
import com.fuzzy.subsystem.core.graphql.query.accessrole.list.GAccessRoleListResult;
import com.fuzzy.subsystem.core.list.AccessRoleListBuilder;
import com.fuzzy.subsystem.core.list.AccessRoleListParam;
import com.fuzzy.subsystem.frontend.authcontext.AuthorizedContext;
import com.fuzzy.subsystems.access.AccessOperation;
import com.fuzzy.subsystems.graphql.input.GInputItems;
import com.fuzzy.subsystems.graphql.input.GPaging;
import com.fuzzy.subsystems.graphql.input.GStandardFilter;
import com.fuzzy.subsystems.graphql.input.GTextFilter;
import com.fuzzy.subsystems.graphql.query.GAccessQuery;
import com.fuzzy.subsystems.graphql.query.GPrimaryKeyQueryImpl;
import com.fuzzy.subsystems.utils.PrimaryKeyValidator;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashSet;
import java.util.Set;

@GraphQLTypeOutObject("access_role_query")
public class GQueryAccessRole {

    private static final String ID = "id";
    private static final String TEXT_FILTER = "text_filter";
    private static final String EXCLUDED_ACCESS_ROLES = "excluded_access_roles";
    private static final String PAGING = "paging";
    private static final String EMPLOYEE_FILTER = "employee_filter";
    private static final String ALWAYS_COMING_DATA = "always_coming_data";
    private static final String TOP_ACCESS_ROLES = "top_access_roles";

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Роль доступа по идентификатору")
    public static GraphQLQuery<RemoteObject, GAccessRole> getAccessRole(
            @GraphQLDescription("Идентификатор роли доступа")
            @NonNull @GraphQLName(ID) final long accessRoleId
    ) {
        GraphQLQuery<RemoteObject, GAccessRole> query =
                new GPrimaryKeyQueryImpl<>(AccessRoleReadable.class, GAccessRole::new, accessRoleId);
        return new GAccessQuery<>(query, CorePrivilege.ACCESS_ROLE, AccessOperation.READ);
    }

    @GraphQLField(value = "access_role_autocomplete")
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Autocomplete по ролям доступа")
    public static GraphQLQuery<RemoteObject, GAccessRoleAutocompleteResult> getAccessRoleAutocomplete(
            @GraphQLDescription("Текстовый фильтр")
            @GraphQLName(TEXT_FILTER) final GTextFilter textFilter,
            @GraphQLDescription("Идентификаторы исключаемых из выдачи ролей доступа")
            @GraphQLName(EXCLUDED_ACCESS_ROLES) final HashSet<Long> excludedAccessRoles,
            @GraphQLDescription("Параметры пейджинга")
            @GraphQLName(PAGING) final GPaging paging
    ) {
        GraphQLQuery<RemoteObject, GAccessRoleAutocompleteResult> query =
                new GraphQLQuery<RemoteObject, GAccessRoleAutocompleteResult>() {

                    private ReadableResource<AccessRoleReadable> accessRoleReadableResource;
                    private AccessRoleAutocomplete accessRoleAutocomplete;

                    @Override
                    public void prepare(ResourceProvider resources) {
                        accessRoleReadableResource = resources.getReadableResource(AccessRoleReadable.class);
                        accessRoleAutocomplete = new AccessRoleAutocomplete(resources);
                    }

                    @Override
                    public GAccessRoleAutocompleteResult execute(RemoteObject source, ContextTransactionRequest context)
                            throws PlatformException {
                        HashSet<Long> validExcludedAccessRoles = new PrimaryKeyValidator(true).validate(
                                excludedAccessRoles, accessRoleReadableResource, context.getTransaction());
                        return new GAccessRoleAutocompleteResult(
                                accessRoleAutocomplete.execute(textFilter, validExcludedAccessRoles, paging, context));
                    }
                };
        return new GAccessQuery<>(query, CorePrivilege.ACCESS_ROLE, AccessOperation.READ);
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Роли доступа с учетом фильтров")
    public static GraphQLQuery<RemoteObject, GAccessRoleListResult> getAccessRoleList(
            @GraphQLDescription("Текстовый фильтр")
            @GraphQLName(TEXT_FILTER)  final GTextFilter textFilter,
            @GraphQLDescription("Фильтр по сотрудникам")
            @GraphQLName(EMPLOYEE_FILTER) final GStandardFilter employeeFilter,
            @GraphQLDescription("Роли доступа, обязательно присутствующие в списке")
            @GraphQLName(ALWAYS_COMING_DATA) final GInputItems alwaysComingData,
            @GraphQLDescription("Параметры пейджинга")
            @GraphQLName(PAGING) final GPaging paging,
            @GraphQLDescription("Роли доступа, отображающиеся в начале списка")
            @GraphQLName(TOP_ACCESS_ROLES) final GOptional<HashSet<Long>> topAccessRoles
    ) {
        GraphQLQuery<RemoteObject, GAccessRoleListResult> query = new GraphQLQuery<RemoteObject, GAccessRoleListResult>() {

            private ReadableResource<AccessRoleReadable> accessRoleReadableResource;
            private ReadableResource<DepartmentReadable> departmentReadableResource;
            private ReadableResource<EmployeeReadable> employeeReadableResource;
            private AccessibleEmployeeFilterHandler employeeFilterHandler;
            private AccessRoleListBuilder listBuilder;

            @Override
            public void prepare(ResourceProvider resources) {
                accessRoleReadableResource = resources.getReadableResource(AccessRoleReadable.class);
                departmentReadableResource = resources.getReadableResource(DepartmentReadable.class);
                employeeReadableResource = resources.getReadableResource(EmployeeReadable.class);
                employeeFilterHandler = new AccessibleEmployeeFilterHandler(resources);
                listBuilder = new AccessRoleListBuilder(resources);
            }

            @Override
            public GAccessRoleListResult execute(RemoteObject source, ContextTransactionRequest context) throws PlatformException {
                QueryTransaction transaction = context.getTransaction();
                Set<Long> employees = null;
                if (employeeFilter != null) {
                    PrimaryKeyValidator primaryKeyValidator = new PrimaryKeyValidator(true);
                    GStandardFilter checkedEmployeeFilter = primaryKeyValidator.validate(
                            employeeFilter,
                            departmentReadableResource,
                            employeeReadableResource,
                            transaction
                    );
                    employees = employeeFilterHandler.get(checkedEmployeeFilter, context);
                }
                Set<Long> alwaysComingItems = alwaysComingData != null ? alwaysComingData.getItems() : null;
                alwaysComingItems = new PrimaryKeyValidator(true).validate(
                        alwaysComingItems, accessRoleReadableResource, transaction);
                AccessRoleListParam listParam = new AccessRoleListParam.Builder()
                        .withEmployeeFilter(employees)
                        .withTextFilter(textFilter)
                        .withAlwaysComingItems(alwaysComingItems)
                        .withPaging(paging)
                        .withTopItems(topAccessRoles.isPresent() ? topAccessRoles.get() : null)
                        .build();
                return new GAccessRoleListResult(listBuilder.build(listParam, context));
            }
        };
        return new GAccessQuery<>(query, CorePrivilege.ACCESS_ROLE, AccessOperation.READ);
    }
}
