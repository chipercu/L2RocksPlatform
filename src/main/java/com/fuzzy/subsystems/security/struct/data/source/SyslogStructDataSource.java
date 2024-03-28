package com.fuzzy.subsystems.security.struct.data.source;

import com.fuzzy.subsystems.security.struct.data.SyslogStructData;

import java.util.HashMap;
import java.util.Map;

public class SyslogStructDataSource implements SyslogStructData {

    private HashMap<String, String> data = new HashMap<>();

    public SyslogStructDataSource(String type) {
        addParam("type", type);
    }

    public SyslogStructDataSource addParam(String name, String value) {
        data.put(name, value);
        return this;
    }

    @Override
    public final String getName() {
        return "source";
    }

    @Override
    public Map<String, String> getData() {
        return data;
    }
}
