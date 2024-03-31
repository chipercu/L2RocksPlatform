package com.fuzzy.subsystems.entityelements;

import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.QueryTransaction;
import com.fuzzy.subsystems.grouping.NodeItemGrouping;

import java.util.HashSet;

public class EntityElementGetter {
    private final EntityElementEnumerator<Long> entityElementEnumerator;
    private final NodeItemGrouping grouping;

    public EntityElementGetter(EntityElementEnumerator<Long> entityElementEnumerator, NodeItemGrouping grouping) {
        this.entityElementEnumerator = entityElementEnumerator;
        this.grouping = grouping;
    }

    public Elements getElements(long entityId, QueryTransaction transaction) throws PlatformException {
        Elements elements = new Elements();
        elements.isAll = entityElementEnumerator.isAll(entityId, transaction);
        if (!elements.isAll) {
            elements.nodes = new HashSet <>();
            entityElementEnumerator.forEachNode(entityId, transaction, nodeId -> {
                elements.nodes.add(nodeId);
                return true;
            });
            elements.nodes.addAll(grouping.getChildNodesRecursively(elements.nodes, transaction));
            elements.items = grouping.getChildItems(elements.nodes, transaction);
            entityElementEnumerator.forEachItem(entityId, transaction, itemId -> {
                elements.items.add(itemId);
                return true;
            });
        }
        return elements;
    }
}
