package com.fuzzy.subsystems.security.struct.data.target;

import com.fuzzy.subsystems.security.struct.data.SyslogStructData;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class SyslogStructDataTarget implements SyslogStructData {

    private final SyslogTargetType type;
    private final Serializable id;
    private Map<String, String> params;

    public SyslogStructDataTarget(SyslogTargetType type) {
        this(type,null);
    }

    public SyslogStructDataTarget(SyslogTargetType type, Serializable id) {
        this.type=type;
        this.id=id;
    }

    public SyslogStructDataTarget withParam(String key, String value) {
        if (params==null) params = new HashMap<>();
        params.put(key, value);
        return this;
    }

    public SyslogTargetType getType() {
        return type;
    }

    @Override
    public String getName() {
        return "target";
    }

    @Override
    public Map<String, String> getData() {
        Map<String, String> data = new HashMap<>();
        data.put("type", type.getType());
        data.put("module", type.getSubsystemInfo().isPlatform() ? "platform" : type.getSubsystemInfo().getName());
        if (id != null) data.put("id", String.valueOf(id));
        if (params != null) data.putAll (params);
        return data;
    }
}
