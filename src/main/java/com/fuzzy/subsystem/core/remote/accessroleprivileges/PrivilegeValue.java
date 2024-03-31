package com.fuzzy.subsystem.core.remote.accessroleprivileges;

import com.fuzzy.cluster.core.remote.struct.RemoteObject;
import com.fuzzy.subsystems.access.AccessOperationCollection;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Objects;

public class PrivilegeValue implements RemoteObject {

    private final String key;
    private final AccessOperationCollection operations;

    public PrivilegeValue(@NonNull String key, @NonNull AccessOperationCollection operations) {
        this.key = key;
        this.operations = operations;
    }

    public @NonNull String getKey() {
        return key;
    }

    public @NonNull AccessOperationCollection getOperations() {
        return operations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PrivilegeValue that = (PrivilegeValue) o;
        return key.equals(that.key) && operations.equals(that.operations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, operations);
    }
}
