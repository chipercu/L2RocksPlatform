package com.fuzzy.cluster.component.service;

import com.fuzzy.cluster.anotation.Info;
import com.fuzzy.cluster.component.service.internal.service.ClusterInputStreamService;
import com.fuzzy.cluster.struct.Component;

@Info(uuid = "com.fuzzy.cluster.component.service")
public class ServiceComponent extends Component {

    public final ClusterInputStreamService clusterInputStreamService;

    public ServiceComponent() {

        this.clusterInputStreamService = new ClusterInputStreamService();
    }

}
