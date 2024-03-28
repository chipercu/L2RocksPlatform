package com.fuzzy.subsystems.list;

import com.fuzzy.subsystems.graphql.input.GPaging;
import com.fuzzy.subsystems.graphql.input.GTextFilter;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Collections;
import java.util.Set;

public class ListParam<T> {

    private final GTextFilter textFilter;
    private final GPaging paging;
    private final Set<T> alwaysComingItems;
    private final Set<T> topItems;
    private final Set<T> idFilters;

    protected ListParam(Builder<T> builder) {
        textFilter = builder.textFilter;
        paging = builder.paging;
        alwaysComingItems = builder.alwaysComingItems != null ?
                Collections.unmodifiableSet(builder.alwaysComingItems) : null;
        topItems = builder.topItems != null ? Collections.unmodifiableSet(builder.topItems) : null;
        idFilters = builder.idFilters != null ? Collections.unmodifiableSet(builder.idFilters) : null;
    }

    public GTextFilter getTextFilter() {
        return textFilter;
    }

    public GPaging getPaging() {
        return paging;
    }

    public Set<T> getAlwaysComingItems() {
        return alwaysComingItems;
    }

    public Set<T> getTopItems() {
        return topItems;
    }

    public Set<T> getIdFilters() {
        return idFilters;
    }

    public static class Builder<T> {

        private GTextFilter textFilter;
        private GPaging paging;
        private Set<T> alwaysComingItems;
        private Set<T> topItems;
        private Set<T> idFilters;

        public Builder<T> withTextFilter(@Nullable GTextFilter textFilter) {
            this.textFilter = textFilter;
            return this;
        }

        public Builder<T> withPaging(@Nullable GPaging paging) {
            this.paging = paging;
            return this;
        }

        public Builder<T> withAlwaysComingItems(@Nullable Set<T> alwaysComingItems) {
            this.alwaysComingItems = alwaysComingItems;
            return this;
        }

        public Builder<T> withTopItems(@Nullable Set<T> topItems) {
            this.topItems = topItems;
            return this;
        }

        public Builder<T> withIdFilters(@Nullable Set<T> idFilters) {
            this.idFilters = idFilters;
            return this;
        }

        public ListParam<T> build() {
            return new ListParam<>(this);
        }
    }
}
