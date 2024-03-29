package com.fuzzy.subsystem.core.graphql.query.department;

import com.fuzzy.main.cluster.core.remote.struct.RemoteObject;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLDescription;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLField;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLName;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLTypeOutObject;
import com.fuzzy.main.platform.component.frontend.authcontext.UnauthorizedContext;
import com.fuzzy.main.platform.component.frontend.context.ContextTransactionRequest;
import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.QueryTransaction;
import com.fuzzy.main.platform.querypool.ReadableResource;
import com.fuzzy.main.platform.querypool.ResourceProvider;
import com.fuzzy.main.platform.sdk.graphql.annotation.GraphQLAuthControl;
import com.fuzzy.main.platform.sdk.graphql.customfield.graphqlquery.GraphQLQuery;
import com.fuzzy.subsystem.core.access.CorePrivilege;
import com.fuzzy.subsystem.core.authcontext.employee.EmployeeAuthContext;
import com.fuzzy.subsystem.core.autocomplete.DepartmentAutocomplete;
import com.fuzzy.subsystem.core.domainobject.department.DepartmentReadable;
import com.fuzzy.subsystem.core.domainobject.employee.EmployeeReadable;
import com.fuzzy.subsystem.core.employeeaccess.ManagerEmployeeAccess;
import com.fuzzy.subsystem.core.employeeaccess.ManagerEmployeeAccessGetter;
import com.fuzzy.subsystem.core.graphql.query.department.tree.GDepartmentTreeResult;
import com.fuzzy.subsystem.core.graphql.query.employee.GQueryEmployee;
import com.fuzzy.subsystem.core.tree.department.DepartmentTreeBuilder;
import com.fuzzy.subsystem.core.tree.department.DepartmentTreeParam;
import com.fuzzy.subsystem.frontend.authcontext.AuthorizedContext;
import com.fuzzy.subsystems.access.AccessOperation;
import com.fuzzy.subsystems.graphql.input.GInputItems;
import com.fuzzy.subsystems.graphql.input.GPaging;
import com.fuzzy.subsystems.graphql.input.GTextFilter;
import com.fuzzy.subsystems.graphql.input.GTreePaging;
import com.fuzzy.subsystems.graphql.query.GAccessQuery;
import com.fuzzy.subsystems.graphql.query.GPrimaryKeyQuery;
import com.fuzzy.subsystems.tree.Tree;
import com.fuzzy.subsystems.utils.PrimaryKeyValidator;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@GraphQLTypeOutObject("department_query")
public class GQueryDepartment {

	private static final String ID = "id";
	private static final String TEXT_FILTER = "text_filter";
	private static final String EXCLUDED_DEPARTMENTS = "excluded_departments";
	private static final String PAGING = "paging";
	private static final String TARGET_DEPARTMENT_ID = "target_department_id";
	private static final String ID_FILTER = "id_filter";
	private static final String ALWAYS_COMING_DATA = "always_coming_data";
	private static final String TOP_NODES = "top_nodes";

	@GraphQLField
	@GraphQLAuthControl({ AuthorizedContext.class })
	@GraphQLDescription("Все отделы")
	public static GraphQLQuery<RemoteObject, ArrayList<GDepartment>> getDepartments() {

		GraphQLQuery<RemoteObject, ArrayList<GDepartment>> query = new GraphQLQuery<RemoteObject, ArrayList<GDepartment>>() {

			private ReadableResource<DepartmentReadable> departmentReadableResource;
			private DepartmentVisibleGetter departmentVisibleGetter;

			@Override
			public void prepare(ResourceProvider resources) {
				departmentReadableResource = resources.getReadableResource(DepartmentReadable.class);
				departmentVisibleGetter = new DepartmentVisibleGetter(resources);
			}

			@Override
			public ArrayList<GDepartment> execute(RemoteObject source, ContextTransactionRequest context) throws PlatformException {
				QueryTransaction transaction = context.getTransaction();
				Set<Long> visibleDepartments = departmentVisibleGetter.getVisibleDepartments(context);
				ArrayList<GDepartment> gDepartments = new ArrayList<>();
				departmentReadableResource.forEach(department -> {
					if (visibleDepartments == null || visibleDepartments.contains(department.getId())) {
						gDepartments.add(new GDepartment(department));
					}
				}, transaction);
				return gDepartments;
			}
		};
		return new GAccessQuery<>(query, CorePrivilege.EMPLOYEES, AccessOperation.READ);
	}

	@GraphQLField
	@GraphQLAuthControl({ AuthorizedContext.class })
	@GraphQLDescription("Получение отдела по идентификатору")
	public static GraphQLQuery<RemoteObject, GDepartment> getDepartment(
			@NonNull @GraphQLName(ID)
			@GraphQLDescription("Идентификатор отдела") final long id
	) {
		GraphQLQuery<RemoteObject, GDepartment> gPrimaryKeyQuery = new GPrimaryKeyQuery<RemoteObject, DepartmentReadable, GDepartment>(
				DepartmentReadable.class, GDepartment::new) {
			@Override
			protected Long getIdentificator(RemoteObject source, QueryTransaction transaction) {
				return id;
			}
		};
		GraphQLQuery<RemoteObject, GDepartment> query = new GraphQLQuery<RemoteObject, GDepartment>() {

			private DepartmentVisibleGetter departmentVisibleGetter;

			@Override
			public void prepare(ResourceProvider resources) {
				departmentVisibleGetter = new DepartmentVisibleGetter(resources);
				gPrimaryKeyQuery.prepare(resources);
			}

			@Override
			public GDepartment execute(RemoteObject source, ContextTransactionRequest context) throws PlatformException {
				Set<Long> visibleDepartments = departmentVisibleGetter.getVisibleDepartments(context);
				if (visibleDepartments == null || visibleDepartments.contains(id)) {
					return gPrimaryKeyQuery.execute(source, context);
				}
				return null;
			}
		};
		return new GAccessQuery<>(query, CorePrivilege.EMPLOYEES, AccessOperation.READ);
	}

	@GraphQLField(value = "department_autocomplete")
	@GraphQLAuthControl({ AuthorizedContext.class })
	@GraphQLDescription("Autocomplete по отделам")
	public static GraphQLQuery<RemoteObject, GDepartmentCollectionResult> getDepartmentAutocomplete(
			@GraphQLName(TEXT_FILTER)
			@GraphQLDescription("Текстовый фильтр") final GTextFilter textFilter,
			@GraphQLName(EXCLUDED_DEPARTMENTS)
			@GraphQLDescription("Идентификаторы исключаемых из выдачи отделов") final HashSet<Long> excludedDepartments,
			@GraphQLName(PAGING)
			@GraphQLDescription("Параметры пейджинга") final GPaging paging,
			@GraphQLName(TARGET_DEPARTMENT_ID)
			@GraphQLDescription("Идентификатор целевого отдела") final Long targetDepartmentId
	) {
		GraphQLQuery<RemoteObject, GDepartmentCollectionResult> query =
				new GraphQLQuery<RemoteObject, GDepartmentCollectionResult>() {

					private ReadableResource<DepartmentReadable> departmentReadableResource;
					private DepartmentAutocomplete departmentAutocomplete;

					@Override
					public void prepare(ResourceProvider resources) {
						departmentReadableResource = resources.getReadableResource(DepartmentReadable.class);
						departmentAutocomplete = new DepartmentAutocomplete(resources);
					}

					@Override
					public GDepartmentCollectionResult execute(RemoteObject source, ContextTransactionRequest context)
							throws PlatformException {
						QueryTransaction transaction = context.getTransaction();
						UnauthorizedContext authContext = context.getSource().getAuthContext();
						if (authContext instanceof EmployeeAuthContext) {
							long employeeId = ((EmployeeAuthContext)authContext).getEmployeeId();
							departmentAutocomplete.setAuthEmployeeId(employeeId);
						}
						HashSet<Long> validExcludedDepartments = new PrimaryKeyValidator(true).validate(
								excludedDepartments, departmentReadableResource, transaction);
						Long validTargetDepartmentId = null;
						if (targetDepartmentId != null &&
								departmentReadableResource.get(targetDepartmentId, transaction) != null) {
							validTargetDepartmentId = targetDepartmentId;
						}
						return new GDepartmentCollectionResult(
								departmentAutocomplete.execute(
										textFilter, validExcludedDepartments, paging, validTargetDepartmentId, context
								));
					}
				};
		return new GAccessQuery<>(query, CorePrivilege.EMPLOYEES, AccessOperation.READ);
	}

	@GraphQLField
	@GraphQLAuthControl({ AuthorizedContext.class })
	@GraphQLDescription("Дерево отделов с учетом доступа к отделам")
	public static GraphQLQuery<RemoteObject, GDepartmentTreeResult> getDepartmentTree(
			@GraphQLName(TEXT_FILTER)
			@GraphQLDescription("Текстовый фильтр")
			final GTextFilter textFilter,
			@GraphQLName(ID_FILTER)
			@GraphQLDescription("Фильтр по отделам")
			final GInputItems idFilter,
			@GraphQLName(ALWAYS_COMING_DATA)
			@GraphQLDescription("Отделы, обязательно присутствующие в дереве")
			final GInputItems alwaysComingData,
			@GraphQLName(PAGING)
			@GraphQLDescription("Параметры пейджинга")
			final GTreePaging paging,
			@GraphQLDescription("Элементы, отображающиеся в начале списка")
			@GraphQLName(TOP_NODES) final HashSet<Long> topNodes
	) {
		GraphQLQuery<RemoteObject, GDepartmentTreeResult> query = new GraphQLQuery <RemoteObject, GDepartmentTreeResult>() {

			private ReadableResource<DepartmentReadable> departmentReadableResource;
			private ManagerEmployeeAccessGetter managerEmployeeAccessGetter;
			private DepartmentTreeBuilder treeBuilder;

			@Override
			public void prepare(ResourceProvider resources) {
				departmentReadableResource = resources.getReadableResource(DepartmentReadable.class);
				managerEmployeeAccessGetter = new ManagerEmployeeAccessGetter(resources);
				treeBuilder = new DepartmentTreeBuilder(resources);
			}

			@Override
			public GDepartmentTreeResult execute(RemoteObject source, ContextTransactionRequest context)
					throws PlatformException {
				QueryTransaction transaction = context.getTransaction();
				UnauthorizedContext authContext = context.getSource().getAuthContext();
				DepartmentTreeParam treeParam = new DepartmentTreeParam();
				ManagerEmployeeAccess access = null;
				if (authContext instanceof EmployeeAuthContext) {
					long employeeId = ((EmployeeAuthContext)authContext).getEmployeeId();
					treeParam.authEmployeeId = employeeId;
					access = managerEmployeeAccessGetter.getAccess(employeeId, transaction);
				}
				treeParam.idFilter =
						validateDepartmentFilter(idFilter, departmentReadableResource, access, transaction);
				treeParam.alwaysComingData =
						validateDepartmentFilter(alwaysComingData, departmentReadableResource, access, transaction);
				treeParam.paging = GQueryEmployee.validatePaging(paging, departmentReadableResource, transaction);
				if (textFilter != null && textFilter.isSpecified()) {
					treeParam.textFilter = textFilter.getText();
				}
				treeParam.topNodes = topNodes;
				Tree<DepartmentReadable, EmployeeReadable> tree = treeBuilder.build(treeParam, transaction);
				return new GDepartmentTreeResult(tree);
			}
		};
		return new GAccessQuery<>(query, CorePrivilege.EMPLOYEES, AccessOperation.READ);
	}

	private static GInputItems validateDepartmentFilter(
			GInputItems inFilter,
			ReadableResource<DepartmentReadable> departmentReadableResource,
			ManagerEmployeeAccess access,
			QueryTransaction transaction
	) throws PlatformException {
		GInputItems outFilter = null;
		if (inFilter != null) {
			HashSet<Long> items = null;
			if (inFilter.getItems() != null) {
				HashSet<Long> outDepartments = new HashSet <>();
				for (Long departmentId : inFilter.getItems()) {
					if (departmentId != null && departmentReadableResource.get(departmentId, transaction) != null &&
							(access == null || access.checkDepartment(departmentId))) {
						outDepartments.add(departmentId);
					}
				}
				items = outDepartments;
			}
			outFilter = new GInputItems(items);
		}
		return outFilter;
	}
}