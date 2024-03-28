package com.fuzzy.subsystems.utils;

import java.time.Instant;
import java.util.Comparator;

public class ComparatorUtility {

    public static int compare(String o1, String o2) {
        return compare(o1, o2, String::compareToIgnoreCase);
    }

    public static int compare(long id1, String o1, long id2, String o2) {
        int result = compare(o1, o2);
        if (result == 0) {
            result = Long.compare(id1, id2);
        }
        return result;
    }

    public static int compare(Instant o1, Instant o2) {
        return compare(o1, o2, Instant::compareTo);
    }

    public static int compare(long id1, Instant o1, long id2, Instant o2) {
        int result = compare(o1, o2);
        if (result == 0) {
            result = Long.compare(id1, id2);
        }
        return result;
    }

    private static <T> int compare(T o1, T o2, Comparator<T> comparator) {
        if (o1 == null || o2 == null) {
            return o1 == o2 ? 0 : (o1 == null ? -1 : 1);
        } else {
            return comparator.compare(o1, o2);
        }
    }
}
