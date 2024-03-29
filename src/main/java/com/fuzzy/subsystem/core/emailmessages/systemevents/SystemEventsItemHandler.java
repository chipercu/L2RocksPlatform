package com.fuzzy.subsystem.core.emailmessages.systemevents;

import com.fuzzy.subsystem.core.service.systemevent.SystemEvent;
import org.apache.commons.lang3.StringUtils;

import java.time.format.DateTimeFormatter;

public class SystemEventsItemHandler {

    private static final int STRING_LENGTH = 100_000;
    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss XXX");

    private static final String[] vars = {
            "{{num}}",
            "{{type}}",
            "{{level}}",
            "{{message}}",
            "{{subsystem_uuid}}",
            "{{time}}",
    };
    private final boolean isNumerable;

    public SystemEventsItemHandler(boolean isNumerable) {
        this.isNumerable = isNumerable;
    }

    public String fillItem(String itemTemplate, SystemEvent event, int num) {
        return StringUtils.replaceEach(itemTemplate, vars, createValues(event, num));
    }

    private String[] createValues(SystemEvent event, int num) {
        return new String[]{
                createNumValue(num),
                createTypeValue(event),
                createLevelValue(event),
                createMessageValue(event),
                createSubsystemUuidValue(event),
                createTimeValue(event),
        };
    }

    public String createNumValue(int num) {
        return isNumerable
                ? String.valueOf(num + 1)
                : StringUtils.EMPTY;
    }

    public String createTypeValue(SystemEvent event) {
        return StringUtils.defaultString(event.getEventType());
    }

    public String createLevelValue(SystemEvent event) {
        return StringUtils.defaultString(StringUtils.lowerCase(event.getLevel().toString()));
    }

    public String createMessageValue(SystemEvent event) {
        return StringUtils.defaultString(StringUtils.substring(event.getMessage(), 0, STRING_LENGTH));
    }

    public String createSubsystemUuidValue(SystemEvent event) {
        return StringUtils.defaultString(event.getSubsystemUuid());
    }

    public String createTimeValue(SystemEvent event) {
        return timeFormatter.format(event.getTime());
    }
}