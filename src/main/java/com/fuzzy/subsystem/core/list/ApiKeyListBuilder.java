package com.fuzzy.subsystem.core.list;

import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.ResourceProvider;
import com.fuzzy.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystem.core.apikeyprivileges.ApiKeyPrivilegesGetter;
import com.fuzzy.subsystem.core.domainobject.apikey.ApiKeyReadable;
import com.fuzzy.subsystem.core.graphql.query.privilege.GInputPrivilege;
import com.fuzzy.subsystem.core.textfilter.ApiKeyTextFilterGetter;
import com.fuzzy.subsystems.access.AccessOperationCollection;
import com.fuzzy.subsystems.list.ListBuilder;
import com.fuzzy.subsystems.list.ListChecker;
import com.fuzzy.subsystems.list.ListResult;
import com.fuzzy.subsystems.utils.ComparatorUtility;

import java.util.HashMap;
import java.util.List;

public class ApiKeyListBuilder {

    private static class Checker implements ListChecker<ApiKeyReadable> {

        private List<GInputPrivilege> privilegeFilter = null;
        private final ApiKeyPrivilegesGetter apiKeyPrivilegesGetter;

        Checker(ResourceProvider resources) {
            apiKeyPrivilegesGetter = new ApiKeyPrivilegesGetter(resources);
        }

        void setPrivilegeFilter(List<GInputPrivilege> privilegeFilter) {
            this.privilegeFilter = privilegeFilter;
        }

        @Override
        public boolean checkItem(ApiKeyReadable apiKey, ContextTransaction<?> context) throws PlatformException {
            if (privilegeFilter == null) {
                return true;
            }
            HashMap<String, AccessOperationCollection> privileges =
                    apiKeyPrivilegesGetter.getPrivileges(apiKey.getId(), context);
            for (GInputPrivilege privilegeFilterItem : privilegeFilter) {
                if (privilegeFilterItem != null) {
                    AccessOperationCollection operations = privileges.get(privilegeFilterItem.getKey());
                    if (operations != null && operations.contains(privilegeFilterItem.getOperations())) {
                        return true;
                    }
                }
            }
            return false;
        }
    }

    private final Checker checker;
    private final ListBuilder<ApiKeyReadable> listBuilder;

    public ApiKeyListBuilder(ResourceProvider resources) {
        checker = new Checker(resources);
        listBuilder = new ListBuilder<>(
                resources.getReadableResource(ApiKeyReadable.class),
                new ApiKeyTextFilterGetter(resources),
                checker
        );
        listBuilder.setComparator((o1, o2) -> ComparatorUtility.compare(o1.getId(), o1.getName(), o2.getId(), o2.getName()));
    }

    public ListResult<ApiKeyReadable> build(ApiKeyListParam param, ContextTransaction<?> context) throws PlatformException {
        this.checker.setPrivilegeFilter(param.getPrivilegeFilter());
        return listBuilder.build(param, context);
    }
}
