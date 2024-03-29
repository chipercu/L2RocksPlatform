package com.fuzzy.subsystem.core.license.enums;

import com.fuzzy.main.rdao.database.utils.BaseEnum;

public enum LicenseParameter implements BaseEnum {

    DISK_SPACE(1, "disk_space"),
    MIN_SCRIPT_RUN_PERIOD(2, "min_script_run_period"),
    MAIL_SENDING(3, "mail_sending"),
    MONITORING_SIMPLE(4, "monitoring_simple"),
    MONITORING_EXTENDED(5, "monitoring_extended"),
    USERS_WITH_ROLE(6, "users_with_role"),
    SCRIPT_EXECUTION_TIME(7, "script_execution_time"),
    MTU(8, "mtu"),
    CLICKHOUSE_SQL_QUERY_MAX_DURATION(9, "clickhouse_sql_query_max_duration"),
    CLICKHOUSE_MAX_MEMORY_USAGE(10, "clickhouse_max_memory_usage");

    private final int id;
    private final String key;

    LicenseParameter(int id, String key) {
        this.id = id;
        this.key = key;
    }

    @Override
    public int intValue() {
        return id;
    }

    public String getKey() {
        return key;
    }

    public static LicenseParameter getById(int id) {
        for (LicenseParameter value : LicenseParameter.values()) {
            if (value.intValue() == id) {
                return value;
            }
        }
        return null;
    }

    public static LicenseParameter ofKey(String key) {
        for (LicenseParameter licenseParameter : LicenseParameter.values()) {
            if (licenseParameter.getKey().equals(key)) {
                return licenseParameter;
            }
        }
        return null;
    }
}
