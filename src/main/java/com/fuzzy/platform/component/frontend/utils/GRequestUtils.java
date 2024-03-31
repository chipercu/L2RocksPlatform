package com.fuzzy.platform.component.frontend.utils;

import com.fuzzy.cluster.graphql.struct.GRequest;

public class GRequestUtils {

    public static String getTraceRequest(GRequest gRequest) {
        return "r" + gRequest.hashCode();
    }
}
