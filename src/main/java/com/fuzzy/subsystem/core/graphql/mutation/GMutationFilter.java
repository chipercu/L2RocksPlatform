package com.fuzzy.subsystem.core.graphql.mutation;

import com.fuzzy.main.cluster.core.remote.struct.RemoteObject;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLDescription;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLField;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLName;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLTypeOutObject;
import com.fuzzy.main.cluster.graphql.struct.GOptional;
import com.fuzzy.main.platform.component.frontend.context.ContextTransactionRequest;
import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.EditableResource;
import com.fuzzy.main.platform.querypool.QueryTransaction;
import com.fuzzy.main.platform.querypool.RemovableResource;
import com.fuzzy.main.platform.querypool.ResourceProvider;
import com.fuzzy.main.platform.sdk.graphql.annotation.GraphQLAuthControl;
import com.fuzzy.main.platform.sdk.graphql.customfield.graphqlquery.GraphQLQuery;
import com.fuzzy.subsystem.core.authcontext.employee.EmployeeAuthContext;
import com.fuzzy.subsystem.core.domainobject.filter.FilterEditable;
import com.fuzzy.subsystem.core.graphql.query.filter.GFilter;
import com.fuzzy.subsystems.exception.GeneralExceptionBuilder;
import com.fuzzy.subsystems.utils.PrimaryKeyValidator;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashSet;
import java.util.Objects;

@GraphQLTypeOutObject("mutation_filter")
public class GMutationFilter {

	private static final String ID = "id";
	private static final String IDS = "ids";
	private static final String GROUP = "group";
	private static final String NAME = "name";
	private static final String VALUE = "value";

	@GraphQLField
	@GraphQLAuthControl({ EmployeeAuthContext.class })
	@GraphQLDescription("Добавление фильтра")
	public static GraphQLQuery<RemoteObject, GFilter> create(
			@NonNull @GraphQLName(GROUP)
			@GraphQLDescription("Группа")
			final String group,
			@NonNull @GraphQLName(NAME)
			@GraphQLDescription("Название")
			final String name,
			@NonNull @GraphQLName(VALUE)
			@GraphQLDescription("Значение")
			final String value
	) {
		return new GraphQLQuery<RemoteObject, GFilter>() {

			private EditableResource<FilterEditable> filterEditableResource;

			@Override
			public void prepare(ResourceProvider resources) {
				filterEditableResource = resources.getEditableResource(FilterEditable.class);
			}

			@Override
			public GFilter execute(RemoteObject source, ContextTransactionRequest context) throws PlatformException {
				QueryTransaction transaction = context.getTransaction();
				EmployeeAuthContext authContext = (EmployeeAuthContext)context.getSource().getAuthContext();
				FilterEditable filter = filterEditableResource.create(transaction);
				filter.setEmployeeId(authContext.getEmployeeId());
				filter.setGroup(group);
				filter.setName(name);
				filter.setValue(value);
				filterEditableResource.save(filter, transaction);
				return new GFilter(filter);
			}
		};
	}

	@GraphQLField
	@GraphQLAuthControl({ EmployeeAuthContext.class })
	@GraphQLDescription("Добавление фильтра")
	public static GraphQLQuery<RemoteObject, GFilter> update(
			@NonNull @GraphQLName(ID)
			@GraphQLDescription("Идентификатор обновляемого фильтра")
			final long filterId,
			@GraphQLName("group")
			@GraphQLDescription("Группа")
			final GOptional<String> group,
			@GraphQLName("name")
			@GraphQLDescription("Название")
			final GOptional<String> name,
			@GraphQLName("value")
			@GraphQLDescription("Значение")
			final GOptional<String> value
	) {
		return new GraphQLQuery<RemoteObject, GFilter>() {

			private EditableResource<FilterEditable> filterEditableResource;

			@Override
			public void prepare(ResourceProvider resources) {
				filterEditableResource = resources.getEditableResource(FilterEditable.class);
			}

			@Override
			public GFilter execute(RemoteObject source, ContextTransactionRequest context) throws PlatformException {
				QueryTransaction transaction = context.getTransaction();
 				FilterEditable filter = new PrimaryKeyValidator(false)
						.validateAndGet(filterId, filterEditableResource, transaction);
				EmployeeAuthContext authContext = (EmployeeAuthContext)context.getSource().getAuthContext();
				if (!Objects.equals(authContext.getEmployeeId(), filter.getEmployeeId())) {
					throw GeneralExceptionBuilder.buildAccessDeniedException();
				}
				if (group != null && group.isPresent() && group.get() != null) {
					filter.setGroup(group.get());
				}
				if (name != null && name.isPresent() && name.get() != null) {
					filter.setName(name.get());
				}
				if (value != null && value.isPresent() && value.get() != null) {
					filter.setValue(value.get());
				}
				filterEditableResource.save(filter, transaction);
				return new GFilter(filter);
			}
		};
	}

	@GraphQLField
	@GraphQLAuthControl({ EmployeeAuthContext.class })
	@GraphQLDescription("Удаление фильтров")
	public static GraphQLQuery<RemoteObject, HashSet<Long>> remove(
			@NonNull @GraphQLName(IDS)
			@GraphQLDescription("Идентификаторы удаляемых фильтров")
			final HashSet<Long> filterIds
	) {
		return new GraphQLQuery<RemoteObject, HashSet<Long>>() {

			private RemovableResource<FilterEditable> filterRemovableResource;

			@Override
			public void prepare(ResourceProvider resources) {
				filterRemovableResource = resources.getRemovableResource(FilterEditable.class);
			}

			@Override
			public HashSet<Long> execute(RemoteObject source, ContextTransactionRequest context) throws PlatformException {
				QueryTransaction transaction = context.getTransaction();
				long employeeId = ((EmployeeAuthContext)context.getSource().getAuthContext()).getEmployeeId();
				HashSet<Long> removed = new HashSet<>();
				for (Long filterId : filterIds) {
					if (filterId != null) {
						FilterEditable filter = filterRemovableResource.get(filterId, transaction);
						if (filter != null && Objects.equals(filter.getEmployeeId(), employeeId)) {
							filterRemovableResource.remove(filter, transaction);
							removed.add(filterId);
						}
					}
				}
				return removed;
			}
		};
	}
}