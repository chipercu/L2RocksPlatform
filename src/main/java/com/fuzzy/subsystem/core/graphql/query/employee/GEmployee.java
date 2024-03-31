package com.fuzzy.subsystem.core.graphql.query.employee;

import com.fuzzy.cluster.graphql.anotation.GraphQLDescription;
import com.fuzzy.cluster.graphql.anotation.GraphQLField;
import com.fuzzy.cluster.graphql.anotation.GraphQLName;
import com.fuzzy.cluster.graphql.anotation.GraphQLTypeOutObject;
import com.fuzzy.database.domainobject.filter.HashFilter;
import com.fuzzy.platform.component.frontend.authcontext.UnauthorizedContext;
import com.fuzzy.platform.component.frontend.context.ContextTransactionRequest;
import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.QueryTransaction;
import com.fuzzy.platform.querypool.ReadableResource;
import com.fuzzy.platform.querypool.ResourceProvider;
import com.fuzzy.platform.querypool.iterator.IteratorEntity;
import com.fuzzy.platform.sdk.graphql.annotation.GraphQLAuthControl;
import com.fuzzy.platform.sdk.graphql.customfield.graphqlquery.GraphQLQuery;
import com.fuzzy.subsystem.core.CoreSubsystem;
import com.fuzzy.subsystem.core.access.CorePrivilege;
import com.fuzzy.subsystem.core.accessroleprivileges.EmployeePrivilegesGetter;
import com.fuzzy.subsystem.core.authcontext.employee.EmployeeAuthContext;
import com.fuzzy.subsystem.core.authcontext.employee.EmployeeSessionAuthContext;
import com.fuzzy.subsystem.core.config.CoreConfigDescription;
import com.fuzzy.subsystem.core.config.CoreConfigGetter;
import com.fuzzy.subsystem.core.config.DisplayNameFormat;
import com.fuzzy.subsystem.core.config.Language;
import com.fuzzy.subsystem.core.domainobject.accessrole.AccessRoleReadable;
import com.fuzzy.subsystem.core.domainobject.department.DepartmentReadable;
import com.fuzzy.subsystem.core.domainobject.employee.EmployeeReadable;
import com.fuzzy.subsystem.core.domainobject.employeeaccessrole.EmployeeAccessRoleReadable;
import com.fuzzy.subsystem.core.domainobject.employeephone.EmployeePhoneReadable;
import com.fuzzy.subsystem.core.employeeaccess.ManagerEmployeeAccessEnumerator;
import com.fuzzy.subsystem.core.employeeaccess.ManagerEmployeeAccessGetter;
import com.fuzzy.subsystem.core.graphql.query.authentication.GAuthenticationCollection;
import com.fuzzy.subsystem.core.graphql.query.department.GDepartment;
import com.fuzzy.subsystem.core.graphql.query.employee.additionalfields.AdditionalFieldValueListQuery;
import com.fuzzy.subsystem.core.graphql.query.employee.additionalfields.GFieldValueInterface;
import com.fuzzy.subsystem.core.graphql.query.privilege.GOutPrivilege;
import com.fuzzy.subsystem.core.grouping.enumerator.DepartmentGroupingEnumerator;
import com.fuzzy.subsystem.core.remote.fieldsgetter.RCFieldsGetter;
import com.fuzzy.subsystem.core.remote.fieldsgetter.SystemFieldDescription;
import com.fuzzy.subsystem.core.remote.integrations.RCIntegrationsExecutor;
import com.fuzzy.subsystem.core.utils.GEmployeeComparator;
import com.fuzzy.subsystem.frontend.authcontext.AuthorizedContext;
import com.fuzzy.subsystems.access.AccessOperation;
import com.fuzzy.subsystems.access.AccessOperationCollection;
import com.fuzzy.subsystems.access.AccessUtils;
import com.fuzzy.subsystems.access.PrivilegeEnum;
import com.fuzzy.subsystems.exception.GeneralExceptionBuilder;
import com.fuzzy.subsystems.graphql.GDomainObject;
import com.fuzzy.subsystems.graphql.input.GPaging;
import com.fuzzy.subsystems.graphql.query.GAccessQuery;
import com.fuzzy.subsystems.graphql.query.GParentsQuery;
import com.fuzzy.subsystems.graphql.query.GPrimaryKeyQuery;
import com.fuzzy.subsystems.grouping.GroupingEnumerator;
import com.fuzzy.subsystems.sorter.Sorter;
import com.fuzzy.subsystems.sorter.SorterUtil;
import com.fuzzy.subsystems.utils.ComparatorUtility;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

@GraphQLTypeOutObject("employee")
@GraphQLDescription("Сотрудник компании")
public class GEmployee extends GDomainObject<EmployeeReadable> implements
        GDepartmentEmployeeElement {

    private static final String PAGING = "paging";

    public GEmployee(EmployeeReadable source) {
        super(source);
    }

    @Override
    public long getId() {
        return getSource().getId();
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Имя сотрудника")
    public static GraphQLQuery<GEmployee, String> getFirstName() {
        return new GPrivateSettingAccessQuery<>(employee -> employee.getSource().getFirstName(), false,
                CorePrivilege.EMPLOYEES, AccessOperation.READ);
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Отчество сотрудника")
    public static GraphQLQuery<GEmployee, String> getPatronymic() {
        return new GPrivateSettingAccessQuery<>(employee -> employee.getSource().getPatronymic(), false,
                CorePrivilege.EMPLOYEES, AccessOperation.READ);
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Фамилия сотрудника")
    public static GraphQLQuery<GEmployee, String> getSecondName() {
        return new GPrivateSettingAccessQuery<>(employee -> employee.getSource().getSecondName(), false,
                CorePrivilege.EMPLOYEES, AccessOperation.READ);
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Табельный номер сотрудника")
    public static GraphQLQuery<GEmployee, String> getPersonnelNumber() {
        return new GEmployeeAccessQuery<>(
                new GAccessQuery<>(employee -> employee.getSource().getPersonnelNumber(),
                        CorePrivilege.EMPLOYEES, AccessOperation.READ)
        );
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Email сотрудника")
    public static GraphQLQuery<GEmployee, String> getEmail() {
        return new GEmployeeAccessQuery<>(
                new GAccessQuery<>(employee -> employee.getSource().getEmail(),
                        CorePrivilege.EMPLOYEES, AccessOperation.READ)
        );
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Логин сотрудника")
    public static GraphQLQuery<GEmployee, String> getLogin() {
        return new GraphQLQuery<>() {

            private ManagerEmployeeAccessGetter managerEmployeeAccessGetter;

            @Override
            public void prepare(ResourceProvider resources) {
                managerEmployeeAccessGetter = new ManagerEmployeeAccessGetter(resources);
            }

            @Override
            public String execute(GEmployee source, ContextTransactionRequest context) throws PlatformException {
                UnauthorizedContext authContext = context.getSource().getAuthContext();
                if (authContext instanceof EmployeeSessionAuthContext authContextUser) {
                    if (Objects.equals(source.getId(), authContextUser.getEmployeeId())) {
                        return source.getSource().getLogin();
                    }
                }
                if (!managerEmployeeAccessGetter.getAccess(context).checkEmployee(source.getId())) {
                    throw GeneralExceptionBuilder.buildAccessDeniedException();
                }
                AccessUtils.validateAccess(context, CorePrivilege.EMPLOYEE_ACCESS, AccessOperation.READ);
                return source.getSource().getLogin();
            }
        };
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Флаг установленного пароля")
    public static GraphQLQuery<GEmployee, Boolean> hasPassword() {
        return new GPrivateSettingAccessQuery<>(employee -> employee.getSource().hasPassword(), true,
                CorePrivilege.EMPLOYEE_ACCESS, AccessOperation.READ);
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Язык сотрудника")
    public static GraphQLQuery<GEmployee, Language> getLanguage() {
        return new GPrivateSettingAccessQuery<>(employee -> employee.getSource().getLanguage(), true,
                CorePrivilege.EMPLOYEES, AccessOperation.READ);
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Отдел сотрудника")
    public static GraphQLQuery<GEmployee, GDepartment> getDepartment() {
        GraphQLQuery<GEmployee, GDepartment> query = new GPrimaryKeyQuery<GEmployee, DepartmentReadable, GDepartment>(
                DepartmentReadable.class, GDepartment::new) {
            @Override
            protected Long getIdentificator(GEmployee source, QueryTransaction transaction) {
                return source.getSource().getDepartmentId();
            }
        };
        return new GEmployeeAccessQuery<>(
                new GAccessQuery<>(query, CorePrivilege.EMPLOYEES, AccessOperation.READ)
        );
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Цепочка дерева отдела сотрудника")
    public static GraphQLQuery<GEmployee, ArrayList<GDepartment>> getDepartments() {
        GraphQLQuery<GEmployee, ArrayList<GDepartment>> query = new GParentsQuery<GEmployee, DepartmentReadable, GDepartment>(
                DepartmentReadable.class, GDepartment::new) {
            @Override
            protected GroupingEnumerator getGroupingEnumerator(ResourceProvider resources) {
                return new DepartmentGroupingEnumerator(resources);
            }

            @Override
            protected Long getParentId(GEmployee source, QueryTransaction transaction) {
                return source.getSource().getDepartmentId();
            }
        };
        return new GEmployeeAccessQuery<>(
                new GAccessQuery<>(query, CorePrivilege.EMPLOYEES, AccessOperation.READ)
        );
    }

    @GraphQLField("display_name")
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Полное имя сотрудника")
    public static GraphQLQuery<GEmployee, String> getDisplayName() {
        return new GraphQLQuery<GEmployee, String>() {

            private CoreConfigGetter coreConfigGetter;

            @Override
            public void prepare(ResourceProvider resources) {
                coreConfigGetter = new CoreConfigGetter(resources);
            }

            @Override
            public String execute(GEmployee source, ContextTransactionRequest context) throws PlatformException {
                DisplayNameFormat displayNameFormat =
                        coreConfigGetter.get(CoreConfigDescription.DISPLAY_NAME_FORMAT, context.getTransaction());
                return source.getSource().getDisplayName(displayNameFormat);
            }
        };
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Роли доступа сотрудника")
    public static GraphQLQuery<GEmployee, GAccessRoleCollection> getAccessRoles(
            @GraphQLName(PAGING)
            @GraphQLDescription("Параметры пейджинга") final GPaging paging
    ) {
        GraphQLQuery<GEmployee, GAccessRoleCollection> query = new GraphQLQuery<GEmployee, GAccessRoleCollection>() {

            private ReadableResource<EmployeeAccessRoleReadable> employeeAccessRoleReadableResource;
            private ReadableResource<AccessRoleReadable> accessRoleReadableResource;

            @Override
            public void prepare(ResourceProvider resources) {
                employeeAccessRoleReadableResource = resources.getReadableResource(EmployeeAccessRoleReadable.class);
                accessRoleReadableResource = resources.getReadableResource(AccessRoleReadable.class);
            }

            @Override
            public GAccessRoleCollection execute(GEmployee source, ContextTransactionRequest context)
                    throws PlatformException {
                QueryTransaction transaction = context.getTransaction();
                Sorter<AccessRoleReadable> sorter = SorterUtil.getSorter(AccessRoleReadable::getName, paging);
                HashFilter filter = new HashFilter(EmployeeAccessRoleReadable.FIELD_EMPLOYEE_ID, source.getId());
                try (IteratorEntity<EmployeeAccessRoleReadable> ie =
                             employeeAccessRoleReadableResource.findAll(filter, transaction)) {
                    while (ie.hasNext()) {
                        sorter.add(accessRoleReadableResource.get(ie.next().getAccessRoleId(), transaction));
                    }
                }
                return new GAccessRoleCollection(sorter);
            }
        };
        return new GEmployeeAccessQuery<>(
                new GAccessQuery<>(query, CorePrivilege.EMPLOYEE_ACCESS, AccessOperation.READ)
        );
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Привилегии доступа сотрудника")
    public static GraphQLQuery<GEmployee, ArrayList<GOutPrivilege>> getPrivileges() {
        GraphQLQuery<GEmployee, ArrayList<GOutPrivilege>> query = new GraphQLQuery<GEmployee, ArrayList<GOutPrivilege>>() {

            private EmployeePrivilegesGetter employeePrivilegesGetter;

            @Override
            public void prepare(ResourceProvider resources) {
                employeePrivilegesGetter = new EmployeePrivilegesGetter(resources);
            }

            @Override
            public ArrayList<GOutPrivilege> execute(GEmployee source, ContextTransactionRequest context) throws PlatformException {
                Map<String, AccessOperationCollection> privileges =
                        employeePrivilegesGetter.getPrivileges(source.getId(), context);
                ArrayList<GOutPrivilege> gOutPrivileges = new ArrayList<>();
                for (Map.Entry<String, AccessOperationCollection> pair : privileges.entrySet()) {
                    gOutPrivileges.add(new GOutPrivilege(
                            pair.getKey(),
                            new ArrayList<>(Arrays.asList(pair.getValue().getOperations()))
                    ));
                }
                return gOutPrivileges;
            }
        };
        return new GPrivateSettingAccessQuery<>(query, true,
                CorePrivilege.EMPLOYEE_ACCESS, AccessOperation.READ);
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Список доступных отделов и сотрудников")
    public static GraphQLQuery<GEmployee, ArrayList<GDepartmentEmployeeElement>> getDepartmentsEmployees() {
        GraphQLQuery<GEmployee, ArrayList<GDepartmentEmployeeElement>> query =
                new GraphQLQuery<GEmployee, ArrayList<GDepartmentEmployeeElement>>() {

                    private ReadableResource<DepartmentReadable> departmentReadableResource;
                    private ReadableResource<EmployeeReadable> employeeReadableResource;
                    private ManagerEmployeeAccessEnumerator accessEnumerator;
                    private CoreConfigGetter coreConfigGetter;

                    @Override
                    public void prepare(ResourceProvider resources) {
                        departmentReadableResource = resources.getReadableResource(DepartmentReadable.class);
                        employeeReadableResource = resources.getReadableResource(EmployeeReadable.class);
                        accessEnumerator = new ManagerEmployeeAccessEnumerator(resources);
                        coreConfigGetter = new CoreConfigGetter(resources);
                    }

                    @Override
                    public ArrayList<GDepartmentEmployeeElement> execute(GEmployee source, ContextTransactionRequest context)
                            throws PlatformException {
                        DisplayNameFormat displayNameFormat =
                                coreConfigGetter.get(CoreConfigDescription.DISPLAY_NAME_FORMAT, context.getTransaction());
                        GEmployeeComparator employeeComparator = new GEmployeeComparator(displayNameFormat);
                        Sorter<GDepartmentEmployeeElement> sorter = new Sorter<>(
                                (o1, o2) -> {
                                    if (o1 instanceof GDepartment && o2 instanceof GEmployee) {
                                        return -1;
                                    } else if (o1 instanceof GEmployee && o2 instanceof GDepartment) {
                                        return 1;
                                    } else if (o1 instanceof GDepartment && o2 instanceof GDepartment) {
                                        return ComparatorUtility.compare(
                                                ((GDepartment) o1).getSource().getName(),
                                                ((GDepartment) o2).getSource().getName()
                                        );
                                    } else if (o1 instanceof GEmployee && o2 instanceof GEmployee) {
                                        return employeeComparator.compare((GEmployee) o1, (GEmployee) o2);
                                    }
                                    return 0;
                                },
                                null
                        );
                        accessEnumerator.forEachNode(source.getId(), context.getTransaction(), departmentId -> {
                            sorter.add(new GDepartment(departmentReadableResource.get(departmentId, context.getTransaction())));
                            return true;
                        });
                        accessEnumerator.forEachItem(source.getId(), context.getTransaction(), employeeId -> {
                            sorter.add(new GEmployee(employeeReadableResource.get(employeeId, context.getTransaction())));
                            return true;
                        });
                        return sorter.getData();
                    }
                };
        return new GEmployeeAccessQuery<>(
                new GAccessQuery<>(query, CorePrivilege.EMPLOYEE_ACCESS, AccessOperation.READ)
        );
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Флаг доступа ко всем сотрудникам")
    public static GraphQLQuery<GEmployee, Boolean> getAllEmployeeAccess() {
        GraphQLQuery<GEmployee, Boolean> query = new GraphQLQuery<GEmployee, Boolean>() {

            private ManagerEmployeeAccessEnumerator accessEnumerator;

            @Override
            public void prepare(ResourceProvider resources) {
                accessEnumerator = new ManagerEmployeeAccessEnumerator(resources);
            }

            @Override
            public Boolean execute(GEmployee source, ContextTransactionRequest context) throws PlatformException {
                return accessEnumerator.isAll(source.getId(), context.getTransaction());
            }
        };
        return new GPrivateSettingAccessQuery<>(query, true,
                CorePrivilege.EMPLOYEE_ACCESS, AccessOperation.READ);
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Телефонные номера")
    public static GraphQLQuery<GEmployee, ArrayList<String>> getPhoneNumbers() {
        GraphQLQuery<GEmployee, ArrayList<String>> query = new GraphQLQuery<GEmployee, ArrayList<String>>() {

            private ReadableResource<EmployeePhoneReadable> employeePhoneReadableResource;

            @Override
            public void prepare(ResourceProvider resources) {
                employeePhoneReadableResource = resources.getReadableResource(EmployeePhoneReadable.class);
            }

            @Override
            public ArrayList<String> execute(GEmployee source, ContextTransactionRequest context) throws PlatformException {
                ArrayList<String> phoneNumbers = new ArrayList<>();
                HashFilter filter = new HashFilter(EmployeePhoneReadable.FIELD_EMPLOYEE_ID, source.getId());
                employeePhoneReadableResource.forEach(filter, employeePhone ->
                        phoneNumbers.add(employeePhone.getPhoneNumber()), context.getTransaction());
                return phoneNumbers;
            }
        };
        return new GPrivateSettingAccessQuery<>(query, true,
                CorePrivilege.EMPLOYEES, AccessOperation.READ);
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Дополнительные поля со значениями")
    public static GraphQLQuery<GEmployee, ArrayList<GFieldValueInterface>> getAdditionalFieldValueList() {
        return new GPrivateSettingAccessQuery<>(new AdditionalFieldValueListQuery(), true,
                CorePrivilege.EMPLOYEES, AccessOperation.READ);
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Список ключей синхронизируемых системных полей")
    public static GraphQLQuery<GEmployee, ArrayList<String>> getSynchronizedSystemFieldKeys() {
        GraphQLQuery<GEmployee, ArrayList<String>> query = new GraphQLQuery<GEmployee, ArrayList<String>>() {

            private RCFieldsGetter rcFieldsGetter;
            private RCIntegrationsExecutor rcIntegrations;

            @Override
            public void prepare(ResourceProvider resources) {
                rcFieldsGetter = resources.getQueryRemoteController(CoreSubsystem.class, RCFieldsGetter.class);
                rcIntegrations = new RCIntegrationsExecutor(resources);
            }

            @Override
            public ArrayList<String> execute(GEmployee source, ContextTransactionRequest context) throws PlatformException {
                String objectType = EmployeeReadable.class.getName();
                ArrayList<SystemFieldDescription> fields =
                        rcFieldsGetter.getSystemFields(objectType, context);
                ArrayList<String> synchronizedSystemFieldKeys = new ArrayList<>(fields.size());
                for (SystemFieldDescription field : fields) {
                    String fieldKey = field.getKey();
                    if (rcIntegrations.isSynchronized(source.getId(), objectType, fieldKey, context)) {
                        synchronizedSystemFieldKeys.add(fieldKey);
                    }
                }
                return synchronizedSystemFieldKeys;
            }
        };
        return new GAccessQuery<>(query, CorePrivilege.EMPLOYEES, AccessOperation.READ);
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Является администратором?")
    public static GraphQLQuery<GEmployee, Boolean> isAdmin() {
        GraphQLQuery<GEmployee, Boolean> query = new GraphQLQuery<GEmployee, Boolean>() {

            private ReadableResource<AccessRoleReadable> accessRoleReadableResource;
            private ReadableResource<EmployeeAccessRoleReadable> employeeAccessRoleReadableResource;

            @Override
            public void prepare(ResourceProvider resources) {
                accessRoleReadableResource = resources.getReadableResource(AccessRoleReadable.class);
                employeeAccessRoleReadableResource = resources.getReadableResource(EmployeeAccessRoleReadable.class);
            }

            @Override
            public Boolean execute(GEmployee source, ContextTransactionRequest context) throws PlatformException {
                QueryTransaction transaction = context.getTransaction();
                HashFilter filter = new HashFilter(EmployeeAccessRoleReadable.FIELD_EMPLOYEE_ID, source.getId());
                try (IteratorEntity<EmployeeAccessRoleReadable> ie =
                             employeeAccessRoleReadableResource.findAll(filter, transaction)) {
                    while (ie.hasNext()) {
                        AccessRoleReadable accessRole =
                                accessRoleReadableResource.get(ie.next().getAccessRoleId(), transaction);
                        if (accessRole.isAdmin()) {
                            return true;
                        }
                    }
                }
                return false;
            }
        };
        return new GEmployeeAccessQuery<>(new GAccessQuery<>(query, CorePrivilege.EMPLOYEES, AccessOperation.READ));
    }

    public static class GEmployeeAccessQuery<T extends Serializable> extends GraphQLQuery<GEmployee, T> {

        private final GraphQLQuery<GEmployee, T> query;
        private ManagerEmployeeAccessGetter managerEmployeeAccessGetter;

        public GEmployeeAccessQuery(GraphQLQuery<GEmployee, T> query) {
            this.query = query;
        }

        public GraphQLQuery<GEmployee, T> getInnerQuery() {
            return query;
        }

        @Override
        public void prepare(ResourceProvider resources) {
            query.prepare(resources);
            managerEmployeeAccessGetter = new ManagerEmployeeAccessGetter(resources);
        }

        @Override
        public T execute(GEmployee source, ContextTransactionRequest context) throws PlatformException {
            if (!managerEmployeeAccessGetter.getAccess(context).checkEmployee(source.getId())) {
                throw GeneralExceptionBuilder.buildAccessDeniedException();
            }
            return query.execute(source, context);
        }
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Аутентификации сотрудника")
    public static GraphQLQuery<GEmployee, GAuthenticationCollection> getAuthentications(
            @GraphQLDescription("Параметры пейджинга")
            @GraphQLName(PAGING) final GPaging paging
    ) {
        return new GEmployeeAccessQuery<>(
                new GAccessQuery<>(new AuthenticationsQuery(paging), GAccessQuery.Operator.AND)
                        .with(CorePrivilege.EMPLOYEE_ACCESS, AccessOperation.READ)
                        .with(CorePrivilege.AUTHENTICATION, AccessOperation.READ)
        );
    }


    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Флаг оповещения системы")
    public static GraphQLQuery<GEmployee, Boolean> isSendSystemEvents() {
        GraphQLQuery<GEmployee, Boolean> query = new GraphQLQuery<>() {

            private EmployeePrivilegesGetter employeePrivilegesGetter;

            @Override
            public void prepare(ResourceProvider resources) {
                employeePrivilegesGetter = new EmployeePrivilegesGetter(resources);
            }

            @Override
            public Boolean execute(GEmployee source, ContextTransactionRequest context) throws PlatformException {
                final boolean isContainsAccess = employeePrivilegesGetter.checkPrivilegeAccessOperations(
                        source.getId(),
                        CorePrivilege.EMPLOYEE_ACCESS.getUniqueKey(),
                        Arrays.asList(AccessOperation.WRITE, AccessOperation.EXECUTE),
                        context
                );
                return isContainsAccess ? source.getSource().isSendSystemEvents() : null;
            }
        };
        return new GEmployeeAccessQuery<>(new GAccessQuery<>(query, CorePrivilege.EMPLOYEES, AccessOperation.READ));
    }

    public static class GPrivateSettingAccessQuery<T extends Serializable> extends GraphQLQuery<GEmployee, T> {

        private final GraphQLQuery<GEmployee, T> query;
        private final boolean checkEmployeeAccess;
        private final PrivilegeEnum privilege;
        private final AccessOperationCollection operations;
        private ManagerEmployeeAccessGetter managerEmployeeAccessGetter;


        public GPrivateSettingAccessQuery(
                GraphQLQuery<GEmployee, T> query,
                boolean checkEmployeeAccess,
                PrivilegeEnum privilege,
                AccessOperation... operations
        ) {
            this.query = query;
            this.checkEmployeeAccess = checkEmployeeAccess;
            this.privilege = privilege;
            this.operations = new AccessOperationCollection(operations);
        }

        public GPrivateSettingAccessQuery(
                Function<GEmployee, T> function,
                boolean checkEmployeeAccess,
                PrivilegeEnum privilege,
                AccessOperation... operations) {
            this(new GraphQLQuery<GEmployee, T>() {
                @Override
                public void prepare(ResourceProvider resources) {

                }

                @Override
                public T execute(GEmployee source, ContextTransactionRequest context) {
                    return function.apply(source);
                }
            }, checkEmployeeAccess, privilege, operations);
        }

        @Override
        public void prepare(ResourceProvider resources) {
            query.prepare(resources);
            managerEmployeeAccessGetter = this.checkEmployeeAccess ? new ManagerEmployeeAccessGetter(resources) : null;
        }

        @Override
        public T execute(GEmployee source, ContextTransactionRequest context) throws PlatformException {
            UnauthorizedContext authContext = context.getSource().getAuthContext();
            if (!(authContext instanceof AuthorizedContext)) {
                throw GeneralExceptionBuilder.buildAccessDeniedException();
            }
            AuthorizedContext authorizedContext = (AuthorizedContext) authContext;
            if (authContext instanceof EmployeeAuthContext &&
                    ((EmployeeAuthContext) authContext).getEmployeeId() == source.getId()) {
                return query.execute(source, context);
            }
            if (!authorizedContext.getOperations(privilege.getUniqueKey()).contains(operations)) {
                throw GeneralExceptionBuilder.buildAccessDeniedException();
            }
            if (checkEmployeeAccess && !managerEmployeeAccessGetter.getAccess(context).checkEmployee(source.getId())) {
                throw GeneralExceptionBuilder.buildAccessDeniedException();
            }
            return query.execute(source, context);
        }
    }
}
