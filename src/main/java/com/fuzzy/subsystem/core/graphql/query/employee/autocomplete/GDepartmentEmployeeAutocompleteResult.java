package com.fuzzy.subsystem.core.graphql.query.employee.autocomplete;

import com.fuzzy.main.cluster.graphql.anotation.GraphQLDescription;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLField;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLTypeOutObject;
import com.fuzzy.main.platform.sdk.graphql.annotation.GraphQLAuthControl;
import com.fuzzy.subsystem.core.domainobject.department.DepartmentReadable;
import com.fuzzy.subsystem.core.domainobject.employee.EmployeeReadable;
import com.fuzzy.subsystem.core.graphql.query.department.GDepartment;
import com.fuzzy.subsystem.core.graphql.query.employee.GDepartmentEmployeeElement;
import com.fuzzy.subsystem.core.graphql.query.employee.GEmployee;
import com.fuzzy.subsystem.frontend.authcontext.AuthorizedContext;
import com.fuzzy.subsystems.autocomplete.AutocompleteElement;
import com.fuzzy.subsystems.autocomplete.AutocompleteResult;
import com.fuzzy.subsystems.struct.RemoteCollectionResult;

import java.util.ArrayList;

@GraphQLTypeOutObject("department_employee_autocomplete_result")
public class GDepartmentEmployeeAutocompleteResult extends RemoteCollectionResult<GDepartmentEmployeeElement> {

	private GDepartmentEmployeeAutocompleteResult() {
	}

	public GDepartmentEmployeeAutocompleteResult(AutocompleteResult<DepartmentReadable, EmployeeReadable> source) {
		ArrayList<GDepartmentEmployeeElement> items = new ArrayList<>();
		for (AutocompleteElement<DepartmentReadable, EmployeeReadable> sourceItem : source.getItems()) {
			if (sourceItem.getNode() != null) {
				items.add(new GDepartment(sourceItem.getNode()));
			} else if (sourceItem.getItem() != null) {
				items.add(new GEmployee(sourceItem.getItem()));
			}
		}
		setItems(items);
		setHasNext(source.hasNext());
		setMatchCount(source.getMatchCount());
	}

	@GraphQLField
	@GraphQLAuthControl({ AuthorizedContext.class })
	@GraphQLDescription("Элементы")
	public ArrayList<GDepartmentEmployeeElement> getItems() {
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
