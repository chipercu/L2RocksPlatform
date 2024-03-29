package com.fuzzy.subsystem.core.graphql.mutation;

import com.infomaximum.cluster.core.remote.struct.RemoteObject;
import com.infomaximum.cluster.graphql.anotation.GraphQLDescription;
import com.infomaximum.cluster.graphql.anotation.GraphQLField;
import com.infomaximum.cluster.graphql.anotation.GraphQLName;
import com.infomaximum.cluster.graphql.anotation.GraphQLTypeOutObject;
import com.infomaximum.cluster.graphql.struct.GOptional;
import com.infomaximum.platform.component.frontend.authcontext.UnauthorizedContext;
import com.infomaximum.platform.component.frontend.context.ContextTransactionRequest;
import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.ReadableResource;
import com.infomaximum.platform.querypool.ResourceProvider;
import com.infomaximum.platform.sdk.graphql.annotation.GraphQLAuthControl;
import com.infomaximum.platform.sdk.graphql.customfield.graphqlquery.GraphQLQuery;
import com.fuzzy.subsystem.core.CoreSubsystem;
import com.fuzzy.subsystem.core.access.CorePrivilege;
import com.fuzzy.subsystem.core.authcontext.employee.EmployeeAuthContext;
import com.fuzzy.subsystem.core.domainobject.department.DepartmentReadable;
import com.fuzzy.subsystem.core.employeeaccess.ManagerEmployeeAccess;
import com.fuzzy.subsystem.core.employeeaccess.ManagerEmployeeAccessGetter;
import com.fuzzy.subsystem.core.graphql.query.department.GDepartment;
import com.fuzzy.subsystem.core.remote.department.DepartmentBuilder;
import com.fuzzy.subsystem.core.remote.department.RControllerDepartmentControl;
import com.fuzzy.subsystem.frontend.authcontext.AuthorizedContext;
import com.fuzzy.subsystems.access.AccessOperation;
import com.fuzzy.subsystems.exception.GeneralExceptionBuilder;
import com.fuzzy.subsystems.graphql.out.GRemovalData;
import com.fuzzy.subsystems.graphql.query.GAccessQuery;
import com.fuzzy.subsystems.remote.RemovalData;
import com.fuzzy.subsystems.utils.PrimaryKeyValidator;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashSet;
import java.util.Set;

@GraphQLTypeOutObject("mutation_department")
public class GMutationDepartment {

	private static final String ID = "id";
	private static final String IDS = "ids";
	private static final String NAME = "name";
	private static final String PARENT_DEPARTMENT_ID = "parent_department_id";

	@GraphQLField
	@GraphQLAuthControl({ AuthorizedContext.class })
	@GraphQLDescription("Создание отдела")
	public static GraphQLQuery<RemoteObject, GDepartment> create(
			@GraphQLName(NAME)
			@GraphQLDescription("Название")
			final GOptional<String> name,
			@GraphQLName(PARENT_DEPARTMENT_ID)
			@GraphQLDescription("Идентификатор родительского отдела")
			final GOptional<Long> parentId
	) {
		GraphQLQuery<RemoteObject, GDepartment> query = new GraphQLQuery<RemoteObject, GDepartment>() {

			private RControllerDepartmentControl rControllerDepartmentControl;

			@Override
			public void prepare(ResourceProvider resources) {
				rControllerDepartmentControl = resources.getQueryRemoteController(
						CoreSubsystem.class, RControllerDepartmentControl.class);
			}

			@Override
			public GDepartment execute(RemoteObject source, ContextTransactionRequest context) throws PlatformException {
				DepartmentBuilder departmentBuilder = new DepartmentBuilder();
				if (name.isPresent()) {
					departmentBuilder.withName(name.get());
				}
				if (parentId.isPresent()) {
					departmentBuilder.withParentId(parentId.get());
				}
				DepartmentReadable department = rControllerDepartmentControl.create(departmentBuilder, context);
				return department != null ? new GDepartment(department) : null;
			}
		};
		return new GAccessQuery<>(query, CorePrivilege.EMPLOYEES, AccessOperation.CREATE);
	}

	@GraphQLField
	@GraphQLAuthControl({ AuthorizedContext.class })
	@GraphQLDescription("Обновление отдела")
	public static GraphQLQuery<RemoteObject, GDepartment> update(
			@NonNull @GraphQLName(ID)
			@GraphQLDescription("Идентификатор обновляемого отдела")
			final long departmentId,
			@GraphQLName(NAME)
			@GraphQLDescription("Новое значение имени")
			final GOptional<String> name,
			@GraphQLName(PARENT_DEPARTMENT_ID)
			@GraphQLDescription("Новое значение идентификатора родительского отдела")
			final GOptional<Long> parentId
	) {
		GraphQLQuery<RemoteObject, GDepartment> query = new GraphQLQuery<RemoteObject, GDepartment>() {

			private RControllerDepartmentControl rControllerDepartmentControl;
			private ManagerEmployeeAccessGetter managerEmployeeAccessGetter;

			@Override
			public void prepare(ResourceProvider resources) {
				rControllerDepartmentControl =
						resources.getQueryRemoteController(CoreSubsystem.class, RControllerDepartmentControl.class);
				managerEmployeeAccessGetter = new ManagerEmployeeAccessGetter(resources);
			}

			@Override
			public GDepartment execute(RemoteObject source, ContextTransactionRequest context) throws PlatformException {
				UnauthorizedContext authContext = context.getSource().getAuthContext();
				if (authContext instanceof EmployeeAuthContext) {
					long employeeId = ((EmployeeAuthContext)authContext).getEmployeeId();
					ManagerEmployeeAccess access = managerEmployeeAccessGetter.getAccess(employeeId, context.getTransaction());
					if (!access.checkDepartment(departmentId)) {
						throw GeneralExceptionBuilder.buildAccessDeniedException();
					}
				}
				DepartmentBuilder departmentBuilder = new DepartmentBuilder();
				if (name.isPresent()) {
					departmentBuilder.withName(name.get());
				}
				if (parentId.isPresent()) {
					departmentBuilder.withParentId(parentId.get());
				}
				DepartmentReadable department =
						rControllerDepartmentControl.update(departmentId, departmentBuilder, context);
				return department != null ? new GDepartment(department) : null;
			}
		};
		return new GAccessQuery<>(query, CorePrivilege.EMPLOYEES, AccessOperation.WRITE);
	}

	@GraphQLField
	@GraphQLAuthControl({ AuthorizedContext.class })
	@GraphQLDescription("Пакетное обновление отделов")
	public static GraphQLQuery<RemoteObject, Boolean> multiUpdate(
			@NonNull @GraphQLName(IDS)
			@GraphQLDescription("Идентификаторы обновляемых отделов")
			final HashSet<Long> departmentIds,
			@GraphQLName(PARENT_DEPARTMENT_ID)
			@GraphQLDescription("Новое значение идентификатора родительского отдела")
			final GOptional<Long> parentId
	) {
		GraphQLQuery<RemoteObject, Boolean> query = new GraphQLQuery<RemoteObject, Boolean>() {

			private ReadableResource<DepartmentReadable> departmentReadableResource;
			private RControllerDepartmentControl rControllerDepartmentControl;
			private ManagerEmployeeAccessGetter managerEmployeeAccessGetter;

			@Override
			public void prepare(ResourceProvider resources) {
				departmentReadableResource = resources.getReadableResource(DepartmentReadable.class);
				rControllerDepartmentControl =
						resources.getQueryRemoteController(CoreSubsystem.class, RControllerDepartmentControl.class);
				managerEmployeeAccessGetter = new ManagerEmployeeAccessGetter(resources);
			}

			@Override
			public Boolean execute(RemoteObject source, ContextTransactionRequest context) throws PlatformException {
				Set<Long> departments = new PrimaryKeyValidator(true).validate(
						departmentIds, departmentReadableResource, context.getTransaction());
				DepartmentBuilder departmentBuilder = new DepartmentBuilder();
				if (parentId.isPresent()) {
					departmentBuilder.withParentId(parentId.get());
				}
				ManagerEmployeeAccess access = null;
				UnauthorizedContext authContext = context.getSource().getAuthContext();
				if (authContext instanceof EmployeeAuthContext) {
					long employeeId = ((EmployeeAuthContext)authContext).getEmployeeId();
					access = managerEmployeeAccessGetter.getAccess(employeeId, context.getTransaction());
				}
				for (Long departmentId : departments) {
					if (access == null || access.checkDepartment(departmentId)) {
						rControllerDepartmentControl.update(departmentId, departmentBuilder, context);
					}
				}
				return true;
			}
		};
		return new GAccessQuery<>(query, CorePrivilege.EMPLOYEES, AccessOperation.WRITE);
	}

	@GraphQLField
	@GraphQLAuthControl({ AuthorizedContext.class })
	@GraphQLDescription("Удаление отделов")
	public static GraphQLQuery<RemoteObject, GRemovalData> remove(
			@GraphQLDescription("Идентификаторы удаляемых отделов")
			@NonNull @GraphQLName(IDS) final HashSet<Long> departmentIds
	) {
		GraphQLQuery<RemoteObject, GRemovalData> query = new GraphQLQuery<RemoteObject, GRemovalData>() {

			private RControllerDepartmentControl rControllerDepartmentControl;
			private ManagerEmployeeAccessGetter managerEmployeeAccessGetter;

			@Override
			public void prepare(ResourceProvider resources) {
				rControllerDepartmentControl =
						resources.getQueryRemoteController(CoreSubsystem.class, RControllerDepartmentControl.class);
				managerEmployeeAccessGetter = new ManagerEmployeeAccessGetter(resources);
			}

			@Override
			public GRemovalData execute(RemoteObject source, ContextTransactionRequest context) throws PlatformException {
				ManagerEmployeeAccess access = null;
				UnauthorizedContext authContext = context.getSource().getAuthContext();
				if (authContext instanceof EmployeeAuthContext) {
					long employeeId = ((EmployeeAuthContext)authContext).getEmployeeId();
					access = managerEmployeeAccessGetter.getAccess(employeeId, context.getTransaction());
				}
				HashSet<Long> departmentsForRemoving = new HashSet<>();
				HashSet<Long> accessDenied = new HashSet<>();
				for (Long departmentId : departmentIds) {
					if (departmentId == null) {
						continue;
					}
					if (access != null && !access.checkDepartment(departmentId)) {
						accessDenied.add(departmentId);
						continue;
					}
					departmentsForRemoving.add(departmentId);
				}
				RemovalData removalData = rControllerDepartmentControl.removeWithCauses(departmentsForRemoving, context);
				removalData.addNonRemoved(GeneralExceptionBuilder.ACCESS_DENIED_CODE, accessDenied);
				return new GRemovalData(removalData);
			}
		};
		return new GAccessQuery<>(query, CorePrivilege.EMPLOYEES, AccessOperation.DELETE);
	}
}
