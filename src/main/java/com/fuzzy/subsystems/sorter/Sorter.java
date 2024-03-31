package com.fuzzy.subsystems.sorter;

import com.fuzzy.platform.exception.PlatformException;

import java.util.ArrayList;
import java.util.function.Consumer;

public class Sorter<T> {

    private final ArrayList<T> data;
    private final SorterComparator<T> comparator;
    private final int limit;
    private final Consumer<T> onMovedToRestFunction;

    private boolean hasNext;

    public Sorter(SorterComparator<T> comparator, Integer limit, Consumer<T> onMovedToRestFunction) {
        this.data = new ArrayList <>();
        this.comparator = comparator;
        this.limit = limit == null ? Integer.MAX_VALUE : limit;
        this.onMovedToRestFunction = onMovedToRestFunction;
    }

    public Sorter(SorterComparator<T> comparator, Integer limit) {
        this(comparator, limit, null);
    }

    public Sorter(SorterComparator<T> comparator) {
        this(comparator, null, null);
    }

    public void add(T object) throws PlatformException {
        int index = BinarySearch.findPosition(data, object, comparator);
        data.add(index, object);
        if (data.size() > limit) {
            T val = data.remove(data.size() - 1);
            if (onMovedToRestFunction != null) {
                onMovedToRestFunction.accept(val);
            }
            hasNext = true;
        }
    }

    public void clear() {
        data.clear();
        hasNext = false;
    }

    public boolean isEmpty() {
        return data.isEmpty() && !hasNext;
    }

    public ArrayList<T> getData() {
        return data;
    }

    public boolean hasNext() {
        return hasNext;
    }
}
