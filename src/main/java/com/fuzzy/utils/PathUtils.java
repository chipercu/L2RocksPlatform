package com.fuzzy.utils;

import org.apache.commons.lang3.SystemUtils;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PathUtils {

    public static Path getSafely(String path) {
        try {
            return Paths.get(path);
        } catch (InvalidPathException e) {
            if (!SystemUtils.IS_OS_WINDOWS) {
                throw e;
            }

            StringBuilder builder = new StringBuilder(path);
            for (int i = 0; i < builder.length(); ++i) {
                if (isInvalidPathChar(builder.charAt(i))) {
                    builder.setCharAt(i, '_');
                }
            }
            return Paths.get(builder.toString());
        }
    }

    /**
     * sun.nio.fs.WindowsPathParser#isInvalidPathChar
     */
    private static boolean isInvalidPathChar(char var0) {
        return var0 < ' ' || "<>:\"|?*".indexOf(var0) != -1;
    }
}
