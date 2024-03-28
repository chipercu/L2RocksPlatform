package com.fuzzy.subsystems.syslog;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class Header extends SysLogItem {

    private static final String VERSION = "1";
    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXXXX");

    private StringBuilder data = null;

    public Header(
            int facility,
            @NonNull Severity severity,
            @Nullable ZonedDateTime timestamp,
            @Nullable String hostName,
            @Nullable String appName,
            @Nullable String procId,
            @Nullable String msgId
    ) {
        this.data = new StringBuilder();
        if (facility < 0 || facility > 23) {
            throw new IllegalArgumentException("Facility must be 0..23");
        }
        data.append('<').append(facility * 8 + severity.getId()).append('>').append(VERSION).append('~');
        data.append(timestamp != null ? timestamp.format(TIMESTAMP_FORMAT) : NIL_VALUE).append('~');
        data.append(validateAndGet(hostName, 255)).append('~');
        data.append(validateAndGet(appName, 48)).append('~');
        data.append(validateAndGet(procId, 128)).append('~');
        data.append(validateAndGet(msgId, 32));
    }

    public String getData() {
        return data.toString();
    }
}
