package com.fuzzy.subsystem.core.graphql.query.tag.autocomplete;

import com.infomaximum.cluster.graphql.anotation.GraphQLDescription;
import com.infomaximum.cluster.graphql.anotation.GraphQLField;
import com.infomaximum.cluster.graphql.anotation.GraphQLTypeOutObject;
import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.sdk.graphql.annotation.GraphQLAuthControl;
import com.fuzzy.subsystem.core.domainobject.tag.TagReadable;
import com.fuzzy.subsystem.core.graphql.query.tag.GTag;
import com.fuzzy.subsystem.frontend.authcontext.AuthorizedContext;
import com.fuzzy.subsystems.autocomplete.LightAutocompleteResult;
import com.fuzzy.subsystems.struct.RemoteRDomainObjectCollectionResult;
import com.fuzzy.subsystems.utils.DomainObjectListGetter;

import java.util.ArrayList;

@GraphQLTypeOutObject("tag_autocomplete_result")
public class GTagAutocompleteResult extends RemoteRDomainObjectCollectionResult<TagReadable, GTag> {

	private GTagAutocompleteResult() {
	}

	GTagAutocompleteResult(DomainObjectListGetter<TagReadable>.Result source) throws PlatformException {
		super(GTag::new, source);
	}

	public GTagAutocompleteResult(LightAutocompleteResult<TagReadable> source) throws PlatformException {
		super(GTag::new, source);
	}

	@GraphQLField
	@GraphQLAuthControl({ AuthorizedContext.class })
	@GraphQLDescription("Элементы")
	public ArrayList<GTag> getItems() {
		return super.getItems();
	}

	@GraphQLField
	@GraphQLAuthControl({ AuthorizedContext.class })
	@GraphQLDescription("Есть ли элементы за границей лимита")
	public boolean hasNext() {
		return super.hasNext();
	}

	@GraphQLField
	@GraphQLAuthControl({ AuthorizedContext.class })
	@GraphQLDescription("Количество элементов, соответствующих текстовому фильтру")
	public int getMatchCount() {
		return super.getMatchCount();
	}
}