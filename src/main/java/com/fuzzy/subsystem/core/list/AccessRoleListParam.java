package com.fuzzy.subsystem.core.list;

import com.fuzzy.subsystems.graphql.input.GPaging;
import com.fuzzy.subsystems.graphql.input.GTextFilter;
import com.fuzzy.subsystems.list.ListParam;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Collections;
import java.util.Set;

public class AccessRoleListParam extends ListParam<Long> {

    private final Set<Long> employeeFilter;

    protected AccessRoleListParam(Builder builder) {
        super(builder);
        this.employeeFilter = builder.employeeFilter != null ?
                Collections.unmodifiableSet(builder.employeeFilter) : null;
    }

    public Set<Long> getEmployeeFilter() {
        return employeeFilter;
    }

    public static class Builder extends ListParam.Builder<Long> {

        Set<Long> employeeFilter;

        public Builder withEmployeeFilter(@Nullable Set<Long> employeeFilter) {
            this.employeeFilter = employeeFilter;
            return this;
        }

        public Builder withTextFilter(@Nullable GTextFilter textFilter) {
            super.withTextFilter(textFilter);
            return this;
        }

        public Builder withPaging(@Nullable GPaging paging) {
            super.withPaging(paging);
            return this;
        }

        public Builder withAlwaysComingItems(@Nullable Set<Long> alwaysComingItems) {
            super.withAlwaysComingItems(alwaysComingItems);
            return this;
        }

        public Builder withTopItems(@Nullable Set<Long> topItems) {
            super.withTopItems(topItems);
            return this;
        }

        public Builder withIdFilters(@Nullable Set<Long> idFilters) {
            super.withIdFilters(idFilters);
            return this;
        }

        @Override
        public AccessRoleListParam build() {
            return new AccessRoleListParam(this);
        }
    }
}
