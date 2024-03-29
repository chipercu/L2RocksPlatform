package com.fuzzy.subsystem.frontend.service.session;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

public class SessionTimeoutQueue {

    private final Map<String, SessionTimeout> storage = new HashMap<>();
    private final TreeSet<SessionTimeout> queue = new TreeSet<>(
            Comparator.comparing(SessionTimeout::getTimeout).thenComparing(SessionTimeout::getUuid));

    public SessionTimeout peek() {
        if (queue.isEmpty()) {
            return null;
        }
        return queue.first();
    }

    public void updateOrAdd(SessionTimeout st) {
        remove(st.getUuid());
        add(st);
    }

    public void remove(String uuid) {
        SessionTimeout prevSt = storage.remove(uuid);
        if (prevSt != null) {
            queue.remove(prevSt);
        }
    }

    public void clear() {
        storage.clear();
        queue.clear();
    }

    public int size() {
        return queue.size();
    }

    private void add(SessionTimeout st) {
        storage.put(st.getUuid(), st);
        queue.add(st);
    }
}
