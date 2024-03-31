package com.fuzzy.cluster.core.service.transport.network.grpc.internal.service.remotecontroller;

import java.util.UUID;

public record WaitLocalExecute(UUID nodeRuntimeId, int packageId) {

}
