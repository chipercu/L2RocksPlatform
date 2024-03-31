package com.fuzzy.subsystem.core.graphql.query.accessrole.list;

import com.fuzzy.cluster.graphql.anotation.GraphQLDescription;
import com.fuzzy.cluster.graphql.anotation.GraphQLField;
import com.fuzzy.cluster.graphql.anotation.GraphQLTypeOutObject;
import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.sdk.graphql.annotation.GraphQLAuthControl;
import com.fuzzy.subsystem.core.domainobject.accessrole.AccessRoleReadable;
import com.fuzzy.subsystem.core.graphql.query.accessrole.GAccessRole;
import com.fuzzy.subsystem.frontend.authcontext.AuthorizedContext;
import com.fuzzy.subsystems.list.ListResult;
import com.fuzzy.subsystems.list.RemoteListResult;

import java.util.ArrayList;

@GraphQLTypeOutObject("access_role_list_result")
public class GAccessRoleListResult extends RemoteListResult<AccessRoleReadable, GAccessRole, GAccessRoleListItem> {

    public GAccessRoleListResult(ListResult<AccessRoleReadable> source) throws PlatformException {
        super(source, GAccessRole::new, GAccessRoleListItem.class);
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Элементы")
    public ArrayList<GAccessRoleListItem> getItems() {
        return super.getItems();
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Количество элементов, соответствующих текстовому фильтру")
    public int getMatchCount() {
        return super.getMatchCount();
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Есть ли элементы за границей лимита")
    public boolean hasNext() {
        return super.hasNext();
    }
}