package com.fuzzy.subsystems.remote;

import org.checkerframework.checker.nullness.qual.NonNull;

public interface Identifiable<T> {
    @NonNull T getIdentifier();
}
