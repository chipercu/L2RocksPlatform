package com.fuzzy.subsystem.core.license.updater.schemeupdater;

import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.subsystem.core.exception.CoreExceptionBuilder;
import com.fuzzy.subsystem.core.license.enums.LicenseParameter;
import net.minidev.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/*
Поднятие схемы с 2 до 3.
Значения параметров конвертируются из числа в JSON объект {limit:-1}
Добавлены параметры:
MTU
USERS_WITH_ROLE
SCRIPT_EXECUTION_TIME
CLICKHOUSE_SQL_EXECUTION_TIMEOUT
Выходной объект соответствует схеме лицензии версии 3
 */
public class SchemeUpdater_2_to_3 implements LicenseSchemeUpdater {
    private static final int OUT_SCHEME_VERSION = 3;

    @Override
    public int update(JSONObject licenseJson) throws PlatformException {
        JSONObject modulesLimitsJson = (JSONObject) licenseJson.get(MODULES_LIMITS);

        for (Map.Entry<String, Object> entry : modulesLimitsJson.entrySet()) {
            Object moduleLimitsObj = entry.getValue();
            JSONObject moduleLimitsJson = (JSONObject) moduleLimitsObj;
            for (Map.Entry<String, Object> e : moduleLimitsJson.entrySet()) {
                String pKey = e.getKey();
                Object pValue = e.getValue();
                Number limit = (Number) pValue;
                LicenseParameter licenseParameter = LicenseParameter.ofKey(pKey);
                if (licenseParameter == null) {
                    throw CoreExceptionBuilder.buildInvalidLicenseFormatException();
                }
                JSONObject limitAsObj = new JSONObject(new HashMap<>() {{
                    put(LIMIT, limit);
                }});
                e.setValue(limitAsObj);
            }
        }
        JSONObject mtuObject = new JSONObject();
        JSONObject limitJson = new JSONObject();
        limitJson.put(LIMIT, UNLIMITED);
        mtuObject.put(LicenseParameter.MTU.getKey(), limitJson);
        modulesLimitsJson.put(AUTOMATION_WEBHOOK_UUID, mtuObject);
        JSONObject usersWithRoleObject = new JSONObject();
        usersWithRoleObject.put(LicenseParameter.USERS_WITH_ROLE.getKey(), limitJson);
        modulesLimitsJson.put(CORE_UUID, usersWithRoleObject);
        JSONObject automationModuleObject = (JSONObject) modulesLimitsJson.get(AUTOMATION_UUID);
        automationModuleObject.put(LicenseParameter.SCRIPT_EXECUTION_TIME.getKey(), limitJson);
        automationModuleObject.put(CLICKHOUSE_SQL_EXECUTION_TIMEOUT_KEY_V3, limitJson);
        return OUT_SCHEME_VERSION;
    }

}
