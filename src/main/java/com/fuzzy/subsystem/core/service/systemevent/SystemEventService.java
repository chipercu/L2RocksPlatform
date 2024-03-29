package com.fuzzy.subsystem.core.service.systemevent;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class SystemEventService {

    private final ConcurrentHashMap<String, SystemEvent> events = new ConcurrentHashMap<>();

    public void captureEvent(SystemEvent systemEvent) {
        events.put(systemEvent.getEventType(), systemEvent);
    }

    public ArrayList<SystemEvent> getEvents() {
        return new ArrayList<>(events.values());
    }


    public ArrayList<SystemEvent> getActualEvents(ZonedDateTime now) {
        return getEvents().stream()
                .filter(event -> checkTtl(event, now))
                .collect(Collectors.toCollection(ArrayList::new));
    }


    public ArrayList<SystemEvent> getActualSortedEvents(ZonedDateTime now, Set<SystemEvent.EventLevel> levelFilter) {
        Comparator<SystemEvent> eventComparator =
                Comparator.comparing((SystemEvent event) -> event.getLevel().getLevel(), Integer::compare)
                        .thenComparing(Comparator.comparing(SystemEvent::getTime).reversed());

        return getActualEvents(now).stream()
                .filter(event -> Objects.isNull(levelFilter) || levelFilter.isEmpty() || levelFilter.contains(event.getLevel()))
                .sorted(eventComparator)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private boolean checkTtl(SystemEvent event, ZonedDateTime now) {
        return event.getTime().plus(event.getTtl().toMillis(), ChronoUnit.MILLIS).isAfter(now);
    }

}