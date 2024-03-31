package com.fuzzy.subsystems.autocomplete;

import com.fuzzy.database.domainobject.DomainObject;
import com.fuzzy.database.utils.PrefixIndexUtils;
import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.QueryTransaction;

import java.util.ArrayList;
import java.util.List;

public class AutocompleteWeightGetter<T extends DomainObject> {

    private static final long WEIGHT_RANGE = 1000;
    private static final long ONE_WEIGHT_MULTIPLIER = 1;
    private static final long TWO_WEIGHT_MULTIPLIER = ONE_WEIGHT_MULTIPLIER * WEIGHT_RANGE;
    private static final long THREE_WEIGHT_MULTIPLIER = TWO_WEIGHT_MULTIPLIER * WEIGHT_RANGE;
    private static final long FOUR_WEIGHT_MULTIPLIER = THREE_WEIGHT_MULTIPLIER * WEIGHT_RANGE;

    private final List<String> innerFilterWords = new ArrayList<>();

    public Long getWeight(
            final List<String> sortedByLengthFilterWords,
            final List<String> displayName,
            final T object,
            final PathGetter<T> pathGetter,
            final QueryTransaction transaction
    ) throws PlatformException {
        if (sortedByLengthFilterWords.isEmpty()) {
            return 0L;
        }

        innerFilterWords.clear();
        innerFilterWords.addAll(sortedByLengthFilterWords);
        List<String> displayNameWords = new ArrayList <>();
        for (String displayNamePart : displayName) {
             PrefixIndexUtils.forEachWord(displayNamePart,
                    (beginIndex, endIndex) -> displayNameWords.add(
                            displayNamePart.substring(beginIndex, endIndex).toLowerCase()));
        }
        Long weight = null;

        Long firstFindingInDisplayName = getFirstFindingAndExcludeFromFilterWords(displayNameWords);
        if (firstFindingInDisplayName == null) {
            return null;
        }

        if (innerFilterWords.isEmpty()) {
            boolean equal = false;
            if (sortedByLengthFilterWords.size() == displayNameWords.size()) {
                for (int i = 0; i < displayNameWords.size(); i++) {
                    equal = displayNameWords.get(i).equals(sortedByLengthFilterWords.get(i));
                    if (!equal) {
                        break;
                    }
                }
            }
            weight = equal ? FOUR_WEIGHT_MULTIPLIER :
                    (WEIGHT_RANGE - firstFindingInDisplayName - 1) * THREE_WEIGHT_MULTIPLIER;
        } else if (object != null && pathGetter != null) {
            Long firstFindingInPathFromRight = null;
            List<String> path = pathGetter.getPath(object, transaction);
            List<String> pathElementWords = new ArrayList <>();
            for (int i = path.size(); i != 0; i--) {
                String pathElement = path.get(i - 1);
                pathElementWords.clear();
                PrefixIndexUtils.forEachWord(pathElement,
                        (beginIndex, endIndex) -> pathElementWords.add(
                                pathElement.substring(beginIndex, endIndex).toLowerCase()));
                if (getFirstFindingAndExcludeFromFilterWords(pathElementWords) != null &&
                        firstFindingInPathFromRight == null) {
                    firstFindingInPathFromRight = (long) (path.size() - i);
                }
            }
            weight = (WEIGHT_RANGE - firstFindingInDisplayName - 1) * ONE_WEIGHT_MULTIPLIER;
            if (firstFindingInPathFromRight != null)
            {
                weight += (WEIGHT_RANGE - firstFindingInPathFromRight - 1) * TWO_WEIGHT_MULTIPLIER;
            }
        }

        return innerFilterWords.isEmpty() ? weight : null;
    }

    private Long getFirstFindingAndExcludeFromFilterWords(List<String> displayNameWords) {
        Long result = null;
        for (int i = 0; i < displayNameWords.size(); i++) {
            for (int j = 0; j < innerFilterWords.size(); j++) {
                if (displayNameWords.get(i).startsWith(innerFilterWords.get(j))) {
                    innerFilterWords.remove(j);
                    if (result == null) {
                        result = (long) i;
                    }
                    break;
                }
            }
        }
        return result;
    }
}
