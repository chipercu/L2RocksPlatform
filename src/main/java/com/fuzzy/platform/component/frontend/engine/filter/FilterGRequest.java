package com.fuzzy.platform.component.frontend.engine.filter;

import com.fuzzy.cluster.graphql.struct.GRequest;
import com.fuzzy.platform.exception.PlatformException;

public interface FilterGRequest {

    void filter(GRequest request) throws PlatformException;

}
