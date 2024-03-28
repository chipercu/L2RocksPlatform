package com.fuzzy.subsystems.security.struct.data.target;

import com.fuzzy.subsystems.subsystem.Info;

public class SyslogTargetType {

    private final String type;
    private final Info subsystemInfo;

    public SyslogTargetType(String type, Info subsystemInfo) {
        this.type = type;
        this.subsystemInfo = subsystemInfo;
    }

    public String getType() {
        return type;
    }

    public Info getSubsystemInfo() {
        return subsystemInfo;
    }
}
