package com.fuzzy.subsystem.core.graphql.query.department;

import com.fuzzy.cluster.graphql.anotation.GraphQLDescription;
import com.fuzzy.cluster.graphql.anotation.GraphQLField;
import com.fuzzy.cluster.graphql.anotation.GraphQLTypeOutObject;
import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.sdk.graphql.annotation.GraphQLAuthControl;
import com.fuzzy.subsystem.core.domainobject.department.DepartmentReadable;
import com.fuzzy.subsystem.frontend.authcontext.AuthorizedContext;
import com.fuzzy.subsystems.autocomplete.LightAutocompleteResult;
import com.fuzzy.subsystems.struct.RemoteRDomainObjectCollectionResult;

import java.util.ArrayList;

@GraphQLTypeOutObject("department_list_result")
public class GDepartmentCollectionResult
		extends RemoteRDomainObjectCollectionResult<DepartmentReadable, GDepartment> {

	private GDepartmentCollectionResult() {
	}

	public GDepartmentCollectionResult(LightAutocompleteResult<DepartmentReadable> source) throws PlatformException {
		super(GDepartment::new, source);
	}

	@GraphQLField
	@GraphQLAuthControl({ AuthorizedContext.class })
	@GraphQLDescription("Элементы")
	public ArrayList<GDepartment> getItems() {
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
