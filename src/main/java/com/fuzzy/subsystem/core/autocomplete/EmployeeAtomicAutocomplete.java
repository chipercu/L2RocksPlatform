package com.fuzzy.subsystem.core.autocomplete;

import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.ResourceProvider;
import com.fuzzy.main.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystem.core.config.CoreConfigDescription;
import com.fuzzy.subsystem.core.config.CoreConfigGetter;
import com.fuzzy.subsystem.core.config.DisplayNameFormat;
import com.fuzzy.subsystem.core.domainobject.employee.EmployeeReadable;
import com.fuzzy.subsystem.core.employeeaccess.ManagerEmployeeAccess;
import com.fuzzy.subsystem.core.employeeaccess.ManagerEmployeeAccessGetter;
import com.fuzzy.subsystem.core.textfilter.EmployeeTextFilterGetter;
import com.fuzzy.subsystems.autocomplete.AtomicAutocompleteImpl;
import com.fuzzy.subsystems.autocomplete.AtomicAutocompleteItem;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class EmployeeAtomicAutocomplete extends AtomicAutocompleteImpl<EmployeeReadable> {

    private final CoreConfigGetter coreConfigGetter;
    private final ManagerEmployeeAccessGetter managerEmployeeAccessGetter;
    private ManagerEmployeeAccess access = null;
    private Long authEmployeeId = null;

    public EmployeeAtomicAutocomplete(ResourceProvider resources) {
        super(
                new EmployeeTextFilterGetter(resources),
                null,
                new EmployeePathGetter(resources)
        );
        coreConfigGetter = new CoreConfigGetter(resources);
        managerEmployeeAccessGetter = new ManagerEmployeeAccessGetter(resources);
    }

    public void setAuthEmployeeId(Long authEmployeeId) {
        this.authEmployeeId = authEmployeeId;
    }

    @Override
    public List<AtomicAutocompleteItem<EmployeeReadable>> get(
            final List<String> sortedByLengthFilterWords,
            final HashSet<Long> excludedEmployees,
            final ContextTransaction<?> context
    ) throws PlatformException {
        DisplayNameFormat displayNameFormat =
                coreConfigGetter.get(CoreConfigDescription.DISPLAY_NAME_FORMAT, context.getTransaction());
        switch (displayNameFormat) {
            case FIRST_SECOND:
                setDisplayNameFieldNumbers(Arrays.asList(
                        EmployeeReadable.FIELD_FIRST_NAME,
                        EmployeeReadable.FIELD_PATRONYMIC,
                        EmployeeReadable.FIELD_SECOND_NAME
                ));
                break;
            case SECOND_FIRST:
                setDisplayNameFieldNumbers(Arrays.asList(
                        EmployeeReadable.FIELD_SECOND_NAME,
                        EmployeeReadable.FIELD_FIRST_NAME,
                        EmployeeReadable.FIELD_PATRONYMIC
                ));
                break;
        }
        return super.get(sortedByLengthFilterWords, excludedEmployees, context);
    }

    @Override
    protected boolean checkItem(EmployeeReadable item, ContextTransaction<?> context) throws PlatformException {
        if (authEmployeeId == null) {
            return true;
        }
        if (access == null) {
            access = managerEmployeeAccessGetter.getAccess(authEmployeeId, context.getTransaction());
        }
        return access.checkEmployee(item.getId());
    }
}
