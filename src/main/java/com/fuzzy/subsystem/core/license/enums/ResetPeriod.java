package com.fuzzy.subsystem.core.license.enums;

import com.fuzzy.database.utils.BaseEnum;

import java.time.Period;

public enum ResetPeriod implements BaseEnum {
    DAY(1),
    WEEK(2),
    MONTH(3);

    private final int id;

    ResetPeriod(int id) {
        this.id = id;
    }

    @Override
    public int intValue() {
        return id;
    }

    public static ResetPeriod get(int id) {
        for (ResetPeriod value : ResetPeriod.values()) {
            if (value.intValue() == id) {
                return value;
            }
        }
        return null;
    }

    public Period toPeriod() {
        return switch (this) {
            case DAY -> Period.ofDays(1);
            case WEEK -> Period.ofWeeks(1);
            case MONTH -> Period.ofMonths(1);
        };
    }

}
