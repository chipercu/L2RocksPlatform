package com.fuzzy.subsystems.autocomplete;

import com.infomaximum.database.domainobject.DomainObject;
import com.infomaximum.database.utils.PrefixIndexUtils;
import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystems.graphql.input.GPaging;
import com.fuzzy.subsystems.graphql.input.GTextFilter;
import com.fuzzy.subsystems.sorter.Sorter;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class Autocomplete<Node extends DomainObject, Item extends DomainObject> {

    private final AtomicAutocomplete<Node> nodeAtomicAutocomplete;
    private final AtomicAutocomplete<Item> itemAtomicAutocomplete;

    public Autocomplete(
            AtomicAutocomplete <Node> nodeAtomicAutocomplete,
            AtomicAutocomplete <Item> itemAtomicAutocomplete
    ) {
        this.nodeAtomicAutocomplete = nodeAtomicAutocomplete;
        this.itemAtomicAutocomplete = itemAtomicAutocomplete;
    }

    public AutocompleteResult<Node, Item> execute(
            final GTextFilter textFilter,
            final HashSet<Long> excludedNodes,
            final HashSet<Long> excludedItems,
            final GPaging paging,
            final ContextTransaction<?> context
    ) throws PlatformException {
        Sorter<AutocompleteElement<Node, Item>> elements = new Sorter <>(
                (o1, o2) -> {
                    int result = Long.compare(o2.getWeight(), o1.getWeight());
                    if (result == 0) {
                        if (o1.getNode() != null && o2.getNode() != null) {
                            return Long.compare(o1.getNode().getId(), o2.getNode().getId());
                        }
                        if (o1.getItem() != null && o2.getItem() != null) {
                            return Long.compare(o1.getItem().getId(), o2.getItem().getId());
                        }
                        return o1.getNode() != null ? -1 : 1;
                    }
                    return result;
                },
                paging != null ? paging.getLimit() : null
        );

        String text = textFilter != null ? textFilter.getText() : null;
        List<String> filterWords = PrefixIndexUtils.splitSearchingTextIntoWords(text);
        Collections.reverse(filterWords);
        List<AtomicAutocompleteItem<Node>> nodes = nodeAtomicAutocomplete.get(filterWords, excludedNodes, context);
        for (AtomicAutocompleteItem<Node> node : nodes) {
            AutocompleteElement<Node, Item> element = new AutocompleteElement<>();
            element.setWeight(node.getWeight());
            element.setNode(node.getObject());
            elements.add(element);
        }
        List<AtomicAutocompleteItem<Item>> items =
                itemAtomicAutocomplete.get(filterWords, excludedItems, context);
        for (AtomicAutocompleteItem<Item> item : items) {
            AutocompleteElement<Node, Item> element = new AutocompleteElement<>();
            element.setWeight(item.getWeight());
            element.setItem(item.getObject());
            elements.add(element);
        }
        AutocompleteResult<Node, Item> result = new AutocompleteResult<>();
        result.setItems(elements.getData());
        result.setHasNext(elements.hasNext());
        if (textFilter != null && textFilter.isSpecified()) {
            result.setMatchCount(elements.getData().size());
        }
        return result;
    }
}
