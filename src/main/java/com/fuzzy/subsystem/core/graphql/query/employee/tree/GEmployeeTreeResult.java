package com.fuzzy.subsystem.core.graphql.query.employee.tree;

import com.infomaximum.cluster.graphql.anotation.GraphQLDescription;
import com.infomaximum.cluster.graphql.anotation.GraphQLField;
import com.infomaximum.cluster.graphql.anotation.GraphQLTypeOutObject;
import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.sdk.graphql.annotation.GraphQLAuthControl;
import com.fuzzy.subsystem.core.domainobject.department.DepartmentReadable;
import com.fuzzy.subsystem.core.domainobject.employee.EmployeeReadable;
import com.fuzzy.subsystem.core.graphql.query.department.GDepartment;
import com.fuzzy.subsystem.core.graphql.query.employee.GEmployee;
import com.fuzzy.subsystem.frontend.authcontext.AuthorizedContext;
import com.fuzzy.subsystems.tree.RemoteTreeResult;
import com.fuzzy.subsystems.tree.Tree;

import java.util.ArrayList;

@GraphQLTypeOutObject("employee_tree_result")
public class GEmployeeTreeResult extends RemoteTreeResult<GEmployeeTreeElement> {

	private GEmployeeTreeResult() {
	}

	public GEmployeeTreeResult(Tree<DepartmentReadable, EmployeeReadable> tree) throws PlatformException {
		super(tree);
	}

	@GraphQLField
	@GraphQLAuthControl({ AuthorizedContext.class })
	@GraphQLDescription("Элементы")
	public ArrayList<GEmployeeTreeElement> getElements() {
		return super.getElements();
	}

	@GraphQLField
	@GraphQLAuthControl({ AuthorizedContext.class })
	@GraphQLDescription("Количество элементов, соответствующих текстовому фильтру")
	public int getMatchCount() {
		return super.getMatchCount();
	}

	@Override
	public GEmployeeTreeElement createElement(Object source) throws PlatformException {
		GEmployeeTreeElement element = new GEmployeeTreeElement();
		if (source != null) {
			if (source instanceof DepartmentReadable) {
				element.setElement(new GDepartment((DepartmentReadable) source));
			} else if (source instanceof EmployeeReadable) {
				element.setElement(new GEmployee((EmployeeReadable) source));
			} else {
				throw new IllegalArgumentException("type of " + source.getClass().getName() + " isn't supported");
			}
		}
		return element;
	}
}
