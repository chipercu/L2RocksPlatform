package com.fuzzy.subsystem.core.utils;

import com.fuzzy.subsystem.core.config.DisplayNameFormat;
import com.fuzzy.subsystems.remote.Identifiable;
import com.fuzzy.subsystems.sorter.SorterComparator;
import com.fuzzy.subsystems.utils.ComparatorUtility;
import org.apache.commons.lang3.StringUtils;

public abstract class AbstractEmployeeComparator<T extends Identifiable<Long>> implements SorterComparator<T> {

    private final DisplayNameFormat displayNameFormat;

    public AbstractEmployeeComparator(DisplayNameFormat displayNameFormat) {
        this.displayNameFormat = displayNameFormat;
    }

    @Override
    public int compare(T employee1, T employee2) {
        if (isEmpty(employee1)) {
            return 1;
        } else if (isEmpty(employee2)) {
            return -1;
        }
        int result = 0;
        switch (displayNameFormat) {
            case FIRST_SECOND -> {
                result = ComparatorUtility.compare(getFirstName(employee1), getFirstName(employee2));
                if (result == 0) {
                    result = ComparatorUtility.compare(getPatronymic(employee1), getPatronymic(employee2));
                }
                if (result == 0) {
                    result = ComparatorUtility.compare(getSecondName(employee1), getSecondName(employee2));
                }
            }
            case SECOND_FIRST -> {
                result = ComparatorUtility.compare(getSecondName(employee1), getSecondName(employee2));
                if (result == 0) {
                    result = ComparatorUtility.compare(getFirstName(employee1), getFirstName(employee2));
                }
                if (result == 0) {
                    result = ComparatorUtility.compare(getPatronymic(employee1), getPatronymic(employee2));
                }
            }
        }
        if (result == 0) {
            result =  employee1.getIdentifier().compareTo(employee2.getIdentifier());
        }
        return result;
    }

    protected abstract String getFirstName(T employee);

    protected abstract String getSecondName(T employee);

    protected abstract String getPatronymic(T employee);

    private boolean isEmpty(T employee) {
        return StringUtils.isEmpty(getFirstName(employee)) &&
                StringUtils.isEmpty(getSecondName(employee)) &&
                StringUtils.isEmpty(getPatronymic(employee));
    }
}
