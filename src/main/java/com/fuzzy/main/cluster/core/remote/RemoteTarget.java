package com.fuzzy.main.cluster.core.remote;

import java.util.UUID;

public record RemoteTarget(UUID nodeRuntimeId, int componentId, String componentUuid) {
}
