package com.fuzzy.subsystems.access;

import com.infomaximum.cluster.core.remote.struct.RemoteObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AccessOperationCollection implements RemoteObject {

    public static final AccessOperationCollection EMPTY = new AccessOperationCollection(0);

    private int value;

    public AccessOperationCollection(int operations) {
        this.value = operations;
    }

    public AccessOperationCollection(Integer operations) {
        this.value = operations != null ? operations : 0;
    }

    public AccessOperationCollection() {
        this(0);
    }

    public AccessOperationCollection(AccessOperation... operations) {
        setOperation(operations);
    }

    public AccessOperationCollection(List<AccessOperation> operations) {
        setOperation(operations.toArray(new AccessOperation[0]));
    }

    public int getValue() {
        return value;
    }

    public boolean isEmpty() {
        return value == 0;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public boolean contains(AccessOperation operation) {
        return (value & operation.intValue()) != 0;
    }

    public boolean contains(Collection<AccessOperation> operations) {
        for (AccessOperation operation : operations) {
            if (!contains(operation)) {
                return false;
            }
        }
        return true;
    }

    public boolean contains(AccessOperation... operations) {
        for (AccessOperation operation : operations) {
            if (!contains(operation)) {
                return false;
            }
        }
        return true;
    }

    public boolean contains(AccessOperationCollection operations) {
        return contains(operations.getOperations());
    }

    public AccessOperation[] getOperations() {
        List<AccessOperation> operations = new ArrayList<>();
        for (AccessOperation operation : AccessOperation.values()) {
            if (contains(operation)) {
                operations.add(operation);
            }
        }
        return operations.toArray(new AccessOperation[0]);
    }

    public void setOperation(AccessOperation... operations) {
        value = 0;
        addOperation(operations);
    }

    public void addOperation(AccessOperation... operations) {
        for (AccessOperation operation : operations) {
            value |= operation.intValue();
        }
    }

    public void removeOperation(AccessOperation... operations) {
        int intOperations = 0;
        for (AccessOperation operation : operations) {
            intOperations |= operation.intValue();
        }
        value &= ~intOperations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AccessOperationCollection that = (AccessOperationCollection) o;

        return value == that.value;
    }
}
