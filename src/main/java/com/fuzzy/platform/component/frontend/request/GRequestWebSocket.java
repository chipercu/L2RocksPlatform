package com.fuzzy.platform.component.frontend.request;

import com.fuzzy.cluster.graphql.struct.GRequest;
import com.fuzzy.network.struct.HandshakeData;
import jakarta.servlet.http.Cookie;

import java.io.Serializable;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class GRequestWebSocket extends GRequest {

    private final String sessionUuid;

    private final Map<String, String> parameters;
    private final Cookie[] cookies;

    private final HandshakeData handshakeData;

    public GRequestWebSocket(Instant instant, RemoteAddress remoteAddress, String query, HashMap<String, Serializable> queryVariables, String operationName, String xTraceId, String sessionUuid, Map<String, String> parameters, Cookie[] cookies) {
        this(instant, remoteAddress, query, queryVariables, operationName, xTraceId, sessionUuid, parameters, cookies, null);
    }

    public GRequestWebSocket(Instant instant, RemoteAddress remoteAddress, String query, HashMap<String, Serializable> queryVariables, String operationName, String xTraceId, String sessionUuid, Map<String, String> parameters, Cookie[] cookies, HandshakeData handshakeData) {
        super(instant, remoteAddress, query, queryVariables, operationName, xTraceId);

        this.sessionUuid = sessionUuid;
        this.parameters = parameters != null ? parameters : new HashMap<>();
        this.cookies = cookies;

        this.handshakeData = handshakeData;
    }

    public String getSessionUuid() {
        return sessionUuid;
    }

    public Map<String, String> getParameters() {
        return Collections.unmodifiableMap(parameters);
    }

    public String getParameter(String name) {
        return parameters.get(name);
    }

    public Cookie getCookie(String name) {
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (name.equals(cookie.getName())) return cookie;
            }
        }
        return null;
    }

    public <T extends HandshakeData> T getHandshakeData() {
        return (T) handshakeData;
    }
}
