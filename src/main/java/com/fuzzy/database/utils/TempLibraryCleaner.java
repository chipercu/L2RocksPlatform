package com.fuzzy.database.utils;

import org.rocksdb.util.Environment;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TempLibraryCleaner {

    public final static String TEMP_FILE_PREFIX = "librocksdbjni";
    public final static String TEMP_FILE_SUFFIX = Environment.getJniLibraryExtension();

    static String getFilesMask() {
        return TEMP_FILE_PREFIX + "*" + TEMP_FILE_SUFFIX;
    }

    static Path getTempDir() {
        return Paths.get(System.getProperty("java.io.tmpdir"));
    }

    public static void clear() {
        if (Environment.isWindows()) {
            clearForce();
        }
    }

    static void clearForce() {
        if (!Environment.isWindows()) {
            return;
        }

        try (DirectoryStream<Path> files = Files.newDirectoryStream(getTempDir(), getFilesMask())) {
            for (Path file : files) {
                try {
                    Files.delete(file);
                } catch (Throwable ignore) {
                }
            }
        } catch (IOException ignore) {
        }
    }
}
