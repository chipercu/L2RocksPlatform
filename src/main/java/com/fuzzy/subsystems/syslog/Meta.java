package com.fuzzy.subsystems.syslog;

public class Meta extends SdElement {

    private Meta() {
        super("meta");
    }

    public static Meta ofSequenceId(int sequenceId) {
        if (sequenceId < 1) {
            throw new IllegalArgumentException("sequenceId must be 1.." + Integer.MAX_VALUE);
        }
        Meta meta = new Meta();
        meta.addParam("sequenceId", String.valueOf(sequenceId));
        return meta;
    }
}
