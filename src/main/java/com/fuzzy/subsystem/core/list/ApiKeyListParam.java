package com.fuzzy.subsystem.core.list;

import com.fuzzy.subsystem.core.graphql.query.privilege.GInputPrivilege;
import com.fuzzy.subsystems.graphql.input.GPaging;
import com.fuzzy.subsystems.graphql.input.GTextFilter;
import com.fuzzy.subsystems.list.ListParam;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class ApiKeyListParam extends ListParam<Long> {

    private final List<GInputPrivilege> privilegeFilter;

    protected ApiKeyListParam(Builder builder) {
        super(builder);
        this.privilegeFilter = builder.privilegeFilter != null ?
                Collections.unmodifiableList(builder.privilegeFilter) : null;
    }

    public List<GInputPrivilege> getPrivilegeFilter() {
        return privilegeFilter;
    }

    public static class Builder extends ListParam.Builder<Long> {

        List<GInputPrivilege> privilegeFilter;

        public Builder withPrivilegeFilter(@Nullable ArrayList<GInputPrivilege> privilegeFilter) {
            this.privilegeFilter = privilegeFilter;
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
        public ApiKeyListParam build() {
            return new ApiKeyListParam(this);
        }
    }
}

