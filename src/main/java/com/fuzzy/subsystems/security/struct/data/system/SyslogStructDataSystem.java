package com.fuzzy.subsystems.security.struct.data.system;

import com.fuzzy.main.Subsystems;
import com.fuzzy.subsystems.security.struct.data.SyslogStructData;

import java.util.HashMap;
import java.util.Map;

public class SyslogStructDataSystem implements SyslogStructData {

    @Override
    public String getName() {
        return "system";
    }

    @Override
    public Map<String, String> getData() {
        Map<String, String> data = new HashMap<>();
        data.put(buildModuleVersionName("platform"), Subsystems.VERSION.toString());
        return data;
    }

    private static String buildModuleVersionName(String moduleName) {
        return "version." + moduleName;
    }
}
