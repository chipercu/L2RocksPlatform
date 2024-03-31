package com.fuzzy.subsystems.sorter;

import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.exception.runtime.PlatformRuntimeException;

import java.util.Collections;
import java.util.List;

public class BinarySearch {

    private BinarySearch() {
    }

    public static <T> int findPosition(List<T> sortedList, T object, SorterComparator<T> comparator)
            throws PlatformException {
        int position;
        try {
            position = Collections.binarySearch(sortedList, object, (o1, o2) -> {
                try {
                    return comparator.compare(o1, o2);
                } catch (PlatformException e) {
                    throw new PlatformRuntimeException(e);
                }
            });
        } catch (PlatformRuntimeException e) {
            throw e.getPlatformException();
        }
        if (position < 0) {
            position = -position - 1;
        }
        return position;
    }
}
