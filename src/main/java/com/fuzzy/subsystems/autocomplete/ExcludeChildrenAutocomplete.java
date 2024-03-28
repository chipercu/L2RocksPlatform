package com.fuzzy.subsystems.autocomplete;

import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.sdk.context.ContextTransaction;
import com.fuzzy.main.rdao.database.domainobject.DomainObject;
import com.fuzzy.subsystems.graphql.input.GPaging;
import com.fuzzy.subsystems.graphql.input.GTextFilter;
import com.fuzzy.subsystems.grouping.NodeItemGrouping;

import java.util.HashSet;

public class ExcludeChildrenAutocomplete<Node extends DomainObject, Item extends DomainObject>
        extends Autocomplete<Node, Item> {

    private final NodeItemGrouping grouping;

    public ExcludeChildrenAutocomplete(
            AtomicAutocomplete<Node> nodeAtomicAutocomplete,
            AtomicAutocomplete<Item> itemAtomicAutocomplete,
            NodeItemGrouping grouping) {
        super(
                nodeAtomicAutocomplete,
                itemAtomicAutocomplete
        );
        this.grouping = grouping;
    }

    @Override
    public AutocompleteResult<Node, Item> execute(
            final GTextFilter textFilter,
            final HashSet<Long> excludedNodes,
            final HashSet<Long> excludedItems,
            final GPaging paging,
            final ContextTransaction<?> context
            ) throws PlatformException {
        HashSet<Long> innerExcludedNodes = null;
        HashSet<Long> innerExcludedItems = null;
        if (excludedNodes != null) {
            innerExcludedNodes = grouping.getChildNodesRecursively(excludedNodes, context.getTransaction());
            innerExcludedNodes.addAll(excludedNodes);
            innerExcludedItems = grouping.getChildItems(innerExcludedNodes, context.getTransaction());
        }
        if (excludedItems != null) {
            if (innerExcludedItems == null) {
                innerExcludedItems = excludedItems;
            } else {
                innerExcludedItems.addAll(excludedItems);
            }
        }
        return super.execute(textFilter, innerExcludedNodes, innerExcludedItems, paging, context);
    }
}
