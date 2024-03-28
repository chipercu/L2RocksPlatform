package com.fuzzy.main.cluster.core.service.transport.network.grpc.internal.service.remotecontroller;

import java.time.Instant;

public class WaitLocalExecuteResult {

    private Instant endTime;

    public WaitLocalExecuteResult() {
    }

    public Instant getEndTime() {
        return endTime;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }
}
