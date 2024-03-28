package com.fuzzy.subsystems.utils;

import org.checkerframework.checker.nullness.qual.NonNull;

public class BytesConverter {

    public static @NonNull Long megaBytesToBytes(@NonNull Long valueMegaByte) {
        return valueMegaByte << 20;
    }

    public static @NonNull Long bytesToMegaBytes(@NonNull Long valueBytes) {
        return valueBytes >> 20;
    }
}
