package com.fuzzy.subsystem.core.graphql.query.tag;

import com.infomaximum.cluster.graphql.anotation.GraphQLDescription;
import com.infomaximum.cluster.graphql.anotation.GraphQLField;
import com.infomaximum.cluster.graphql.anotation.GraphQLTypeOutObject;
import com.infomaximum.platform.sdk.graphql.annotation.GraphQLAuthControl;
import com.fuzzy.subsystem.core.domainobject.tag.TagReadable;
import com.fuzzy.subsystem.frontend.authcontext.AuthorizedContext;
import com.fuzzy.subsystems.graphql.GDomainObject;

@GraphQLTypeOutObject("tag")
public class GTag extends GDomainObject<TagReadable> {

	public GTag(TagReadable source) {
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
	public String getName() {
		return getSource().getName();
	}

	@GraphQLField
	@GraphQLAuthControl({ AuthorizedContext.class })
	@GraphQLDescription("Цвет")
	public String getColour() {
		return getSource().getColour();
	}

	@GraphQLField
	@GraphQLAuthControl({ AuthorizedContext.class })
	@GraphQLDescription("Только чтение")
	public boolean isReadOnly() {
		return getSource().isReadOnly();
	}
}
