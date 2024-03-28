package com.fuzzy.subsystems.list;

import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystems.comparators.IdentifiableComparator;
import com.fuzzy.subsystems.function.Consumer;
import com.fuzzy.subsystems.function.Function;
import com.fuzzy.subsystems.remote.Identifiable;
import com.fuzzy.subsystems.sorter.Sorter;
import com.fuzzy.subsystems.sorter.SorterComparator;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class AbstractListBuilder<K extends Comparable<K>, T extends Identifiable<K>> {

    private record Comparator<K, T>(@NonNull SorterComparator<T> comparator,
                                    @Nullable Set<K> topItems,
                                    @NonNull Function<T, K> idGetter) implements SorterComparator<T> {
        @Override
        public int compare(T o1, T o2) throws PlatformException {
            if (isTop(o1)) {
                return isTop(o2) ? comparator.compare(o1, o2) : -1;
            } else {
                return isTop(o2) ? 1 : comparator.compare(o1, o2);
            }
        }

        private boolean isTop(@NonNull T item) throws PlatformException {
            return topItems != null && topItems.contains(idGetter.apply(item));
        }
    }

    private SorterComparator<T> comparator;

    public void setComparator(@NonNull SorterComparator <T> itemComparator) {
        this.comparator = itemComparator;
    }

    public ListResult<T> build(@NonNull ListParam<K> param, @NonNull ContextTransaction<?> context) throws PlatformException {
        HashSet<K> checkedTopItems = new HashSet<>();
        if (param.getTopItems() != null) {
            forEach(param.getTopItems(), object -> {
                if (checkItem(object, context)) {
                    checkedTopItems.add(object.getIdentifier());
                }
            }, context);
        }
        Integer limit = param.getPaging() != null ?
                Integer.max(param.getPaging().getLimit(), checkedTopItems.size()) : null;
        Comparator<K, T> innerComparator = new Comparator<>(
                this.comparator != null ? this.comparator : new IdentifiableComparator<>(),
                param.getTopItems(),
                Identifiable::getIdentifier
        );
        Sorter<T> sorter = new Sorter <>(innerComparator, limit);
        boolean textFilterExists = param.getTextFilter() != null && param.getTextFilter().isSpecified();
        String text = param.getTextFilter() != null ? param.getTextFilter().getText() : null;
        final int[] matchCount = new int[]{0};
        Consumer<T> handler = object -> {
            if ((param.getIdFilters() == null || param.getIdFilters().contains(object.getIdentifier()))
                    && (checkedTopItems.contains(object.getIdentifier()) || checkItem(object, context))) {
                sorter.add(object);
                matchCount[0]++;
            }
        };
        if (!StringUtils.isEmpty(text)) {
            forEach(text, handler, context);
        } else if (param.getIdFilters() != null) {
            forEach(param.getIdFilters(), handler, context);
        } else {
            forEach(handler, context);
        }
        Set<K> alwaysComingItems = null;
        if (param.getAlwaysComingItems() != null) {
            alwaysComingItems = new HashSet <>(param.getAlwaysComingItems());
        }
        List<ListItem<T>> items = new ArrayList<>();
        for (T object : sorter.getData()) {
            items.add(new ListItem<>(object, textFilterExists, false));
            if (alwaysComingItems != null) {
                alwaysComingItems.remove(object.getIdentifier());
            }
        }
        if (alwaysComingItems != null) {
            forEachAlwaysComingItems(alwaysComingItems, object ->
                    items.add(new ListItem<>(object, false, true)), context);
        }
        return new ListResult<>(items, textFilterExists ? matchCount[0] : 0, sorter.getNextCount());
    }

    protected abstract void forEach(@NonNull Consumer<T> handler,
                                    @NonNull ContextTransaction<?> context) throws PlatformException;

    protected abstract void forEach(@NonNull Set<K> ids,
                                    @NonNull Consumer<T> handler,
                                    @NonNull ContextTransaction<?> context) throws PlatformException;

    protected abstract void forEach(@NonNull String text,
                                    @NonNull Consumer<T> handler,
                                    @NonNull ContextTransaction<?> context) throws PlatformException;

    protected abstract void forEachAlwaysComingItems(@NonNull Set<K> ids,
                                                     @NonNull Consumer<T> handler,
                                                     @NonNull ContextTransaction<?> context) throws PlatformException;

    protected abstract boolean checkItem(@NonNull T item,
                                         @NonNull ContextTransaction<?> context) throws PlatformException;
}
