package com.fuzzy.subsystem.frontend.remote.session;

import com.fuzzy.cluster.core.remote.struct.RemoteObject;
import com.fuzzy.subsystem.frontend.service.session.Session;

import java.time.Instant;

public abstract class SessionRemoteAdapter implements RemoteObject {

    private final String uuid;

    private final Instant accessTime;

    public SessionRemoteAdapter(Session session) {
        uuid = session.uuid;
        accessTime = session.getAccessTime();
    }

    public String getUUID() {
        return uuid;
    }

    public Instant getAccessTime() {
        return accessTime;
    }
}
