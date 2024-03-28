package com.fuzzy.subsystems.grouping;

import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.QueryTransaction;
import com.fuzzy.subsystems.function.Function;

import java.util.Collection;
import java.util.HashSet;

public class NodeItemGrouping {

    @FunctionalInterface
    private interface ForEachFunction {
        boolean apply(final Long id, final QueryTransaction transaction, final Function<Long, Boolean> function)
                throws PlatformException;
    }

    private NodeGrouping nodeGrouping;

    private GroupingEnumerator itemGroupingEnumerator;

    public NodeItemGrouping(GroupingEnumerator nodeGroupingEnumerator, GroupingEnumerator itemGroupingEnumerator) {
        this.nodeGrouping = new NodeGrouping(nodeGroupingEnumerator);
        this.itemGroupingEnumerator = itemGroupingEnumerator;
    }

    public boolean forEachParentOfNode(
            final Long nodeId,
            final QueryTransaction transaction,
            final Function<Long, Boolean> function
    ) throws PlatformException {
        return nodeGrouping.forEachParent(nodeId, transaction, function);
    }

    public boolean forEachParentOfNodeRecursively(
            final Long nodeId,
            final QueryTransaction transaction,
            final Function<Long, Boolean> function
    ) throws PlatformException {
        return nodeGrouping.forEachParentRecursively(nodeId, transaction, function);
    }

    public boolean forEachParentOfItem(
            final Long itemId,
            final QueryTransaction transaction,
            final Function<Long, Boolean> function
    ) throws PlatformException {
        return itemGroupingEnumerator.forEachParent(itemId, transaction, function);
    }

    public boolean forEachParentOfItemRecursively(
            final Long itemId,
            final QueryTransaction transaction,
            final Function<Long, Boolean> function
    ) throws PlatformException {
        HashSet<Long> parents = new HashSet<>();
        if (!itemGroupingEnumerator.forEachParent(itemId, transaction, parentId -> {
            parents.add(parentId);
            return function.apply(parentId);
        })) {
            return false;
        }
        for (Long nodeId : parents) {
            if (!nodeGrouping.forEachParentRecursively(nodeId, transaction, function::apply)) {
                return false;
            }
        }
        return true;
    }

    public boolean forEachChildNode(
            final Long nodeId,
            final QueryTransaction transaction,
            final Function<Long, Boolean> function
    ) throws PlatformException {
        return nodeGrouping.forEachChild(nodeId, transaction, function);
    }

    public boolean forEachChildNodeRecursively(
            final Long nodeId,
            final QueryTransaction transaction,
            final Function<Long, Boolean> function
    ) throws PlatformException {
        return nodeGrouping.forEachChildRecursively(nodeId, transaction, function);
    }

    public boolean forEachChildItem(
            final Long nodeId,
            final QueryTransaction transaction,
            final Function<Long, Boolean> function
    ) throws PlatformException {
        return itemGroupingEnumerator.forEachChild(nodeId, transaction, function);
    }

    public boolean forEachChildItemRecursively(
            final Long nodeId,
            final QueryTransaction transaction,
            final Function<Long, Boolean> function
    ) throws PlatformException {
        if (!itemGroupingEnumerator.forEachChild(nodeId, transaction, function)) {
            return false;
        }
        return nodeGrouping.forEachChildRecursively(nodeId, transaction,
                childNodeId -> itemGroupingEnumerator.forEachChild(childNodeId, transaction, function));
    }

    public HashSet<Long> getParentsOfNode(final Long nodeId, final QueryTransaction transaction)
            throws PlatformException {
        return getIds(nodeId, transaction, this::forEachParentOfNode);
    }

    public HashSet<Long> getParentsOfNodeRecursively(final Long nodeId, final QueryTransaction transaction)
            throws PlatformException {
        return getIds(nodeId, transaction, this::forEachParentOfNodeRecursively);
    }

    public HashSet<Long> getParentsOfItem(final Long itemId, final QueryTransaction transaction)
            throws PlatformException {
        return getIds(itemId, transaction, this::forEachParentOfItem);
    }

    public HashSet<Long> getParentsOfItemRecursively(final Long itemId, final QueryTransaction transaction)
            throws PlatformException {
        return getIds(itemId, transaction, this::forEachParentOfItemRecursively);
    }

    public HashSet<Long> getChildNodes(final Long nodeId, final QueryTransaction transaction) throws PlatformException {
        return getIds(nodeId, transaction, this::forEachChildNode);
    }

    public HashSet<Long> getChildNodesRecursively(final Long nodeId, final QueryTransaction transaction)
            throws PlatformException {
        return getIds(nodeId, transaction, this::forEachChildNodeRecursively);
    }

    public HashSet<Long> getChildNodesRecursively(final Collection<Long> nodes, final QueryTransaction transaction)
            throws PlatformException {
        return getIds(nodes, transaction, this::forEachChildNodeRecursively);
    }

    public HashSet<Long> getChildItems(final Long nodeId, final QueryTransaction transaction) throws PlatformException {
        return getIds(nodeId, transaction, this::forEachChildItem);
    }

    public HashSet<Long> getChildItems(final Collection<Long> nodes, final QueryTransaction transaction)
            throws PlatformException {
        return getIds(nodes, transaction, this::forEachChildItem);
    }

    public HashSet<Long> getChildItemsRecursively(final Long nodeId, final QueryTransaction transaction)
            throws PlatformException {
        return getIds(nodeId, transaction, this::forEachChildItemRecursively);
    }

    public HashSet<Long> getChildItemsRecursively(final Collection<Long> nodes, final QueryTransaction transaction)
            throws PlatformException {
        return getIds(nodes, transaction, this::forEachChildItemRecursively);
    }

    public boolean isParentOfNode(
            final Long checkingNodeId,
            final Long childNodeId,
            final QueryTransaction transaction
    ) throws PlatformException {
        return isParentOf(checkingNodeId, childNodeId, transaction, this::forEachParentOfNodeRecursively);
    }

    public boolean isParentOfItem(
            final Long checkingNodeId,
            final Long childItemId,
            final QueryTransaction transaction
    ) throws PlatformException {
        return isParentOf(checkingNodeId, childItemId, transaction, this::forEachParentOfItemRecursively);
    }

    private HashSet<Long> getIds(final Long id, final QueryTransaction transaction, final ForEachFunction forEachFunction)
            throws PlatformException {
        HashSet<Long> ids = new HashSet<>();
        forEachFunction.apply(id, transaction, ids::add);
        return ids;
    }

    private HashSet<Long> getIds(final Collection<Long> inputIds, final QueryTransaction transaction, final ForEachFunction forEachFunction)
            throws PlatformException {
        HashSet<Long> ids = new HashSet<>();
        for (Long inputId : inputIds) {
            forEachFunction.apply(inputId, transaction, ids::add);
        }
        return ids;
    }

    private boolean isParentOf(
            final Long checkingId,
            final Long childId,
            final QueryTransaction transaction,
            final ForEachFunction forEachFunction
    ) throws PlatformException {
        return !forEachFunction.apply(childId, transaction, parentNodeId -> !checkingId.equals(parentNodeId));
    }
}
