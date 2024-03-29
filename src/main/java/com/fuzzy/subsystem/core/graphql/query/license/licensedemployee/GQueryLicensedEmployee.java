package com.fuzzy.subsystem.core.graphql.query.license.licensedemployee;

import com.fuzzy.main.cluster.core.remote.struct.RemoteObject;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLDescription;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLField;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLTypeOutObject;
import com.fuzzy.main.platform.component.frontend.context.ContextTransactionRequest;
import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.ReadableResource;
import com.fuzzy.main.platform.querypool.ResourceProvider;
import com.fuzzy.main.platform.sdk.graphql.annotation.GraphQLAuthControl;
import com.fuzzy.main.platform.sdk.graphql.customfield.graphqlquery.GraphQLQuery;
import com.fuzzy.subsystem.core.CoreSubsystem;
import com.fuzzy.subsystem.core.domainobject.employee.EmployeeReadable;
import com.fuzzy.subsystem.core.graphql.query.employee.GEmployee;
import com.fuzzy.subsystem.core.license.enums.BusinessRoleLimit;
import com.fuzzy.subsystem.core.remote.licensedemployee.RCLicensedEmployeeGetter;
import com.fuzzy.subsystem.frontend.authcontext.AuthorizedContext;
import com.fuzzy.subsystems.utils.PrimaryKeyValidator;

import java.util.ArrayList;
import java.util.HashSet;

@GraphQLTypeOutObject("query_licensed_employee")
public class GQueryLicensedEmployee {


    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Бизнесс-администраторы>")
    public static GraphQLQuery<RemoteObject, ArrayList<GEmployee>> getBusinessAdmins() {
        return new GraphQLQuery<>() {
            private RCLicensedEmployeeGetter rcLicensedEmployeeGetter;
            private PrimaryKeyValidator primaryKeyValidator;
            private ReadableResource<EmployeeReadable> employeeReadableResource;

            @Override
            public void prepare(ResourceProvider resources) {
                rcLicensedEmployeeGetter = resources.getQueryRemoteController(CoreSubsystem.class, RCLicensedEmployeeGetter.class);
                primaryKeyValidator = new PrimaryKeyValidator(false);
                employeeReadableResource = resources.getReadableResource(EmployeeReadable.class);
            }

            @Override
            public ArrayList<GEmployee> execute(RemoteObject source, ContextTransactionRequest context) throws PlatformException {
                ArrayList<GEmployee> list = new ArrayList<>();
                HashSet<Long> licensedEmployees = rcLicensedEmployeeGetter.getLicensedEmployees(BusinessRoleLimit.ADMIN, context);
                for (Long employeeId : licensedEmployees) {
                    list.add(new GEmployee(primaryKeyValidator.validateAndGet(employeeId, employeeReadableResource, context.getTransaction())));
                }
                return list;
            }
        };
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Аналитики")
    public static GraphQLQuery<RemoteObject, ArrayList<GEmployee>> getAnalysts() {
        return new GraphQLQuery<>() {
            private RCLicensedEmployeeGetter licensedEmployeeGetter;
            private PrimaryKeyValidator primaryKeyValidator;
            private ReadableResource<EmployeeReadable> employeeReadableResource;

            @Override
            public void prepare(ResourceProvider resources) {
                licensedEmployeeGetter = resources.getQueryRemoteController(CoreSubsystem.class, RCLicensedEmployeeGetter.class);
                primaryKeyValidator = new PrimaryKeyValidator(false);
                employeeReadableResource = resources.getReadableResource(EmployeeReadable.class);
            }

            @Override
            public ArrayList<GEmployee> execute(RemoteObject source, ContextTransactionRequest context) throws PlatformException {
                ArrayList<GEmployee> list = new ArrayList<>();
                HashSet<Long> licensedEmployees = licensedEmployeeGetter.getLicensedEmployees(BusinessRoleLimit.ANALYST, context);
                for (Long employeeId : licensedEmployees) {
                    list.add(new GEmployee(primaryKeyValidator.validateAndGet(employeeId, employeeReadableResource, context.getTransaction())));
                }
                return list;
            }
        };
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Бизнесс-пользователи")
    public static GraphQLQuery<RemoteObject, ArrayList<GEmployee>> getBusinessUsers() {
        return new GraphQLQuery<>() {
            private RCLicensedEmployeeGetter licensedEmployeeGetter;
            private PrimaryKeyValidator primaryKeyValidator;
            private ReadableResource<EmployeeReadable> employeeReadableResource;

            @Override
            public void prepare(ResourceProvider resources) {
                licensedEmployeeGetter = resources.getQueryRemoteController(CoreSubsystem.class, RCLicensedEmployeeGetter.class);
                primaryKeyValidator = new PrimaryKeyValidator(false);
                employeeReadableResource = resources.getReadableResource(EmployeeReadable.class);
            }

            @Override
            public ArrayList<GEmployee> execute(RemoteObject source, ContextTransactionRequest context) throws PlatformException {
                ArrayList<GEmployee> list = new ArrayList<>();
                HashSet<Long> licensedEmployees = licensedEmployeeGetter.getLicensedEmployees(BusinessRoleLimit.BUSINESS_USER, context);
                for (Long employeeId : licensedEmployees) {
                    list.add(new GEmployee(primaryKeyValidator.validateAndGet(employeeId, employeeReadableResource, context.getTransaction())));
                }
                return list;
            }
        };
    }
}
