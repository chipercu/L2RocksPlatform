package com.fuzzy.subsystem.core.remote.employee;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.Serializable;

public record EmployeeAccountMetaData(
        @NonNull Long id,
        @NonNull String login,
        @NonNull String domain) implements Serializable {
}
