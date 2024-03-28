package com.fuzzy.subsystems.logback;

import ch.qos.logback.core.PropertyDefinerBase;

public class LogNamePropertyDefiner extends PropertyDefinerBase {

    public static final String FILENAME = "main.log";

    @Override
    public String getPropertyValue() {
        return FILENAME;
    }
}
