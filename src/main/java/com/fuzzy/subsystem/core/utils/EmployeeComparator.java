package com.fuzzy.subsystem.core.utils;

import com.fuzzy.subsystem.core.config.DisplayNameFormat;
import com.fuzzy.subsystem.core.domainobject.employee.EmployeeReadable;

public class EmployeeComparator extends AbstractEmployeeComparator<EmployeeReadable>{

    public EmployeeComparator(DisplayNameFormat displayNameFormat) {
        super(displayNameFormat);
    }

    @Override
    protected String getFirstName(EmployeeReadable employee) {
        return employee.getFirstName();
    }

    @Override
    protected String getSecondName(EmployeeReadable employee) {
        return employee.getSecondName();
    }

    @Override
    protected String getPatronymic(EmployeeReadable employee) {
        return employee.getPatronymic();
    }

}
