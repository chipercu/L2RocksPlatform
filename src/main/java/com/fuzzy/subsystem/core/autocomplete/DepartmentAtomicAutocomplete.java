package com.fuzzy.subsystem.core.autocomplete;

import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.ResourceProvider;
import com.infomaximum.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystem.core.domainobject.department.DepartmentReadable;
import com.fuzzy.subsystem.core.employeeaccess.ManagerEmployeeAccess;
import com.fuzzy.subsystem.core.employeeaccess.ManagerEmployeeAccessGetter;
import com.fuzzy.subsystem.core.textfilter.DepartmentTextFilterGetter;
import com.fuzzy.subsystems.autocomplete.AtomicAutocompleteImpl;

public class DepartmentAtomicAutocomplete extends AtomicAutocompleteImpl<DepartmentReadable> {

    private final ManagerEmployeeAccessGetter managerEmployeeAccessGetter;
    private ManagerEmployeeAccess access = null;
    private Long authEmployeeId = null;

    public DepartmentAtomicAutocomplete(ResourceProvider resources) {
        super(
                new DepartmentTextFilterGetter(resources),
                DepartmentTextFilterGetter.FIELD_NAMES,
                new DepartmentPathGetter(resources)
        );
        managerEmployeeAccessGetter = new ManagerEmployeeAccessGetter(resources);
    }

    public void setAuthEmployeeId(Long authEmployeeId) {
        this.authEmployeeId = authEmployeeId;
    }

    @Override
    protected boolean checkItem(DepartmentReadable item, ContextTransaction<?> context) throws PlatformException {
        if (authEmployeeId == null) {
            return true;
        }
        if (access == null) {
            access = managerEmployeeAccessGetter.getAccess(authEmployeeId, context.getTransaction());
        }
        return access.checkDepartment(item.getId());
    }
}
