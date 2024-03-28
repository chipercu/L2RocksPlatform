package com.fuzzy.subsystems.utils;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.time.LocalDateTime;

public class MinMaxLocalDateTime {

    private LocalDateTime min = null;
    private LocalDateTime max = null;

    public void add(@NonNull LocalDateTime object) {
        if (min == null || object.compareTo(min) < 0) {
            min = object;
        }
        if (max == null || object.compareTo(max) > 0) {
            max = object;
        }
    }

    public boolean isEmpty() {
        return min == null;
    }

    public LocalDateTime getMin() {
        return min;
    }

    public LocalDateTime getMax() {
        return max;
    }
}
