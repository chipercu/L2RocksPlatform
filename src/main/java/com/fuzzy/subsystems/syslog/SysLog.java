package com.fuzzy.subsystems.syslog;

import org.checkerframework.checker.nullness.qual.NonNull;

public class SysLog {

    public static String buildMessage(@NonNull Header header, @NonNull StructuredData structuredData) {
        return header.getData() + '~' + structuredData.getData();
    }
}
