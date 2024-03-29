package com.fuzzy.subsystem.core.graphql.query.department.tree;

import com.infomaximum.cluster.graphql.anotation.GraphQLDescription;
import com.infomaximum.cluster.graphql.anotation.GraphQLField;
import com.infomaximum.cluster.graphql.anotation.GraphQLTypeOutObject;
import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.sdk.graphql.annotation.GraphQLAuthControl;
import com.fuzzy.subsystem.core.domainobject.department.DepartmentReadable;
import com.fuzzy.subsystem.core.domainobject.employee.EmployeeReadable;
import com.fuzzy.subsystem.core.graphql.query.department.GDepartment;
import com.fuzzy.subsystem.frontend.authcontext.AuthorizedContext;
import com.fuzzy.subsystems.tree.RemoteTreeResult;
import com.fuzzy.subsystems.tree.Tree;

import java.util.ArrayList;

@GraphQLTypeOutObject("department_tree_result")
public class GDepartmentTreeResult extends RemoteTreeResult<GDepartmentTreeElement> {

    private GDepartmentTreeResult() {
    }

    public GDepartmentTreeResult(Tree<DepartmentReadable, EmployeeReadable> tree) throws PlatformException {
        super(tree);
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Элементы")
    public ArrayList<GDepartmentTreeElement> getElements() {
        return super.getElements();
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Количество элементов, соответствующих текстовому фильтру")
    public int getMatchCount() {
        return super.getMatchCount();
    }

    @Override
    public GDepartmentTreeElement createElement(Object source) throws PlatformException {
        GDepartmentTreeElement element = new GDepartmentTreeElement();
        if (source != null) {
            element.setElement(new GDepartment((DepartmentReadable) source));
        }
        return element;
    }
}
