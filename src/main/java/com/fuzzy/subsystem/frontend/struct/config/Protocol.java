package com.fuzzy.subsystem.frontend.struct.config;

public enum Protocol {

    HTTP("http"),
    HTTPS("https"),
    HTTP3("http3");

    private final String value;

    Protocol(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static Protocol of(String value) throws IllegalArgumentException {
        for (Protocol protocol : Protocol.values()) {
            if (protocol.value.equals(value)){
                return protocol;
            }
        }
        throw new IllegalArgumentException("Illegal protocol value, " + value);
    }
}
