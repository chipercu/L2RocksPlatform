package com.fuzzy.cluster.core.service.transport.network;

import com.fuzzy.cluster.core.component.RuntimeComponentInfo;

import java.util.UUID;

public record LocationRuntimeComponent(UUID node, RuntimeComponentInfo component) {
}
