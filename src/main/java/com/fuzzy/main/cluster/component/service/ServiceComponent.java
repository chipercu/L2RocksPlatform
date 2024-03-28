package com.fuzzy.main.cluster.component.service;

import com.fuzzy.main.cluster.anotation.Info;
import com.fuzzy.main.cluster.component.service.internal.service.ClusterInputStreamService;
import com.fuzzy.main.cluster.struct.Component;

@Info(uuid = "com.fuzzy.main.cluster.component.service")
public class ServiceComponent extends Component {

    public final ClusterInputStreamService clusterInputStreamService;

    public ServiceComponent() {

        this.clusterInputStreamService = new ClusterInputStreamService();
    }

}
