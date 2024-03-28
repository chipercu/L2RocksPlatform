package com.fuzzy.main.platform.component.frontend.utils;


import com.fuzzy.main.cluster.graphql.struct.GRequest;

public class GRequestUtils {

    public static String getTraceRequest(GRequest gRequest) {
        return "r" + gRequest.hashCode();
    }
}
