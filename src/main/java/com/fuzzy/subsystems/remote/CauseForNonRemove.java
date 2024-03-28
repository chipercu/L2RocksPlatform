package com.fuzzy.subsystems.remote;

import java.io.Serializable;
import java.util.HashSet;

public class CauseForNonRemove implements Serializable {

    private final String cause;
    private final HashSet<Long> nonRemoved;

    public CauseForNonRemove(String cause, HashSet<Long> nonRemoved) {
        this.cause = cause;
        this.nonRemoved = nonRemoved;
    }

    public String getCause() {
        return cause;
    }

    public HashSet<Long> getNonRemoved() {
        return nonRemoved;
    }
}
