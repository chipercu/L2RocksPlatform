package com.fuzzy.subsystem.core.graphql.query.employee.autocomplete;

import com.fuzzy.main.cluster.graphql.anotation.GraphQLDescription;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLField;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLTypeOutObject;
import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.sdk.graphql.annotation.GraphQLAuthControl;
import com.fuzzy.subsystem.core.domainobject.employee.EmployeeReadable;
import com.fuzzy.subsystem.core.graphql.query.employee.GEmployee;
import com.fuzzy.subsystem.frontend.authcontext.AuthorizedContext;
import com.fuzzy.subsystems.autocomplete.LightAutocompleteResult;
import com.fuzzy.subsystems.struct.RemoteRDomainObjectCollectionResult;

import java.util.ArrayList;

@GraphQLTypeOutObject("employee_autocomplete_result")
public class GEmployeeAutocompleteResult extends
		RemoteRDomainObjectCollectionResult<EmployeeReadable, GEmployee> {

	private GEmployeeAutocompleteResult() {
	}

	public GEmployeeAutocompleteResult(LightAutocompleteResult<EmployeeReadable> source)
			throws PlatformException {
		super(GEmployee::new, source);
	}

	@GraphQLField
	@GraphQLAuthControl({ AuthorizedContext.class })
	@GraphQLDescription("Элементы")
	public ArrayList<GEmployee> getItems() {
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