package com.fuzzy.subsystems.tree;

import com.fuzzy.subsystem.core.graphql.query.tree.GTreeView;
import com.fuzzy.subsystems.graphql.input.GInputNodesItems;
import com.fuzzy.subsystems.graphql.input.GTreePaging;

public class TreeParam {

    public GInputNodesItems rootFilter = null;
    public GInputNodesItems idFilter = null;
    public String textFilter = null;
    public GTreePaging paging = null;
    public GInputNodesItems alwaysComingData = null;
    public GTreeView view = GTreeView.NODE_ITEM;
    public GInputNodesItems topElements = new GInputNodesItems(null, null);
}
