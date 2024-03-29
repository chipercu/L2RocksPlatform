package com.fuzzy.subsystem.core.graphql.query.employee.tree;

import com.fuzzy.main.cluster.core.remote.struct.RemoteObject;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLDescription;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLField;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLTypeOutObject;
import com.fuzzy.main.platform.sdk.graphql.annotation.GraphQLAuthControl;
import com.fuzzy.subsystem.core.graphql.query.employee.GDepartmentEmployeeElement;
import com.fuzzy.subsystem.frontend.authcontext.AuthorizedContext;
import com.fuzzy.subsystems.tree.RemoteTreeElement;

import java.util.ArrayList;

@GraphQLTypeOutObject("employee_tree_element")
public class GEmployeeTreeElement extends RemoteTreeElement<GDepartmentEmployeeElement> implements RemoteObject {

	@GraphQLField
	@GraphQLAuthControl({ AuthorizedContext.class })
	@GraphQLDescription("Элементы")
	public GDepartmentEmployeeElement getElement() {
		return super.getElement();
	}

	@GraphQLField
	@GraphQLAuthControl({ AuthorizedContext.class })
	@GraphQLDescription("Есть ли элементы за границей лимита")
	public boolean hasNext() {
		return super.hasNext();
	}

	@GraphQLField
	@GraphQLAuthControl({ AuthorizedContext.class })
	@GraphQLDescription("Флаг выбора")
	public boolean isSelected() {
		return super.isSelected();
	}

	@GraphQLField
	@GraphQLAuthControl({ AuthorizedContext.class })
	@GraphQLDescription("Флаг видимости элемента")
	public boolean isHidden() {
		return super.isHidden();
	}

	@GraphQLField
	@GraphQLAuthControl({ AuthorizedContext.class })
	@GraphQLDescription("Цепочка родительских отделов")
	public ArrayList<Long> getParentDepartments() {
		return super.getParents();
	}
}
