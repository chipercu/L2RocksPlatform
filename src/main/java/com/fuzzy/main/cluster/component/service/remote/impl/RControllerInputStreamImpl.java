package com.fuzzy.main.cluster.component.service.remote.impl;

import com.fuzzy.main.cluster.component.service.ServiceComponent;
import com.fuzzy.main.cluster.component.service.internal.service.ClusterInputStreamService;
import com.fuzzy.main.cluster.component.service.remote.RControllerInputStream;
import com.fuzzy.main.cluster.core.remote.AbstractRController;

public class RControllerInputStreamImpl extends AbstractRController<ServiceComponent> implements RControllerInputStream {

    private final ClusterInputStreamService clusterInputStreamService;

    public RControllerInputStreamImpl(ServiceComponent component) {
        super(component);
        this.clusterInputStreamService = component.clusterInputStreamService;
    }

    @Override
    public byte[] next(int id, int limit) {
        return clusterInputStreamService.read(id, limit);
    }
}
