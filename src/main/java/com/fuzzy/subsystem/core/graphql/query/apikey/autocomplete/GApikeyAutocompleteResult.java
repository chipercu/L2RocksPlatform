package com.fuzzy.subsystem.core.graphql.query.apikey.autocomplete;

import com.infomaximum.cluster.graphql.anotation.GraphQLDescription;
import com.infomaximum.cluster.graphql.anotation.GraphQLField;
import com.infomaximum.cluster.graphql.anotation.GraphQLTypeOutObject;
import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.sdk.graphql.annotation.GraphQLAuthControl;
import com.fuzzy.subsystem.core.domainobject.apikey.ApiKeyReadable;
import com.fuzzy.subsystem.core.graphql.query.apikey.GApiKey;
import com.fuzzy.subsystem.frontend.authcontext.AuthorizedContext;
import com.fuzzy.subsystems.autocomplete.LightAutocompleteResult;
import com.fuzzy.subsystems.struct.RemoteRDomainObjectCollectionResult;
import com.fuzzy.subsystems.utils.DomainObjectListGetter;

import java.util.ArrayList;

@GraphQLTypeOutObject("apikey_autocomplete_result")
public class GApikeyAutocompleteResult extends RemoteRDomainObjectCollectionResult<ApiKeyReadable, GApiKey> {

	private GApikeyAutocompleteResult() {
	}

	GApikeyAutocompleteResult(DomainObjectListGetter<ApiKeyReadable>.Result source) throws PlatformException {
		super(GApiKey::new, source);
	}

	public GApikeyAutocompleteResult(LightAutocompleteResult<ApiKeyReadable> source) throws PlatformException {
		super(GApiKey::new, source);
	}

	@GraphQLField
	@GraphQLAuthControl({ AuthorizedContext.class })
	@GraphQLDescription("Элементы")
	public ArrayList<GApiKey> getItems() {
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