package com.fuzzy.subsystems.access;

import org.checkerframework.checker.nullness.qual.NonNull;

public class Privilege {

    private int id;
    private String uniqueKey;
    private AccessOperationCollection availableOperations;

    public Privilege(int id, @NonNull String uniqueKey, AccessOperation... availableOperations) {
        this.id = id;
        this.uniqueKey = uniqueKey;
        this.availableOperations = new AccessOperationCollection(availableOperations);
    }

    public int intValue() {
        return id;
    }

    public String getUniqueKey() {
        return uniqueKey;
    }

    public AccessOperationCollection getAvailableOperations() {
        return new AccessOperationCollection(availableOperations.getValue());
    }
}
