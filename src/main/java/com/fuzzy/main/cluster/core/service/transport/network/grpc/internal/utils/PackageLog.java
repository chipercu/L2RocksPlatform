package com.fuzzy.main.cluster.core.service.transport.network.grpc.internal.utils;


import com.infomaximum.cluster.core.service.transport.network.grpc.struct.*;

public class PackageLog {

    public static String toString(PNetPackage value) {
        if (value.hasRequest()) {
            return toString(value.getRequest());
        } else if (value.hasResponse()) {
            return toString(value.getResponse());
        } else if (value.hasResponseProcessing()) {
            return toString(value.getResponseProcessing());
        } else if (value.hasUpdateNode()) {
            return toString(value.getUpdateNode());
        } else if (value.hasHandshakeRequest()) {
            return toString(value.getHandshakeRequest());
        } else if (value.hasHandshakeResponse()) {
            return toString(value.getHandshakeResponse());
        } else {
            throw new RuntimeException("Unknown package: " + value);
        }
    }

    public static String toString(PNetPackageRequest value) {
        return "PackageRequest{" +
                "id=" + value.getPackageId() +
                '}';
    }

    public static String toString(PNetPackageResponse value) {
        return "PackageResponse{" +
                "id=" + value.getPackageId() +
                '}';
    }

    public static String toString(PNetPackageProcessing value) {
        return "PackageResponseProcessing{" +
                "id=" + value.getPackageId() +
                '}';
    }

    public static String toString(PNetPackageUpdateNode value) {
        return "PackageUpdateNode{}";
    }

    public static String toString(PNetPackageHandshakeRequest value) {
        return "PackageHandshakeRequest{}";
    }

    public static String toString(PNetPackageHandshakeResponse value) {
        return "PackageHandshakeResponse{}";
    }
}
