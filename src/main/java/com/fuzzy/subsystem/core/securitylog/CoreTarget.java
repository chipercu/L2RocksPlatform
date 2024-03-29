package com.fuzzy.subsystem.core.securitylog;

import com.fuzzy.subsystem.core.CoreSubsystem;
import com.fuzzy.subsystems.security.struct.data.target.SyslogTargetType;

public class CoreTarget {

    public static final SyslogTargetType TYPE_SYSTEM =
            new SyslogTargetType("system", CoreSubsystem.INFO);
    public static final SyslogTargetType TYPE_EMPLOYEE =
            new SyslogTargetType("employee", CoreSubsystem.INFO);
    public static final SyslogTargetType TYPE_SETTINGS =
            new SyslogTargetType("settings", CoreSubsystem.INFO);
    public static final SyslogTargetType TYPE_ACCESS_ROLE =
            new SyslogTargetType("access_role", CoreSubsystem.INFO);
    public static final SyslogTargetType TYPE_API_KEY =
            new SyslogTargetType("api_key", CoreSubsystem.INFO);
    public static final SyslogTargetType TYPE_DATABASE =
            new SyslogTargetType("database", CoreSubsystem.INFO);
    public static final SyslogTargetType TYPE_DEPARTMENT =
            new SyslogTargetType("department", CoreSubsystem.INFO);
    public static final SyslogTargetType TYPE_AUTHENTICATION =
            new SyslogTargetType("authentication", CoreSubsystem.INFO);
    public static final SyslogTargetType TYPE_LICENSE =
            new SyslogTargetType("license", CoreSubsystem.INFO);
}
