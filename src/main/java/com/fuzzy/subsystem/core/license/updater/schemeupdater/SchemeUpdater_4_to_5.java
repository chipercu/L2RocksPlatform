package com.fuzzy.subsystem.core.license.updater.schemeupdater;

import com.infomaximum.platform.exception.PlatformException;
import com.fuzzy.subsystem.core.license.enums.LicenseParameter;
import net.minidev.json.JSONObject;

/*
Поднятие схемы с 4 до 5.
    Добавлен параметр: clickhouse_max_memory_usage
    Переименован параметр: sql_query_max_duration -> clickhouse_sql_query_max_duration
    Удален параметр: clickhouse_sql_execution_timeout
Выходной объект соответствует схеме лицензии версии 5
 */
public class SchemeUpdater_4_to_5 implements LicenseSchemeUpdater {

    public static final int OUT_SCHEME_VERSION = 5;
    public static final String DEPRECATED_SQL_QUERY_MAX_DURATION_KEY = "sql_query_max_duration";

    @Override
    public int update(JSONObject licenseJson) throws PlatformException {
        JSONObject jModulesLimits = (JSONObject) licenseJson.get(MODULES_LIMITS);
        addClickHouseMaxMemoryUsage(jModulesLimits);
        renameSqlQueryMaxDuration(jModulesLimits);
        removeClickHouseSqlExecutionTimeout(jModulesLimits);

        return OUT_SCHEME_VERSION;
    }

    private static void addClickHouseMaxMemoryUsage(JSONObject jModulesLimits) {
        JSONObject jBiDataLimits = (JSONObject) jModulesLimits.get(BI_DATA_UUID);
        JSONObject jLimit = new JSONObject();
        jLimit.put(LIMIT, UNLIMITED);
        jBiDataLimits.put(LicenseParameter.CLICKHOUSE_MAX_MEMORY_USAGE.getKey(), jLimit);
    }

    private static void renameSqlQueryMaxDuration(JSONObject jModulesLimits) {
        JSONObject jDashboardLimits = (JSONObject) jModulesLimits.get(DASHBOARD_UUID);
        Object oSqlQueryMaxDuration = jDashboardLimits.get(DEPRECATED_SQL_QUERY_MAX_DURATION_KEY);
        jDashboardLimits.remove(DEPRECATED_SQL_QUERY_MAX_DURATION_KEY);
        jDashboardLimits.put(LicenseParameter.CLICKHOUSE_SQL_QUERY_MAX_DURATION.getKey(), oSqlQueryMaxDuration);
    }

    private static void removeClickHouseSqlExecutionTimeout(JSONObject jModulesLimits) {
        JSONObject jAutomationLimits = (JSONObject) jModulesLimits.get(AUTOMATION_UUID);
        jAutomationLimits.remove(CLICKHOUSE_SQL_EXECUTION_TIMEOUT_KEY_V3);
    }
}
