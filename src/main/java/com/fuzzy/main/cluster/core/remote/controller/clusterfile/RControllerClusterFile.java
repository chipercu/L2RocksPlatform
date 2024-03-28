package com.fuzzy.main.cluster.core.remote.controller.clusterfile;

import com.fuzzy.main.cluster.core.remote.struct.ClusterInputStream;
import com.fuzzy.main.cluster.core.remote.struct.RController;

/**
 * Created by kris on 02.11.16.
 */
public interface RControllerClusterFile extends RController {

    long getSize(String clusterFileUUID) throws Exception;

    ClusterInputStream getInputStream(String clusterFileUUID) throws Exception;

    void delete(String clusterFileUUID) throws Exception;

    void deleteIfExists(String clusterFileUUID) throws Exception;
}


