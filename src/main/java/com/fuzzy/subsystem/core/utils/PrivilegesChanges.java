package com.fuzzy.subsystem.core.utils;

import com.fuzzy.subsystems.access.AccessOperationCollection;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PrivilegesChanges {

    public static void mergeChangesPrivileges(@NonNull Map<String, AccessOperationCollection> prevPrivileges,
                                              @NonNull Map<String, AccessOperationCollection> currentPrivileges,
                                              @NonNull PrivilegeConsumer privilegeBiConsumer) {
        Set<String> usedKeys = new HashSet<>();
        usedKeys.addAll(prevPrivileges.keySet());
        usedKeys.addAll(currentPrivileges.keySet());
        for (String key : usedKeys) {
            if (currentPrivileges.containsKey(key) && prevPrivileges.containsKey(key)) {
                AccessOperationCollection prevAccessOperationCollection = prevPrivileges.get(key);
                AccessOperationCollection currentAccessOperationCollection = currentPrivileges.get(key);
                if (!currentAccessOperationCollection.equals(prevAccessOperationCollection)) {
                    privilegeBiConsumer.accept(key, prevAccessOperationCollection, currentAccessOperationCollection);
                }
            } else if (currentPrivileges.containsKey(key)) {
                privilegeBiConsumer.accept(key, new AccessOperationCollection(), currentPrivileges.get(key));
            } else if (prevPrivileges.containsKey(key)) {
                privilegeBiConsumer.accept(key, prevPrivileges.get(key), new AccessOperationCollection());
            }
        }
    }

    @FunctionalInterface
    public interface PrivilegeConsumer {
        void accept(String key, AccessOperationCollection prevPrivileges, AccessOperationCollection curPrivileges);
    }
}
