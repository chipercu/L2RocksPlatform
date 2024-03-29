package com.fuzzy.subsystem.core.graphql.query.filter;

import com.infomaximum.cluster.core.remote.struct.RemoteObject;
import com.infomaximum.cluster.graphql.anotation.GraphQLDescription;
import com.infomaximum.cluster.graphql.anotation.GraphQLField;
import com.infomaximum.cluster.graphql.anotation.GraphQLName;
import com.infomaximum.cluster.graphql.anotation.GraphQLTypeOutObject;
import com.infomaximum.database.domainobject.filter.HashFilter;
import com.infomaximum.platform.component.frontend.context.ContextTransactionRequest;
import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.QueryTransaction;
import com.infomaximum.platform.querypool.ReadableResource;
import com.infomaximum.platform.querypool.ResourceProvider;
import com.infomaximum.platform.sdk.graphql.annotation.GraphQLAuthControl;
import com.infomaximum.platform.sdk.graphql.customfield.graphqlquery.GraphQLQuery;
import com.fuzzy.subsystem.core.authcontext.employee.EmployeeAuthContext;
import com.fuzzy.subsystem.core.domainobject.filter.FilterReadable;
import com.fuzzy.subsystem.frontend.authcontext.AuthorizedContext;
import com.fuzzy.subsystems.graphql.query.GPrimaryKeyQuery;
import com.fuzzy.subsystems.utils.ComparatorUtility;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.Objects;

@GraphQLTypeOutObject("filter_query")
public class GQueryFilter {

	@GraphQLField
	@GraphQLAuthControl({ EmployeeAuthContext.class })
	@GraphQLDescription("Фильтр по идентификатору")
	public static GraphQLQuery<RemoteObject, GFilter> getFilter(
			@NonNull @GraphQLName("id")
			@GraphQLDescription("Идентификатор фильтра") final long id
	) {
		return new GPrimaryKeyQuery<RemoteObject, FilterReadable, GFilter>(
				FilterReadable.class, GFilter::new) {

			@Override
			public GFilter execute(RemoteObject source, ContextTransactionRequest context) throws PlatformException {
				GFilter filter = super.execute(source, context);
				if (filter != null) {
					long employeeId = ((EmployeeAuthContext) context.getSource().getAuthContext()).getEmployeeId();
					if (!Objects.equals(filter.getSource().getEmployeeId(), employeeId)) {
						filter = null;
					}
				}
				return filter;
			}

			@Override
			protected Long getIdentificator(RemoteObject source, QueryTransaction transaction) {
				return id;
			}
		};
	}

	@GraphQLField
	@GraphQLAuthControl({ AuthorizedContext.class })
	@GraphQLDescription("Фильтры по группе")
	public static GraphQLQuery<RemoteObject, ArrayList<GFilter>> getFiltersByGroup(
			@NonNull @GraphQLName("group")
			@GraphQLDescription("Группа")
			final String group
	) {
		return new GraphQLQuery<RemoteObject, ArrayList<GFilter>>() {

			private ReadableResource<FilterReadable> filterReadableResource;

			@Override
			public void prepare(ResourceProvider resources) {
				filterReadableResource = resources.getReadableResource(FilterReadable.class);
			}

			@Override
			public ArrayList<GFilter> execute(RemoteObject source, ContextTransactionRequest context) throws PlatformException {
				ArrayList<GFilter> filters = new ArrayList<>();
				long employeeId = ((EmployeeAuthContext)context.getSource().getAuthContext()).getEmployeeId();
				HashFilter hashFilter = new HashFilter(FilterReadable.FIELD_GROUP, group)
						.appendField(FilterReadable.FIELD_EMPLOYEE_ID, employeeId);
				filterReadableResource.forEach(hashFilter,
						filter -> filters.add(new GFilter(filter)), context.getTransaction());
				filters.sort((o1, o2) -> ComparatorUtility.compare(o1.getId(), o1.getName(), o2.getId(), o2.getName()));
				return filters;
			}
		};
	}
}
