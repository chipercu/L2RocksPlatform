package com.fuzzy.subsystem.core.config;

import com.fuzzy.cluster.core.remote.struct.RemoteObject;

public class ComplexPassword implements RemoteObject {

    private final int minPasswordLength;

    public ComplexPassword(int minPasswordLength) {
        this.minPasswordLength = minPasswordLength;
    }

    public int getMinPasswordLength() {
        return minPasswordLength;
    }
}