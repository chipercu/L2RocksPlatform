package com.fuzzy.subsystem.core.license.updater.schemeupdater;

import com.fuzzy.platform.exception.PlatformException;
import net.minidev.json.JSONObject;

public interface LicenseSchemeUpdater {
    long UNLIMITED = -1L;
    String LIMIT = "limit";
    String MODULES_LIMITS = "modules_limits";
    String AUTOMATION_WEBHOOK_UUID = "com.fuzzy.subsystem.automationwebhook";
    String CORE_UUID = "com.fuzzy.subsystem.core";
    String AUTOMATION_UUID = "com.fuzzy.subsystem.automation";
    String DASHBOARD_UUID = "com.fuzzy.subsystem.dashboard";
    String BI_DATA_UUID = "com.fuzzy.subsystem.bidata";
    String CLICKHOUSE_SQL_EXECUTION_TIMEOUT_KEY_V3 = "clickhouse_sql_execution_timeout";

    int update(JSONObject licenseJson) throws PlatformException;
}
