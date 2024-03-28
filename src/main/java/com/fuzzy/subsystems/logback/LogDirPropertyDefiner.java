package com.fuzzy.subsystems.logback;

import ch.qos.logback.core.PropertyDefinerBase;

public class LogDirPropertyDefiner extends PropertyDefinerBase {

    public static final String LOG_DIR = (System.getProperty("log_dir") == null) ? "logs" : System.getProperty("log_dir");

    @Override
    public String getPropertyValue() {
        return LOG_DIR;
    }
}
