package com.fuzzy.subsystem.core.utils;

import com.fuzzy.subsystem.core.config.DisplayNameFormat;
import com.fuzzy.subsystem.core.graphql.query.employee.GEmployee;

public class GEmployeeComparator extends AbstractEmployeeComparator<GEmployee> {

    public GEmployeeComparator(DisplayNameFormat displayNameFormat) {
        super(displayNameFormat);
    }

    @Override
    protected String getFirstName(GEmployee employee) {
        return employee.getSource().getFirstName();
    }

    @Override
    protected String getSecondName(GEmployee employee) {
        return employee.getSource().getSecondName();
    }

    @Override
    protected String getPatronymic(GEmployee employee) {
        return employee.getSource().getPatronymic();
    }


}