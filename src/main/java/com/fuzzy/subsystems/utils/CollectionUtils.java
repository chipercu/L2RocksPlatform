package com.fuzzy.subsystems.utils;

import java.util.Collection;
import java.util.Set;

public class CollectionUtils {

    public static <T> boolean isIntersected(Collection<T> firstCollection, Set<T> secondCollection) {
        for (T firstItem : firstCollection) {
            if (secondCollection.contains(firstItem)) {
                return true;
            }
        }
        return false;
    }

    public static <E, T extends Collection<E>> T retainCollections(T first, T second) {
        if (second != null) {
            if (first == null) {
                first = second;
            } else {
                first.retainAll(second);
            }
        }
        return first;
    }
}
