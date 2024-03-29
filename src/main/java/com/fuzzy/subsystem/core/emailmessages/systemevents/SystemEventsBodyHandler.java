package com.fuzzy.subsystem.core.emailmessages.systemevents;

import com.fuzzy.subsystem.core.service.systemevent.SystemEvent;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SystemEventsBodyHandler {

    public static final Pattern foreachPattern = Pattern.compile("\\{\\{foreach\\}\\}(?<event>(?s).*)\\{\\{/foreach\\}\\}");

    private final ArrayList<SystemEvent> events;
    private final SystemEventsItemHandler systemEventsItemHandler;

    public SystemEventsBodyHandler(ArrayList<SystemEvent> events) {
        this.events = events;
        final boolean isNumerable = events.size() > 1;
        this.systemEventsItemHandler = new SystemEventsItemHandler(isNumerable);
    }

    public String fillBody(String bodyTemplate) {
        final String systemEventTemplate = extractSystemEventTemplate(bodyTemplate);
        return fillTemplate(bodyTemplate, fillItems(systemEventTemplate));
    }

    private String fillTemplate(String bodyTemplate, String fillItems) {
        Matcher matcher = foreachPattern.matcher(bodyTemplate);
        return matcher.find() ? matcher.replaceFirst(Matcher.quoteReplacement(fillItems)) : bodyTemplate;
    }

    private String fillItems(String systemEventTemplate) {
        StringBuilder fillItems = new StringBuilder();
        if (!events.isEmpty() && StringUtils.isNotBlank(systemEventTemplate)) {
            for (int i = 0; i < events.size(); i++) {
                fillItems.append(systemEventsItemHandler.fillItem(systemEventTemplate, events.get(i), i));
            }
        }
        return fillItems.toString();
    }

    private String extractSystemEventTemplate(String bodyTemplate) {
        Matcher matcher = foreachPattern.matcher(bodyTemplate);
        return matcher.find() ? matcher.group("event") : StringUtils.EMPTY;
    }
}