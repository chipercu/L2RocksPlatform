package com.fuzzy.main.cluster.core.service.transport.network;

import com.fuzzy.main.cluster.core.component.RuntimeComponentInfo;

import java.util.UUID;

public record LocationRuntimeComponent(UUID node, RuntimeComponentInfo component) {
}
