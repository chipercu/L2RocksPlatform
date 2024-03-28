package com.fuzzy.subsystems.comparators;

import java.util.function.BiFunction;

class Tool {

    public static <T> int compare(T o1, T o2, BiFunction<T, T, Integer> compareFunc) {
        if (o1 == o2) {
            return 0;
        } else if (o1 == null) {
            return -1;
        } else if (o2 == null) {
            return 1;
        } else {
            return compareFunc.apply(o1, o2);
        }
    }
}
