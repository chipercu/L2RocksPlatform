package com.fuzzy.subsystem.core.graphql.query.employee;

import com.infomaximum.cluster.core.remote.struct.RemoteObject;
import com.infomaximum.cluster.graphql.anotation.GraphQLDescription;
import com.infomaximum.cluster.graphql.anotation.GraphQLField;
import com.infomaximum.cluster.graphql.anotation.GraphQLName;
import com.infomaximum.cluster.graphql.anotation.GraphQLTypeOutObject;
import com.infomaximum.cluster.graphql.struct.GOptional;
import com.infomaximum.database.domainobject.DomainObject;
import com.infomaximum.database.domainobject.filter.HashFilter;
import com.infomaximum.platform.component.frontend.authcontext.UnauthorizedContext;
import com.infomaximum.platform.component.frontend.context.ContextTransactionRequest;
import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.QueryTransaction;
import com.infomaximum.platform.querypool.ReadableResource;
import com.infomaximum.platform.querypool.ResourceProvider;
import com.infomaximum.platform.sdk.graphql.annotation.GraphQLAuthControl;
import com.infomaximum.platform.sdk.graphql.customfield.graphqlquery.GraphQLQuery;
import com.fuzzy.subsystem.core.access.CorePrivilege;
import com.fuzzy.subsystem.core.authcontext.employee.EmployeeAuthContext;
import com.fuzzy.subsystem.core.autocomplete.EmployeeAtomicAutocomplete;
import com.fuzzy.subsystem.core.autocomplete.EmployeeAutocomplete;
import com.fuzzy.subsystem.core.domainobject.accessrole.AccessRoleReadable;
import com.fuzzy.subsystem.core.domainobject.authentication.AuthenticationReadable;
import com.fuzzy.subsystem.core.domainobject.department.DepartmentReadable;
import com.fuzzy.subsystem.core.domainobject.employee.EmployeeReadable;
import com.fuzzy.subsystem.core.domainobject.employeedata.EmployeeDataReadable;
import com.fuzzy.subsystem.core.employeeaccess.ManagerEmployeeAccess;
import com.fuzzy.subsystem.core.employeeaccess.ManagerEmployeeAccessGetter;
import com.fuzzy.subsystem.core.graphql.query.employee.autocomplete.GDepartmentEmployeeAutocompleteResult;
import com.fuzzy.subsystem.core.graphql.query.employee.autocomplete.GEmployeeAutocompleteResult;
import com.fuzzy.subsystem.core.graphql.query.employee.tree.GEmployeeTreeResult;
import com.fuzzy.subsystem.core.tree.employee.EmployeeTreeBuilder;
import com.fuzzy.subsystem.core.tree.employee.EmployeeTreeParam;
import com.fuzzy.subsystem.frontend.authcontext.AuthorizedContext;
import com.fuzzy.subsystems.access.AccessOperation;
import com.fuzzy.subsystems.access.AccessUtils;
import com.fuzzy.subsystems.autocomplete.LightAutocomplete;
import com.fuzzy.subsystems.graphql.enums.EnableMonitoringType;
import com.fuzzy.subsystems.graphql.input.*;
import com.fuzzy.subsystems.graphql.out.GKeyValue;
import com.fuzzy.subsystems.graphql.query.GAccessQuery;
import com.fuzzy.subsystems.graphql.query.GPrimaryKeyQuery;
import com.fuzzy.subsystems.tree.Tree;
import com.fuzzy.subsystems.utils.PrimaryKeyValidator;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.function.Function;

@GraphQLTypeOutObject("employee_query")
public class GQueryEmployee {

    private static final String ID = "id";
    private static final String TEXT_FILTER = "text_filter";
    private static final String EXCLUDED_DEPARTMENTS = "excluded_departments";
    private static final String EXCLUDED_EMPLOYEES = "excluded_employees";
    private static final String ACCESS_ROLE_FILTER = "access_role_filter";
    private static final String AUTHENTICATION_FILTER = "authentication_filter";
    private static final String PAGING = "paging";
    private static final String ID_FILTER = "id_filter";
    private static final String ALWAYS_COMING_DATA = "always_coming_data";
    private static final String MONITORING_TYPES = "monitoring_types";
    private static final String TOP_ITEMS = "top_items";

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Коллекция сотрудники")
    public static GraphQLQuery<RemoteObject, ArrayList<GEmployee>> getEmployees() {
        return new GraphQLQuery<>() {

            private ReadableResource<EmployeeReadable> employeeReadableResource;
            private ManagerEmployeeAccessGetter managerEmployeeAccessGetter;

            @Override
            public void prepare(ResourceProvider resources) {
                employeeReadableResource = resources.getReadableResource(EmployeeReadable.class);
                managerEmployeeAccessGetter = new ManagerEmployeeAccessGetter(resources);
            }

            @Override
            public ArrayList<GEmployee> execute(RemoteObject source, ContextTransactionRequest context) throws PlatformException {
                ArrayList<GEmployee> gEmployees = new ArrayList<>();
                ManagerEmployeeAccess access = managerEmployeeAccessGetter.getAccess(context);
                employeeReadableResource.forEach(employee -> {
                    if (access.checkEmployee(employee.getId())) {
                        gEmployees.add(new GEmployee(employee));
                    }
                }, context.getTransaction());
                return gEmployees;
            }
        };
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Сотрудник по идентификатору")
    public static GraphQLQuery<RemoteObject, GEmployee> getEmployee(
            @GraphQLDescription("Идентификатор сотрудника")
            @NonNull @GraphQLName(ID) final long employeeId
    ) {
        GraphQLQuery<RemoteObject, GEmployee> query =
                new GPrimaryKeyQuery<>(EmployeeReadable.class, GEmployee::new) {
                    @Override
                    protected Long getIdentificator(RemoteObject source, QueryTransaction transaction) {
                        return employeeId;
                    }
                };
        return new GraphQLQuery<>() {

            private ManagerEmployeeAccessGetter managerEmployeeAccessGetter;

            @Override
            public void prepare(ResourceProvider resources) {
                managerEmployeeAccessGetter = new ManagerEmployeeAccessGetter(resources);
                query.prepare(resources);
            }

            @Override
            public GEmployee execute(RemoteObject source, ContextTransactionRequest context) throws PlatformException {
                Long currentEmployeeId = null;
                UnauthorizedContext authContext = context.getSource().getAuthContext();
                if (authContext instanceof EmployeeAuthContext) {
                    currentEmployeeId = ((EmployeeAuthContext) authContext).getEmployeeId();
                }
                if (currentEmployeeId != null && Objects.equals(currentEmployeeId, employeeId)
                        || managerEmployeeAccessGetter.getAccess(context).checkEmployee(employeeId)) {
                    return query.execute(source, context);
                }
                return null;
            }
        };
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Autocomplete по сотрудникам и отделам с учетом доступа к сотрудникам/отделам")
    public static GraphQLQuery<RemoteObject, GDepartmentEmployeeAutocompleteResult> getDepartmentEmployeeAutocomplete(
            @GraphQLDescription("Текстовый фильтр")
            @GraphQLName(TEXT_FILTER) final GTextFilter textFilter,
            @GraphQLDescription("Идентификаторы исключаемых из выдачи отделов")
            @GraphQLName(EXCLUDED_DEPARTMENTS) final HashSet<Long> excludedDepartments,
            @GraphQLDescription("Идентификаторы исключаемых из выдачи сотрудников")
            @GraphQLName(EXCLUDED_EMPLOYEES) final HashSet<Long> excludedEmployees,
            @GraphQLDescription("Параметры пейджинга")
            @GraphQLName(PAGING) final GPaging paging
    ) {
        GraphQLQuery<RemoteObject, GDepartmentEmployeeAutocompleteResult> query =
                new GraphQLQuery<>() {

                    private ReadableResource<DepartmentReadable> departmentReadableResource;
                    private ReadableResource<EmployeeReadable> employeeReadableResource;
                    private EmployeeAutocomplete employeeAutocomplete;

                    @Override
                    public void prepare(ResourceProvider resources) {
                        departmentReadableResource = resources.getReadableResource(DepartmentReadable.class);
                        employeeReadableResource = resources.getReadableResource(EmployeeReadable.class);
                        employeeAutocomplete = new EmployeeAutocomplete(resources);
                    }

                    @Override
                    public GDepartmentEmployeeAutocompleteResult execute(
                            RemoteObject source,
                            ContextTransactionRequest context
                    ) throws PlatformException {
                        UnauthorizedContext authContext = context.getSource().getAuthContext();
                        if (authContext instanceof EmployeeAuthContext) {
                            long employeeId = ((EmployeeAuthContext) authContext).getEmployeeId();
                            employeeAutocomplete.setAuthEmployeeId(employeeId);
                        }
                        QueryTransaction transaction = context.getTransaction();
                        PrimaryKeyValidator pKeyValidator = new PrimaryKeyValidator(true);
                        HashSet<Long> validExcludedDepartments =
                                pKeyValidator.validate(excludedDepartments, departmentReadableResource, transaction);
                        HashSet<Long> validExcludedEmployees =
                                pKeyValidator.validate(excludedEmployees, employeeReadableResource, transaction);
                        return new GDepartmentEmployeeAutocompleteResult(
                                employeeAutocomplete.execute(
                                        textFilter,
                                        validExcludedDepartments,
                                        validExcludedEmployees,
                                        paging,
                                        context
                                ));
                    }
                };
        return new GAccessQuery<>(query, CorePrivilege.EMPLOYEES, AccessOperation.READ);
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Autocomplete по сотрудникам с учетом доступа к сотрудникам")
    public static GraphQLQuery<RemoteObject, GEmployeeAutocompleteResult> getEmployeeAutocomplete(
            @GraphQLDescription("Текстовый фильтр")
            @GraphQLName(TEXT_FILTER) final GTextFilter textFilter,
            @GraphQLDescription("Идентификаторы исключаемых из выдачи сотрудников")
            @GraphQLName(EXCLUDED_EMPLOYEES) final HashSet<Long> excludedEmployees,
            @GraphQLDescription("Параметры пейджинга")
            @GraphQLName(PAGING) final GPaging paging
    ) {
        GraphQLQuery<RemoteObject, GEmployeeAutocompleteResult> query =
                new GraphQLQuery<>() {

                    private ReadableResource<EmployeeReadable> employeeReadableResource;
                    private EmployeeAtomicAutocomplete employeeAtomicAutocomplete;

                    @Override
                    public void prepare(ResourceProvider resources) {
                        employeeReadableResource = resources.getReadableResource(EmployeeReadable.class);
                        employeeAtomicAutocomplete = new EmployeeAtomicAutocomplete(resources);
                    }

                    @Override
                    public GEmployeeAutocompleteResult execute(RemoteObject source, ContextTransactionRequest context)
                            throws PlatformException {
                        UnauthorizedContext authContext = context.getSource().getAuthContext();
                        if (authContext instanceof EmployeeAuthContext) {
                            long employeeId = ((EmployeeAuthContext) authContext).getEmployeeId();
                            employeeAtomicAutocomplete.setAuthEmployeeId(employeeId);
                        }
                        PrimaryKeyValidator pKeyValidator = new PrimaryKeyValidator(true);
                        HashSet<Long> validExcludedEmployees =
                                pKeyValidator.validate(excludedEmployees, employeeReadableResource, context.getTransaction());
                        return new GEmployeeAutocompleteResult(
                                new LightAutocomplete<>(employeeAtomicAutocomplete).execute(
                                        textFilter, validExcludedEmployees, paging, context));
                    }
                };
        return new GAccessQuery<>(query, CorePrivilege.EMPLOYEES, AccessOperation.READ);
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Дерево сотрудников с учетом доступа к сотрудникам и отделам")
    public static GraphQLQuery<RemoteObject, GEmployeeTreeResult> getEmployeeTree(
            @GraphQLDescription("Текстовый фильтр")
            @GraphQLName(TEXT_FILTER) final GTextFilter textFilter,
            @GraphQLDescription("Фильтр по отделам и сотрудникам")
            @GraphQLName(ID_FILTER) final GStandardFilter idFilter,
            @GraphQLDescription("Фильтр по ролям доступа сотрудников")
            @GraphQLName(ACCESS_ROLE_FILTER) final GItemsFilter accessRolesFilter,
            @GraphQLDescription("Фильтр по аутентификациям")
            @GraphQLName(AUTHENTICATION_FILTER) final GItemsFilter authenticationFilter,
            @GraphQLDescription("Отделы и сотрудники, обязательно присутствующие в дереве")
            @GraphQLName(ALWAYS_COMING_DATA) final GInputNodesItems alwaysComingData,
            @GraphQLDescription("Параметры пейджинга")
            @GraphQLName(PAGING) final GTreePaging paging,
            @GraphQLDescription("Фильтр по режимам включения мониторинга для сотрудника")
            @GraphQLName(MONITORING_TYPES) final ArrayList<EnableMonitoringType> monitoringTypes,
            @GraphQLDescription("Элементы, отображающиеся в начале списка")
            @GraphQLName(TOP_ITEMS) final GInputNodesItems topItems
    ) {
        GraphQLQuery<RemoteObject, GEmployeeTreeResult> query = new GraphQLQuery<>() {

            private ReadableResource<DepartmentReadable> departmentReadableResource;
            private ReadableResource<EmployeeReadable> employeeReadableResource;
            private ReadableResource<AccessRoleReadable> accessRoleReadableResource;
            private ReadableResource<AuthenticationReadable> authenticationReadableResource;
            private ManagerEmployeeAccessGetter managerEmployeeAccessGetter;
            private EmployeeTreeBuilder treeBuilder;

            @Override
            public void prepare(ResourceProvider resources) {
                departmentReadableResource = resources.getReadableResource(DepartmentReadable.class);
                employeeReadableResource = resources.getReadableResource(EmployeeReadable.class);
                accessRoleReadableResource = resources.getReadableResource(AccessRoleReadable.class);
                authenticationReadableResource = resources.getReadableResource(AuthenticationReadable.class);
                managerEmployeeAccessGetter = new ManagerEmployeeAccessGetter(resources);
                treeBuilder = new EmployeeTreeBuilder(resources);
            }

            @Override
            public GEmployeeTreeResult execute(RemoteObject source, ContextTransactionRequest context)
                    throws PlatformException {
                if (accessRolesFilter != null) {
                    AccessUtils.validateAccess(context, CorePrivilege.ACCESS_ROLE, AccessOperation.READ);
                }
                if (authenticationFilter != null) {
                    AccessUtils.validateAccess(context, CorePrivilege.AUTHENTICATION, AccessOperation.READ);
                }

                QueryTransaction transaction = context.getTransaction();
                UnauthorizedContext authContext = context.getSource().getAuthContext();
                EmployeeTreeParam treeParam = new EmployeeTreeParam();
                ManagerEmployeeAccess access = null;
                if (authContext instanceof EmployeeAuthContext) {
                    long employeeId = ((EmployeeAuthContext) authContext).getEmployeeId();
                    treeParam.authEmployeeId = employeeId;
                    access = managerEmployeeAccessGetter.getAccess(employeeId, transaction);
                }
                treeParam.accessRoleFilter = validateFilter(accessRolesFilter, accessRoleReadableResource, transaction);
                treeParam.authenticationFilter = validateFilter(authenticationFilter, authenticationReadableResource, transaction);
                treeParam.idFilter = validateEmployeesFilter(idFilter, departmentReadableResource, employeeReadableResource, access, transaction);
                treeParam.alwaysComingData = validateEmployeesFilter(alwaysComingData, departmentReadableResource, employeeReadableResource, access, transaction);
                treeParam.paging = validatePaging(paging, departmentReadableResource, transaction);
                treeParam.monitoringTypes = monitoringTypes;
                if (textFilter != null && textFilter.isSpecified()) {
                    treeParam.textFilter = textFilter.getText();
                }
                treeParam.topElements = topItems;
                Tree<DepartmentReadable, EmployeeReadable> tree = treeBuilder.build(treeParam, context);
                return new GEmployeeTreeResult(tree);
            }
        };
        return new GAccessQuery<>(query, CorePrivilege.EMPLOYEES, AccessOperation.READ);
    }

    @GraphQLField
    @GraphQLAuthControl({ EmployeeAuthContext.class })
    @GraphQLDescription("Дерево сотрудников без учёта доступа к сотрудникам и отделам")
    public static GraphQLQuery<RemoteObject, GEmployeeTreeResult> getEmployeeWithoutAccess(
            @GraphQLDescription("Текстовый фильтр")
            @GraphQLName(TEXT_FILTER) final GTextFilter textFilter,
            @GraphQLDescription("Фильтр по отделам и сотрудникам")
            @GraphQLName(ID_FILTER) final GStandardFilter idFilter,
            @GraphQLDescription("Фильтр по ролям доступа сотрудников")
            @GraphQLName(ACCESS_ROLE_FILTER) final GItemsFilter accessRolesFilter,
            @GraphQLDescription("Фильтр по аутентификациям")
            @GraphQLName(AUTHENTICATION_FILTER) final GItemsFilter authenticationFilter,
            @GraphQLDescription("Отделы и сотрудники, обязательно присутствующие в дереве")
            @GraphQLName(ALWAYS_COMING_DATA) final GInputNodesItems alwaysComingData,
            @GraphQLDescription("Параметры пейджинга")
            @GraphQLName(PAGING) final GTreePaging paging,
            @GraphQLDescription("Фильтр по режимам включения мониторинга для сотрудника")
            @GraphQLName(MONITORING_TYPES) final ArrayList<EnableMonitoringType> monitoringTypes,
            @GraphQLDescription("Элементы, отображающиеся в начале списка")
            @GraphQLName(TOP_ITEMS) final GInputNodesItems topItems
    ) {
        return new GraphQLQuery<>() {

            private ReadableResource<DepartmentReadable> departmentReadableResource;
            private ReadableResource<EmployeeReadable> employeeReadableResource;
            private ReadableResource<AccessRoleReadable> accessRoleReadableResource;
            private ReadableResource<AuthenticationReadable> authenticationReadableResource;
            private EmployeeTreeBuilder treeBuilder;

            @Override
            public void prepare(ResourceProvider resources) {
                departmentReadableResource = resources.getReadableResource(DepartmentReadable.class);
                employeeReadableResource = resources.getReadableResource(EmployeeReadable.class);
                accessRoleReadableResource = resources.getReadableResource(AccessRoleReadable.class);
                authenticationReadableResource = resources.getReadableResource(AuthenticationReadable.class);
                treeBuilder = new EmployeeTreeBuilder(resources);
            }

            @Override
            public GEmployeeTreeResult execute(RemoteObject source, ContextTransactionRequest context)
                    throws PlatformException {
                if (accessRolesFilter != null) {
                    AccessUtils.validateAccess(context, CorePrivilege.ACCESS_ROLE, AccessOperation.READ);
                }
                if (authenticationFilter != null) {
                    AccessUtils.validateAccess(context, CorePrivilege.AUTHENTICATION, AccessOperation.READ);
                }

                QueryTransaction transaction = context.getTransaction();
                EmployeeTreeParam treeParam = new EmployeeTreeParam();
                treeParam.accessRoleFilter = validateFilter(accessRolesFilter, accessRoleReadableResource, transaction);
                treeParam.authenticationFilter = validateFilter(authenticationFilter, authenticationReadableResource, transaction);
                treeParam.idFilter = validateEmployeesFilter(idFilter, departmentReadableResource, employeeReadableResource, null, transaction);
                treeParam.alwaysComingData = validateEmployeesFilter(alwaysComingData, departmentReadableResource, employeeReadableResource, null, transaction);
                treeParam.paging = validatePaging(paging, departmentReadableResource, transaction);
                treeParam.monitoringTypes = monitoringTypes;
                if (textFilter != null && textFilter.isSpecified()) {
                    treeParam.textFilter = textFilter.getText();
                }
                treeParam.topElements = topItems;
                Tree<DepartmentReadable, EmployeeReadable> tree = treeBuilder.build(treeParam, context);
                return new GEmployeeTreeResult(tree);
            }
        };
    }

    @GraphQLField
    @GraphQLAuthControl({ EmployeeAuthContext.class })
    @GraphQLDescription("Хранилище данных для текущего сотрудника")
    public static GraphQLQuery<RemoteObject, ArrayList<GKeyValue>> getDataForCurrentEmployee(
            @GraphQLDescription("Ключи")
            @GraphQLName("keys") GOptional<HashSet<String>> keys
    ) {
        return new GraphQLQuery<>() {

            private ReadableResource<EmployeeDataReadable> employeeDataReadableResource;

            @Override
            public void prepare(ResourceProvider resources) {
                employeeDataReadableResource = resources.getReadableResource(EmployeeDataReadable.class);
            }

            @Override
            public ArrayList<GKeyValue> execute(RemoteObject source, ContextTransactionRequest context)
                    throws PlatformException {
                QueryTransaction transaction = context.getTransaction();
                long employeeId = ((EmployeeAuthContext) context.getSource().getAuthContext()).getEmployeeId();
                ArrayList<GKeyValue> result = new ArrayList<>();
                if (keys.isPresent() && keys.get() != null) {
                    for (String key : keys.get()) {
                        HashFilter filter = new HashFilter(EmployeeDataReadable.FIELD_EMPLOYEE_ID, employeeId)
                                .appendField(EmployeeDataReadable.FIELD_KEY, key);
                        employeeDataReadableResource.forEach(filter, employeeData ->
                                result.add(new GKeyValue(employeeData.getKey(), employeeData.getValue())), transaction);
                    }
                } else {
                    HashFilter filter = new HashFilter(EmployeeDataReadable.FIELD_EMPLOYEE_ID, employeeId);
                    employeeDataReadableResource.forEach(filter, employeeData ->
                            result.add(new GKeyValue(employeeData.getKey(), employeeData.getValue())), transaction);
                }
                return result;
            }
        };
    }

    public static GTreePaging validatePaging(
            GTreePaging inPaging,
            ReadableResource<DepartmentReadable> departmentReadableResource,
            QueryTransaction transaction
    ) throws PlatformException {
        GTreePaging outPaging = null;
        if (inPaging != null) {
            ArrayList<GPagingElement> elements = null;
            if (inPaging.getElements() != null) {
                ArrayList<GPagingElement> outElements = new ArrayList<>();
                for (GPagingElement element : inPaging.getElements()) {
                    if (element != null &&
                            departmentReadableResource.get(element.getId(), transaction) != null) {
                        outElements.add(element);
                    }
                }
                elements = outElements;
            }
            outPaging = new GTreePaging(elements, inPaging.getRootLimit(), inPaging.getDefaultLimit());
        }
        return outPaging;
    }

    private static HashSet<Long> validateAccess(HashSet<Long> inElements,
                                                Function<Long, Boolean> checker) {
        if (inElements == null) {
            return null;
        }
        HashSet<Long> outElements = new HashSet<>();
        for (Long elementId : inElements) {
            if (checker.apply(elementId)) {
                outElements.add(elementId);
            }
        }
        return outElements;
    }

    private static GStandardFilter validateEmployeesFilter(
            GStandardFilter inFilter,
            ReadableResource<DepartmentReadable> departmentReadableResource,
            ReadableResource<EmployeeReadable> employeeReadableResource,
            ManagerEmployeeAccess access,
            QueryTransaction transaction
    ) throws PlatformException {
        if (inFilter == null) {
            return null;
        }
        GStandardFilter outFilter = new PrimaryKeyValidator(true).validate(
                inFilter, departmentReadableResource, employeeReadableResource, transaction);
        if (access != null) {
            outFilter = new GStandardFilter(
                    outFilter.getOperation(),
                    validateAccess(outFilter.getNodes(), access::checkDepartment),
                    validateAccess(outFilter.getItems(), access::checkEmployee)
            );
        }
        return outFilter;
    }

    private static GInputNodesItems validateEmployeesFilter(
            GInputNodesItems inFilter,
            ReadableResource<DepartmentReadable> departmentReadableResource,
            ReadableResource<EmployeeReadable> employeeReadableResource,
            ManagerEmployeeAccess access,
            QueryTransaction transaction
    ) throws PlatformException {
        if (inFilter == null) {
            return null;
        }
        GInputNodesItems outFilter = new PrimaryKeyValidator(true).validate(
                inFilter, departmentReadableResource, employeeReadableResource, transaction);
        if (access != null) {
            outFilter = new GInputNodesItems(
                    validateAccess(outFilter.getNodes(), access::checkDepartment),
                    validateAccess(outFilter.getItems(), access::checkEmployee)
            );
        }
        return outFilter;
    }

    private static <T extends DomainObject> GItemsFilter validateFilter(
            GItemsFilter inFilter,
            ReadableResource<T> readableResource,
            QueryTransaction transaction
    ) throws PlatformException {
        return new PrimaryKeyValidator(true).validate(inFilter, readableResource, transaction);
    }
}