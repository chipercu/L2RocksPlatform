package com.fuzzy.subsystem.core.autocomplete;

import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.ResourceProvider;
import com.fuzzy.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystem.core.domainobject.department.DepartmentReadable;
import com.fuzzy.subsystem.core.grouping.DepartmentGrouping;
import com.fuzzy.subsystems.autocomplete.LightAutocomplete;
import com.fuzzy.subsystems.autocomplete.LightAutocompleteResult;
import com.fuzzy.subsystems.graphql.input.GPaging;
import com.fuzzy.subsystems.graphql.input.GTextFilter;

import java.util.HashSet;

public class DepartmentAutocomplete {

    private final DepartmentAtomicAutocomplete departmentAutocomplete;
    private final DepartmentGrouping grouping;

    public DepartmentAutocomplete(ResourceProvider resources) {
        this.departmentAutocomplete = new DepartmentAtomicAutocomplete(resources);
        grouping = new DepartmentGrouping(resources);
    }

    public LightAutocompleteResult<DepartmentReadable> execute(
            GTextFilter textFilter,
            HashSet<Long> excludedDepartments,
            GPaging paging,
            Long targetDepartmentId,
            ContextTransaction<?> context
    ) throws PlatformException {
        HashSet<Long> innerExcludedDepartments = null;
        if (targetDepartmentId != null) {
            innerExcludedDepartments = grouping.getChildNodesRecursively(targetDepartmentId, context.getTransaction());
            innerExcludedDepartments.add(targetDepartmentId);
            if (excludedDepartments != null) {
                innerExcludedDepartments.addAll(excludedDepartments);
            }
        } else if (excludedDepartments != null) {
            innerExcludedDepartments = excludedDepartments;
        }
        LightAutocomplete<DepartmentReadable> autocomplete = new LightAutocomplete<>(departmentAutocomplete);
        return autocomplete.execute(textFilter, innerExcludedDepartments, paging, context);
    }

    public void setAuthEmployeeId(Long id) {
        departmentAutocomplete.setAuthEmployeeId(id);
    }
}
