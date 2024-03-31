package com.fuzzy.subsystem.core.graphql.query.accessrole.list;

import com.fuzzy.cluster.core.remote.struct.RemoteObject;
import com.fuzzy.cluster.graphql.anotation.GraphQLDescription;
import com.fuzzy.cluster.graphql.anotation.GraphQLField;
import com.fuzzy.cluster.graphql.anotation.GraphQLTypeOutObject;
import com.fuzzy.platform.sdk.graphql.annotation.GraphQLAuthControl;
import com.fuzzy.subsystem.core.graphql.query.accessrole.GAccessRole;
import com.fuzzy.subsystem.frontend.authcontext.AuthorizedContext;
import com.fuzzy.subsystems.list.RemoteListItem;

@GraphQLTypeOutObject("access_role_list_element")
public class GAccessRoleListItem extends RemoteListItem<GAccessRole> implements RemoteObject {

    private GAccessRoleListItem() {
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Элемент")
    public GAccessRole getElement() {
        return super.getElement();
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
}
