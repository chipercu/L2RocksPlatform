package com.fuzzy.subsystem.core.graphql.query.employee;

import com.fuzzy.cluster.graphql.anotation.GraphQLDescription;
import com.fuzzy.cluster.graphql.anotation.GraphQLField;
import com.fuzzy.cluster.graphql.anotation.GraphQLTypeOutObject;
import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.sdk.graphql.annotation.GraphQLAuthControl;
import com.fuzzy.subsystem.core.domainobject.employee.EmployeeReadable;
import com.fuzzy.subsystem.frontend.authcontext.AuthorizedContext;
import com.fuzzy.subsystems.collection.RemoteCollection;
import com.fuzzy.subsystems.sorter.Sorter;

import java.util.ArrayList;

@GraphQLTypeOutObject("employee_collection")
public class GEmployeeCollection extends RemoteCollection<EmployeeReadable, GEmployee> {

    public GEmployeeCollection(Sorter<EmployeeReadable> source) throws PlatformException {
        super(source, GEmployee::new);
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Элементы")
    @Override
    public ArrayList<GEmployee> getItems() {
        return super.getItems();
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Есть ли элементы за границей лимита")
    @Override
    public boolean hasNext() {
        return super.hasNext();
    }
}
