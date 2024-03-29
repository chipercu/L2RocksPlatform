package com.fuzzy.subsystem.core.graphql.mutation;

import com.infomaximum.cluster.core.remote.struct.RemoteObject;
import com.infomaximum.cluster.graphql.anotation.GraphQLDescription;
import com.infomaximum.cluster.graphql.anotation.GraphQLField;
import com.infomaximum.cluster.graphql.anotation.GraphQLName;
import com.infomaximum.cluster.graphql.anotation.GraphQLTypeOutObject;
import com.infomaximum.cluster.graphql.struct.GOptional;
import com.infomaximum.database.domainobject.filter.HashFilter;
import com.infomaximum.platform.component.frontend.context.ContextTransactionRequest;
import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.QueryTransaction;
import com.infomaximum.platform.querypool.ReadableResource;
import com.infomaximum.platform.querypool.ResourceProvider;
import com.infomaximum.platform.sdk.graphql.annotation.GraphQLAuthControl;
import com.infomaximum.platform.sdk.graphql.customfield.graphqlquery.GraphQLQuery;
import com.fuzzy.subsystem.core.CoreSubsystem;
import com.fuzzy.subsystem.core.access.CorePrivilege;
import com.fuzzy.subsystem.core.domainobject.accessrole.AccessRoleReadable;
import com.fuzzy.subsystem.core.domainobject.employeeaccessrole.EmployeeAccessRoleReadable;
import com.fuzzy.subsystem.core.exception.CoreExceptionBuilder;
import com.fuzzy.subsystem.core.graphql.query.accessrole.GAccessRole;
import com.fuzzy.subsystem.core.graphql.query.privilege.GInputPrivilege;
import com.fuzzy.subsystem.core.privilege.PrivilegeGetter;
import com.fuzzy.subsystem.core.remote.accessrole.AccessRoleBuilder;
import com.fuzzy.subsystem.core.remote.accessrole.RControllerAccessRole;
import com.fuzzy.subsystem.core.remote.accessroleprivileges.PrivilegeValue;
import com.fuzzy.subsystem.core.remote.accessroleprivileges.RControllerAccessRolePrivileges;
import com.fuzzy.subsystem.core.subscription.employee.GEmployeeUpdateEvent;
import com.fuzzy.subsystem.frontend.authcontext.AuthorizedContext;
import com.fuzzy.subsystems.access.AccessOperation;
import com.fuzzy.subsystems.access.AccessOperationCollection;
import com.fuzzy.subsystems.exception.GeneralExceptionBuilder;
import com.fuzzy.subsystems.function.Consumer;
import com.fuzzy.subsystems.graphql.out.GRemovalData;
import com.fuzzy.subsystems.graphql.query.GAccessQuery;
import com.fuzzy.subsystems.remote.RemovalData;
import com.fuzzy.subsystems.utils.PrimaryKeyValidator;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@GraphQLTypeOutObject("mutation_access_role")
public class GMutationAccessRole {

    private static final String ID = "id";
    private static final String IDS = "ids";
    private static final String NAME = "name";
    private static final String PRIVILEGES = "privileges";

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Создание роли доступа")
    public static GraphQLQuery<RemoteObject, GAccessRole> create(
            @NonNull @GraphQLName(NAME)
            @GraphQLDescription("Название")
            final String name,
            @GraphQLName(PRIVILEGES)
            @GraphQLDescription("Привилегии")
            final GOptional<ArrayList<GInputPrivilege>> privileges
    ) {
        GraphQLQuery<RemoteObject, GAccessRole> query = new GraphQLQuery<RemoteObject, GAccessRole>() {

            private RControllerAccessRole rControllerAccessRole;
            private Set<RControllerAccessRolePrivileges> controllerAccessRolePrivilegesList;
            private PrivilegeGetter privilegeGetter;

            @Override
            public void prepare(ResourceProvider resources) {
                rControllerAccessRole =
                        resources.getQueryRemoteController(CoreSubsystem.class, RControllerAccessRole.class);
                controllerAccessRolePrivilegesList =
                        resources.getQueryRemoteControllers(RControllerAccessRolePrivileges.class);
                privilegeGetter = new PrivilegeGetter(resources);
            }

            @Override
            public GAccessRole execute(RemoteObject source, ContextTransactionRequest context)
                    throws PlatformException {
                AccessRoleReadable accessRole =
                        rControllerAccessRole.create(new AccessRoleBuilder().withName(name), context);
                setPrivileges(
                        controllerAccessRolePrivilegesList,
                        privilegeGetter,
                        accessRole.getId(),
                        privileges,
                        accessRoleId -> {},
                        context
                );
                return new GAccessRole(accessRole);
            }
        };
        return new GAccessQuery<>(query, CorePrivilege.ACCESS_ROLE, AccessOperation.CREATE);
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Редактирование роли доступа")
    public static GraphQLQuery<RemoteObject, GAccessRole> update(
            CoreSubsystem component,
            @NonNull @GraphQLName(ID)
            @GraphQLDescription("Идентификатор обновляемой роли доступа")
            final long accessRoleId,
            @GraphQLName(NAME)
            @GraphQLDescription("Новое значение названия")
            final GOptional<String> name,
            @GraphQLName(PRIVILEGES)
            @GraphQLDescription("Новое значение привилегий")
            final GOptional<ArrayList<GInputPrivilege>> privileges
    ) {
        GraphQLQuery<RemoteObject, GAccessRole> query = new GraphQLQuery<RemoteObject, GAccessRole>() {

            private ReadableResource<AccessRoleReadable> accessRoleReadableResource;
            private ReadableResource<EmployeeAccessRoleReadable> employeeAccessRoleReadableResource;
            private RControllerAccessRole rControllerAccessRole;
            private Set<RControllerAccessRolePrivileges> controllerAccessRolePrivilegesList;
            private PrivilegeGetter privilegeGetter;

            @Override
            public void prepare(ResourceProvider resources) {
                accessRoleReadableResource = resources.getReadableResource(AccessRoleReadable.class);
                employeeAccessRoleReadableResource = resources.getReadableResource(EmployeeAccessRoleReadable.class);
                rControllerAccessRole =
                        resources.getQueryRemoteController(CoreSubsystem.class, RControllerAccessRole.class);
                controllerAccessRolePrivilegesList =
                        resources.getQueryRemoteControllers(RControllerAccessRolePrivileges.class);
                privilegeGetter = new PrivilegeGetter(resources);
            }

            @Override
            public GAccessRole execute(RemoteObject source, ContextTransactionRequest context)
                    throws PlatformException {
                QueryTransaction transaction = context.getTransaction();
                AccessRoleReadable accessRole = new PrimaryKeyValidator(false)
                        .validateAndGet(accessRoleId, accessRoleReadableResource, context.getTransaction());
                if (accessRole.isAdmin()) {
                    throw GeneralExceptionBuilder.buildReadOnlyObjectException();
                }
                AccessRoleBuilder builder = new AccessRoleBuilder();
                if (name != null && name.isPresent()) {
                    builder.withName(name.get());
                }
                accessRole = rControllerAccessRole.update(accessRoleId, builder, context);
                setPrivileges(
                        controllerAccessRolePrivilegesList,
                        privilegeGetter,
                        accessRole.getId(),
                        privileges,
                        accessRoleId -> {
                            HashFilter filter = new HashFilter(EmployeeAccessRoleReadable.FIELD_ACCESS_ROLE_ID, accessRoleId);
                            employeeAccessRoleReadableResource.forEach(filter, employeeAccessRole ->
                                    GEmployeeUpdateEvent.send(component, employeeAccessRole.getEmployeeId(), transaction), transaction);
                        },
                        context
                );
                return new GAccessRole(accessRole);
            }
        };
        return new GAccessQuery<>(query, CorePrivilege.ACCESS_ROLE, AccessOperation.WRITE);
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Удаление ролей доступа")
    public static GraphQLQuery<RemoteObject, GRemovalData> remove(
            @GraphQLDescription("Идентификаторы удаляемых ролей доступа")
            @NonNull @GraphQLName(IDS) final HashSet<Long> accessRoleIds
    ) {
        GraphQLQuery<RemoteObject, GRemovalData> query = new GraphQLQuery<RemoteObject, GRemovalData>() {

            private ReadableResource<AccessRoleReadable> accessRoleReadableResource;
            private RControllerAccessRole rControllerAccessRole;

            @Override
            public void prepare(ResourceProvider resources) {
                accessRoleReadableResource = resources.getReadableResource(AccessRoleReadable.class);
                rControllerAccessRole =
                        resources.getQueryRemoteController(CoreSubsystem.class, RControllerAccessRole.class);
            }

            @Override
            public GRemovalData execute(RemoteObject source, ContextTransactionRequest context) throws PlatformException {
                HashSet<Long> validAccessRoles = new HashSet<>();
                HashSet<Long> adminAccessRoles = new HashSet<>();
                for (Long accessRoleId : accessRoleIds) {
                    if (accessRoleId != null) {
                        AccessRoleReadable accessRole =
                                accessRoleReadableResource.get(accessRoleId, context.getTransaction());
                        if (accessRole != null && accessRole.isAdmin()) {
                            adminAccessRoles.add(accessRoleId);
                            continue;
                        }
                        validAccessRoles.add(accessRoleId);
                    }
                }
                RemovalData removalData = rControllerAccessRole.removeWithCauses(validAccessRoles, context);
                removalData.addNonRemoved(CoreExceptionBuilder.ADMINISTRATOR_ACCESS_ROLE_CODE, adminAccessRoles);
                return new GRemovalData(removalData);
            }
        };
        return new GAccessQuery<>(query, CorePrivilege.ACCESS_ROLE, AccessOperation.DELETE);
    }

    private static void setPrivileges(
            Set<RControllerAccessRolePrivileges> controllerAccessRolePrivilegesList,
            PrivilegeGetter privilegeGetter,
            long accessRoleId,
            GOptional<ArrayList<GInputPrivilege>> privileges,
            Consumer<Long> eventSender,
            ContextTransactionRequest context
    ) throws PlatformException {
        if (privileges != null && privileges.isPresent()) {
            if (privileges.get() == null) {
                throw GeneralExceptionBuilder.buildInvalidValueException(PRIVILEGES, null);
            }
            PrivilegeValue[] privilegeValues = GInputPrivilege.convert(privileges.get());
            for (PrivilegeValue privilegeValue : privilegeValues) {
                AccessOperationCollection availableOperation =
                        privilegeGetter.getAvailableOperations(privilegeValue.getKey(), context);
                if (!availableOperation.contains(privilegeValue.getOperations())) {
                    throw GeneralExceptionBuilder.buildInvalidValueException(PRIVILEGES, privilegeValue.getKey());
                }
            }
            for (RControllerAccessRolePrivileges rControllerAccessRolePrivileges :
                    controllerAccessRolePrivilegesList) {
                rControllerAccessRolePrivileges.setPrivilegesToAccessRole(accessRoleId, privilegeValues, context);
            }
            eventSender.accept(accessRoleId);
        }
    }
}
