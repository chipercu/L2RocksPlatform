package com.fuzzy.cluster.core.service.transport.network.local.event;

import com.fuzzy.cluster.core.component.RuntimeComponentInfo;

public interface EventUpdateLocalComponent {

    void registerComponent(RuntimeComponentInfo subSystemInfo);

    void unRegisterComponent(RuntimeComponentInfo subSystemInfo);
}
