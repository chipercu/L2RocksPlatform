package com.fuzzy.main.platform.component.frontend.engine.filter;

import com.fuzzy.main.cluster.graphql.struct.GRequest;
import com.fuzzy.main.platform.exception.PlatformException;

public interface FilterGRequest {

    void filter(GRequest request) throws PlatformException;

}
