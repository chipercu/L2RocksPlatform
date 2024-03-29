package com.fuzzy.subsystem.frontend.struct.request;

import com.fuzzy.main.platform.component.frontend.request.GRequestWebSocket;

import java.io.Serializable;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

//todo V.Bukharkin, V.Ulitin подумать, как реализовать по-другому
public class RequestWebSocket extends GRequestWebSocket {

    public RequestWebSocket(Instant instant,  RemoteAddress remoteAddress, String sessionUuid, Map<String, String> parameters) {
        super(instant, remoteAddress, null, null, null, null, sessionUuid, parameters, null);
    }

    @Override
    public String getQuery() {
        throw new UnsupportedOperationException();
    }

    @Override
    public HashMap<String, Serializable> getQueryVariables() {
        throw new UnsupportedOperationException();
    }
}
