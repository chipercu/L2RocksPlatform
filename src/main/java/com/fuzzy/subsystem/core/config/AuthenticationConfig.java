package com.fuzzy.subsystem.core.config;

import com.fuzzy.cluster.core.remote.struct.RemoteObject;

import java.time.Duration;

public class AuthenticationConfig implements RemoteObject {

    private final ComplexPassword complexPassword;
    private final Duration passwordExpirationTime;
    private final Integer maxInvalidLogonCount;

    public AuthenticationConfig(
            ComplexPassword complexPassword,
            Duration passwordExpirationTime,
            Integer maxInvalidLogonCount
    ) {
        this.complexPassword = complexPassword;
        this.passwordExpirationTime = passwordExpirationTime;
        this.maxInvalidLogonCount = maxInvalidLogonCount;
    }

    public ComplexPassword getComplexPassword() {
        return complexPassword;
    }

    public Duration getPasswordExpirationTime() {
        return passwordExpirationTime;
    }

    public Integer getMaxInvalidLogonCount() {
        return maxInvalidLogonCount;
    }
}