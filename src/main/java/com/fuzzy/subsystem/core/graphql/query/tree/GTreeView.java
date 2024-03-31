package com.fuzzy.subsystem.core.graphql.query.tree;

import com.fuzzy.cluster.core.remote.struct.RemoteObject;
import com.fuzzy.cluster.graphql.anotation.GraphQLDescription;
import com.fuzzy.cluster.graphql.anotation.GraphQLTypeOutObject;

@GraphQLTypeOutObject("tree_view")
public enum GTreeView implements RemoteObject {

    @GraphQLDescription("Только группы")
    NODE(1),

    @GraphQLDescription("Группы и элементы")
    NODE_ITEM(2);

    private final int id;

    GTreeView(int id) {
        this.id = id;
    }


    public int getId() {
        return id;
    }


    public static GTreeView get(long id) {
        for (GTreeView item: GTreeView.values()){
            if (item.getId() == id){
                return item;
            }
        }
        return null;
    }
}
