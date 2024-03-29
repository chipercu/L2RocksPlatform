package com.fuzzy.subsystem.core.graphql.query.filter;

import com.fuzzy.main.cluster.graphql.anotation.GraphQLDescription;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLField;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLTypeOutObject;
import com.fuzzy.main.platform.sdk.graphql.annotation.GraphQLAuthControl;
import com.fuzzy.subsystem.core.domainobject.filter.FilterReadable;
import com.fuzzy.subsystem.frontend.authcontext.AuthorizedContext;
import com.fuzzy.subsystems.graphql.GDomainObject;

@GraphQLTypeOutObject("filter")
public class GFilter extends GDomainObject<FilterReadable> {

	public GFilter(FilterReadable source) {
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
	@GraphQLDescription("Группа")
	public String getGroup() {
		return getSource().getGroup();
	}

	@GraphQLField
	@GraphQLAuthControl({ AuthorizedContext.class })
	@GraphQLDescription("Название")
	public String getName() {
		return getSource().getName();
	}

	@GraphQLField
	@GraphQLAuthControl({ AuthorizedContext.class })
	@GraphQLDescription("Значение")
	public String getValue() {
		return getSource().getValue();
	}
}
