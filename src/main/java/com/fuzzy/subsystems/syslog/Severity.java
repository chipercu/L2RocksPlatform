package com.fuzzy.subsystems.syslog;

public enum Severity {

    EMERGENCY(0),
    ALERT(1),
    CRITICAL(2),
    ERROR(3),
    WARNING(4),
    NOTICE(5),
    INFORMATIONAL(6),
    DEBUG(7);

    private final int id;

    Severity(int id) {
        this.id = id;
    }


    public int getId() {
        return id;
    }
}
