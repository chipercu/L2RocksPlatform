package com.fuzzy.subsystems.grouping;

import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.QueryTransaction;
import com.fuzzy.subsystems.function.Function;

import java.util.Collection;
import java.util.HashSet;

public class NodeGrouping {

    @FunctionalInterface
    private interface ForEachFunction {
        boolean apply(final Long id, final QueryTransaction transaction, final Function<Long, Boolean> function)
                throws PlatformException;
    }

    private GroupingEnumerator groupingEnumerator;

    public NodeGrouping(GroupingEnumerator groupingEnumerator) {
        this.groupingEnumerator = groupingEnumerator;
    }

    public boolean forEachParent(
            final Long id,
            final QueryTransaction transaction,
            final Function<Long, Boolean> function
    ) throws PlatformException {
        return groupingEnumerator.forEachParent(id, transaction, function);
    }

    public boolean forEachChild(
            final Long id,
            final QueryTransaction transaction,
            final Function<Long, Boolean> function
    ) throws PlatformException {
        return groupingEnumerator.forEachChild(id, transaction,function);
    }

    public boolean forEachParentRecursively(
            final Long id,
            final QueryTransaction transaction,
            final Function<Long, Boolean> function
    ) throws PlatformException {
        return forEachRecursively(id, transaction, this::forEachParent, function);
    }

    public boolean forEachChildRecursively(
            final Long id,
            final QueryTransaction transaction,
            final Function<Long, Boolean> function
    ) throws PlatformException {
        return forEachRecursively(id, transaction, this::forEachChild, function);
    }

    public HashSet<Long> getParents(final Long id, final QueryTransaction transaction) throws PlatformException {
        HashSet<Long> parents = new HashSet<>();
        forEachParent(id, transaction, parents::add);
        return parents;
    }

    public HashSet<Long> getParentsRecursively(final Long id, final QueryTransaction transaction)
            throws PlatformException {
        HashSet<Long> parents = new HashSet<>();
        forEachParentRecursively(id, transaction, parents::add);
        return parents;
    }

    public HashSet<Long> getChildren(final Long id, final QueryTransaction transaction) throws PlatformException {
        HashSet<Long> children = new HashSet<>();
        forEachChild(id, transaction, children::add);
        return children;
    }

    public HashSet<Long> getChildrenRecursively(final Long id, final QueryTransaction transaction)
            throws PlatformException {
        HashSet<Long> children = new HashSet<>();
        forEachChildRecursively(id, transaction, children::add);
        return children;
    }

    public HashSet<Long> getChildrenRecursively(final Collection<Long> ids, final QueryTransaction transaction)
            throws PlatformException {
        HashSet<Long> children = new HashSet<>();
        for (Long id : ids) {
            forEachChildRecursively(id, transaction, children::add);
        }
        return children;
    }

    public boolean isParent(final Long checkingId, final Long childId, final QueryTransaction transaction)
            throws PlatformException {
        return !forEachParentRecursively(childId, transaction, parentId -> !parentId.equals(checkingId));
    }

    private boolean forEachRecursively(
            final Long id,
            final QueryTransaction transaction,
            final ForEachFunction forEachFunction,
            final Function<Long, Boolean> function
    ) throws PlatformException {
        HashSet<Long> ids = new HashSet<>();
        HashSet<Long> collectedIds = new HashSet<>();
        ids.add(id);
        while (!ids.isEmpty()) {
            for (Long pKey : ids) {
                if (!forEachFunction.apply(pKey, transaction, parentId -> {
                    collectedIds.add(parentId);
                    return function.apply(parentId);
                })) {
                    return false;
                }
            }
            ids.clear();
            ids.addAll(collectedIds);
            collectedIds.clear();
        }
        return true;
    }
}
