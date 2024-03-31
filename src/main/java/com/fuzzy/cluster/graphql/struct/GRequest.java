package com.fuzzy.cluster.graphql.struct;

import com.fuzzy.cluster.core.remote.struct.RemoteObject;

import java.io.Serializable;
import java.time.Instant;
import java.util.HashMap;

/**
 * Created by kris on 23.01.17.
 */
public class GRequest implements RemoteObject {

    private final Instant instant;
    private final RemoteAddress remoteAddress;
    private final String query;
    private final HashMap<String, Serializable> queryVariables;

    private final String operationName;

    private final String xTraceId;

    public GRequest(
            Instant instant,
            RemoteAddress remoteAddress,
            String query, HashMap<String, Serializable> queryVariables, String operationName,
            String xTraceId
    ) {
        this.instant = instant;

        this.remoteAddress = remoteAddress;

        this.query = query;
        this.queryVariables = queryVariables;
        this.operationName = operationName;

        this.xTraceId = xTraceId;
    }

    public Instant getInstant() {
        return instant;
    }

    public RemoteAddress getRemoteAddress() {
        return remoteAddress;
    }

    public String getQuery() {
        return query;
    }

    public HashMap<String, Serializable> getQueryVariables() {
        return queryVariables;
    }

    public String getOperationName() {
        return operationName;
    }

    public String getXTraceId() {
        return xTraceId;
    }

    public static class RemoteAddress implements RemoteObject {

        public final String rawRemoteAddress;
        public final String endRemoteAddress;

        public RemoteAddress(String remoteAddress) {
            this.rawRemoteAddress = remoteAddress;
            this.endRemoteAddress = remoteAddress;
        }

        public RemoteAddress(String rawRemoteAddress, String endRemoteAddress) {
            this.rawRemoteAddress = rawRemoteAddress;
            this.endRemoteAddress = endRemoteAddress;
        }
    }

}
