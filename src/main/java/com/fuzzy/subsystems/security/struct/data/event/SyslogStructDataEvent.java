package com.fuzzy.subsystems.security.struct.data.event;

import com.fuzzy.subsystems.security.struct.data.SyslogStructData;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashMap;
import java.util.Map;

public class SyslogStructDataEvent implements SyslogStructData {

    private final String type;
    private Map<String, String> params;

    public SyslogStructDataEvent(@NonNull String type) {
        this.type = type;
    }

    public SyslogStructDataEvent withParam(String key, String value) {
        if (params == null) params = new HashMap<>();
        params.put(key, value);
        return this;
    }

    @Override
    public String getName() {
        return "event";
    }

    @Override
    public Map<String, String> getData() {
        return params != null ? params : new HashMap<>();
    }

    public @NonNull String getType() {
        return type;
    }
}
