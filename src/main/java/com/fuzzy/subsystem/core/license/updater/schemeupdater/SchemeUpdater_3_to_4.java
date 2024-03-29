package com.fuzzy.subsystem.core.license.updater.schemeupdater;

import com.infomaximum.platform.exception.PlatformException;
import net.minidev.json.JSONObject;

/*
Поднятие схемы с 3 до 4.
Добавлен параметр:
SQL_QUERY_MAX_DURATION
Выходной объект соответствует схеме лицензии версии 4
 */
public class SchemeUpdater_3_to_4 implements LicenseSchemeUpdater {
    private static final int OUT_SCHEME_VERSION = 4;
    private static final String SQL_QUERY_MAX_DURATION_KEY = "sql_query_max_duration";

    @Override
    public int update(JSONObject licenseJson) throws PlatformException {
        JSONObject modulesLimitsJson = (JSONObject) licenseJson.get(MODULES_LIMITS);
        JSONObject durationObject = new JSONObject();
        JSONObject limitJson = new JSONObject();
        limitJson.put(LIMIT, UNLIMITED);
        durationObject.put(SQL_QUERY_MAX_DURATION_KEY, limitJson);
        modulesLimitsJson.put(DASHBOARD_UUID, durationObject);
        return OUT_SCHEME_VERSION;
    }
}
