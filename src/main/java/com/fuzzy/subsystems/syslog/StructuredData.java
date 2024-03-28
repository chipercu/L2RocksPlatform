package com.fuzzy.subsystems.syslog;

import org.checkerframework.checker.nullness.qual.NonNull;

public class StructuredData extends SysLogItem {

    private StringBuilder data = null;

    public StructuredData addElement(@NonNull SdElement element) {
        if (data == null) {
            data = new StringBuilder();
        } else {
            data.append('~');
        }
        data.append(element.getData());
        return this;
    }

    public boolean isEmpty() {
        return data == null;
    }

    public String getData() {
        return data != null ? data.toString() : NIL_VALUE;
    }
}
