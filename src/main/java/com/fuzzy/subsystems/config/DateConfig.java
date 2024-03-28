package com.fuzzy.subsystems.config;

import java.util.Date;

public class DateConfig extends Config<Date> {

    public DateConfig(String name, Date defaultValue) {
        super(name, defaultValue, Date.class);
    }
}
