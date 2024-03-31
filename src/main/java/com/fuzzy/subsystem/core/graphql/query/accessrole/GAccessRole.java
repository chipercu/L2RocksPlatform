package com.fuzzy.subsystem.core.graphql.query.accessrole;

import com.fuzzy.cluster.graphql.anotation.GraphQLDescription;
import com.fuzzy.cluster.graphql.anotation.GraphQLField;
import com.fuzzy.cluster.graphql.anotation.GraphQLName;
import com.fuzzy.cluster.graphql.anotation.GraphQLTypeOutObject;
import com.fuzzy.platform.component.frontend.context.ContextTransactionRequest;
import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.ResourceProvider;
import com.fuzzy.platform.sdk.graphql.annotation.GraphQLAuthControl;
import com.fuzzy.platform.sdk.graphql.customfield.graphqlquery.GraphQLQuery;
import com.fuzzy.subsystem.core.access.CorePrivilege;
import com.fuzzy.subsystem.core.accessroleprivileges.AccessRolePrivilegesGetter;
import com.fuzzy.subsystem.core.domainobject.accessrole.AccessRoleReadable;
import com.fuzzy.subsystem.core.graphql.query.accessrole.util.AccessRoleEmployeeGetter;
import com.fuzzy.subsystem.core.graphql.query.employee.GEmployeeCollection;
import com.fuzzy.subsystem.core.graphql.query.privilege.GOutPrivilege;
import com.fuzzy.subsystem.frontend.authcontext.AuthorizedContext;
import com.fuzzy.subsystems.access.AccessOperation;
import com.fuzzy.subsystems.access.AccessOperationCollection;
import com.fuzzy.subsystems.graphql.GDomainObject;
import com.fuzzy.subsystems.graphql.input.GPagingEx;
import com.fuzzy.subsystems.graphql.input.GStandardFilter;
import com.fuzzy.subsystems.graphql.query.GAccessQuery;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;

@GraphQLTypeOutObject("access_role")
public class GAccessRole extends GDomainObject<AccessRoleReadable> {

    private static final String EMPLOYEE_FILTER = "employee_filter";

    public GAccessRole(AccessRoleReadable source) {
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
    public static GraphQLQuery<GAccessRole, String> getName() throws PlatformException {
        return new GAccessQuery<GAccessRole, String>(
                gAccessRole -> gAccessRole.getSource().getName(),
                GAccessQuery.Operator.OR
        ).with(CorePrivilege.ACCESS_ROLE, AccessOperation.READ)
                .with(CorePrivilege.EMPLOYEE_ACCESS, AccessOperation.READ);
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Только чтение")
    public static GraphQLQuery<GAccessRole, Boolean> isReadOnly() throws PlatformException {
        return new GAccessQuery<>(gAccessRole -> gAccessRole.getSource().isAdmin(),
                CorePrivilege.ACCESS_ROLE, AccessOperation.READ);
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Привилегии")
    public static GraphQLQuery<GAccessRole, ArrayList<GOutPrivilege>> getPrivileges() {
        GraphQLQuery<GAccessRole, ArrayList<GOutPrivilege>> query =
                new GraphQLQuery<GAccessRole, ArrayList<GOutPrivilege>>() {

            private AccessRolePrivilegesGetter accessRolePrivilegesGetter;

            @Override
            public void prepare(ResourceProvider resources) {
                accessRolePrivilegesGetter = new AccessRolePrivilegesGetter(resources);
            }

            @Override
            public ArrayList<GOutPrivilege> execute(GAccessRole source, ContextTransactionRequest context)
                    throws PlatformException {
                ArrayList<GOutPrivilege> gPrivileges = new ArrayList<>();
                HashMap<String, AccessOperationCollection> privilegeValues =
                        accessRolePrivilegesGetter.getPrivileges(source.getId(), context);
                HashSet<String> privileges = accessRolePrivilegesGetter.getPrivilegeCollection(context);
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
        return new GAccessQuery<>(query, CorePrivilege.ACCESS_ROLE, AccessOperation.READ);
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Количество сотрудников")
    public static GraphQLQuery<GAccessRole, Integer> getEmployeeCount(
            @GraphQLName(EMPLOYEE_FILTER)
            @GraphQLDescription("Фильтр по сотрудникам")
            final GStandardFilter employeeFilter
    ) {
        GraphQLQuery<GAccessRole, Integer> query = new GraphQLQuery<GAccessRole, Integer>() {

            private AccessRoleEmployeeGetter accessRoleEmployeeGetter;

            @Override
            public void prepare(ResourceProvider resources) {
                accessRoleEmployeeGetter = new AccessRoleEmployeeGetter(resources);
            }

            @Override
            public Integer execute(GAccessRole source, ContextTransactionRequest context)
                    throws PlatformException {
                return accessRoleEmployeeGetter.getEmployeeCount(source.getId(), employeeFilter, context);
            }
        };
        return new GAccessQuery<>(query, CorePrivilege.ACCESS_ROLE, AccessOperation.READ);
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Сотрудники")
    public static GraphQLQuery<GAccessRole, GEmployeeCollection> getEmployees(
            @GraphQLName(EMPLOYEE_FILTER)
            @GraphQLDescription("Фильтр по сотрудникам")
            final GStandardFilter employeeFilter,
            @GraphQLName("paging")
            @GraphQLDescription("Параметры пейджинга")
            final GPagingEx paging
    ) {
        GraphQLQuery<GAccessRole, GEmployeeCollection> query = new GraphQLQuery<GAccessRole, GEmployeeCollection>() {

            private AccessRoleEmployeeGetter accessRoleEmployeeGetter;

            @Override
            public void prepare(ResourceProvider resources) {
                accessRoleEmployeeGetter = new AccessRoleEmployeeGetter(resources);
            }

            @Override
            public GEmployeeCollection execute(GAccessRole source, ContextTransactionRequest context) throws PlatformException {
                Integer limit = paging != null ? paging.getLimit(source.getId()) : null;
                return accessRoleEmployeeGetter.getEmployees(source.getId(), employeeFilter, limit, context);
            }
        };
        return new GAccessQuery<>(query, CorePrivilege.ACCESS_ROLE, AccessOperation.READ);
    }
}
