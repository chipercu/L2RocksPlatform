package com.fuzzy.main.rdao.database.utils;

import com.fuzzy.main.rdao.database.exception.DatabaseException;

import java.nio.file.Path;

public class PathUtils {

    public static void checkPath(Path path) throws DatabaseException {
        if (!path.isAbsolute()) {
            throw new DatabaseException("RocksDB-paths is not absolute.");
        }
    }
}
