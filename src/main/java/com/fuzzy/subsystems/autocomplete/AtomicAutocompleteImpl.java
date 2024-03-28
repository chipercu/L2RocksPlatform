package com.fuzzy.subsystems.autocomplete;

import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.sdk.context.ContextTransaction;
import com.fuzzy.main.platform.sdk.iterator.Iterator;
import com.fuzzy.main.rdao.database.domainobject.DomainObject;
import com.fuzzy.subsystems.textfilter.TextFilterGetter;

import java.util.*;

public class AtomicAutocompleteImpl<T extends DomainObject> implements AtomicAutocomplete<T> {

    private final TextFilterGetter<T> textFilterGetter;
    private Collection<Integer> displayNameFieldNumbers;
    private final PathGetter<T> pathGetter;
    private final AutocompleteWeightGetter<T> weightGetter = new AutocompleteWeightGetter <>();

    public AtomicAutocompleteImpl(
            TextFilterGetter <T> textFilterGetter,
            Collection<Integer> displayNameFieldNumbers,
            PathGetter<T> pathGetter) {
        this.textFilterGetter = textFilterGetter;
        this.displayNameFieldNumbers = displayNameFieldNumbers;
        this.pathGetter = pathGetter;
    }

    protected void setDisplayNameFieldNumbers(List<Integer> displayNameFieldNumbers) {
        this.displayNameFieldNumbers = displayNameFieldNumbers;
    }

    @Override
    public List<AtomicAutocompleteItem<T>> get(
            final List<String> sortedByLengthFilterWords,
            final HashSet<Long> excluded,
            final ContextTransaction<?> context
    ) throws PlatformException {
        Map<Long, T> items = new HashMap<>();
        List<String> filterWords = sortedByLengthFilterWords.isEmpty() ? Collections.singletonList("") : sortedByLengthFilterWords;
        for (String filterWord : filterWords) {
            try (Iterator<T> ie = textFilterGetter.findAll(filterWord, context.getTransaction())) {
                while (ie.hasNext()) {
                    T object = ie.next();
                    if (checkItem(object, context)) {
                        items.put(object.getId(), object);
                    }
                }
            }
        }
        if (excluded != null) {
            for (Long excludedId : excluded) {
                items.remove(excludedId);
            }
        }
        List<AtomicAutocompleteItem<T>> result = new ArrayList<>();
        for (T object : items.values()) {
            if (sortedByLengthFilterWords.isEmpty()) {
                result.add(new AtomicAutocompleteItem<>(object, 0));
            } else {
                List<String> displayName = new ArrayList<>(displayNameFieldNumbers.size());
                for (Integer fieldNumber : displayNameFieldNumbers) {
                    displayName.add(object.get(fieldNumber));
                }
                Long weight = weightGetter.getWeight(sortedByLengthFilterWords, displayName, object, pathGetter, context.getTransaction());
                if (weight != null) {
                    result.add(new AtomicAutocompleteItem<>(object, weight));
                }
            }
        }
        return result;
    }

    protected boolean checkItem(T item, ContextTransaction<?> context) throws PlatformException {
        return true;
    }
}
