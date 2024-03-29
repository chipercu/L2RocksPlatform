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

public class LightAutocomplete<Item extends DomainObject> {

    private AtomicAutocomplete<Item> itemAtomicAutocomplete;

    public LightAutocomplete(AtomicAutocomplete <Item> itemAtomicAutocomplete) {
        this.itemAtomicAutocomplete = itemAtomicAutocomplete;
    }

    public LightAutocompleteResult<Item> execute(
            final GTextFilter textFilter,
            final HashSet<Long> excludedItems,
            final GPaging paging,
            final ContextTransaction<?> context
    ) throws PlatformException {
        Sorter<LightAutocompleteElement<Item>> elements = new Sorter <>(
                (o1, o2) -> Long.compare(o2.getWeight(), o1.getWeight()),
                paging != null ? paging.getLimit() : null
        );
        String text = textFilter != null ? textFilter.getText() : null;
        List<String> filterWords = PrefixIndexUtils.splitSearchingTextIntoWords(text);
        Collections.reverse(filterWords);
        List<AtomicAutocompleteItem<Item>> items =
                itemAtomicAutocomplete.get(filterWords, excludedItems, context);
        for (AtomicAutocompleteItem<Item> item : items) {
            LightAutocompleteElement<Item> element = new LightAutocompleteElement<>();
            element.setWeight(item.getWeight());
            element.setItem(item.getObject());
            elements.add(element);
        }
        LightAutocompleteResult<Item> result = new LightAutocompleteResult<>();
        result.setItems(elements.getData());
        result.setHasNext(elements.hasNext());
        if (textFilter != null && textFilter.isSpecified()) {
            result.setMatchCount(elements.getData().size());
        }
        return result;
    }
}
