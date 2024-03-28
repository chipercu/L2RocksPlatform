package com.fuzzy.utils;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Duration;
import java.util.Collection;
import java.util.stream.Stream;

public final class FileUtils {

    private FileUtils() {}

    public static boolean deleteDirectoryIfExists(Path dir) throws IOException {
        if (!Files.exists(dir) || !Files.isDirectory(dir)) {
            return false;
        }

        IOException[] exception = new IOException[]{null};
        Files.walkFileTree(dir, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                try {
                    Files.delete(file);
                } catch (IOException e) {
                    if (exception[0] == null) exception[0] = e;
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
                if (exc != null && exception[0] == null) {
                    exception[0] = exc;
                }

                try {
                    Files.delete(dir);
                } catch (IOException e) {
                    if (exception[0] == null) exception[0] = e;
                }
                return FileVisitResult.CONTINUE;
            }
        });
        if (exception[0] != null) {
            throw exception[0];
        }
        return true;
    }

    public static boolean deleteDirectoryIfExists(Path dir, int attempts, Duration attemptTimeout) throws IOException {
        int attemptCount = 0;
        while (true) {
            try {
                return deleteDirectoryIfExists(dir);
            } catch (IOException e) {
                if ((attemptCount++) >= attempts) {
                    throw e;
                }
                try {
                    Thread.sleep(attemptTimeout.toMillis());
                } catch (InterruptedException ie) {
                    // do nothing
                }
            }
        }
    }

    /**
     * Удаляет директорию со всеми вложенными объектами. Перед удалением снимает аттрибуты, блокирующие удаление.
     * Используется на ОС Windows, чтобы избежать ошибки java.nio.file.AccessDeniedException
     *
     * @param dir Директория для удаления
     * @return true при успешном удалении.
     * @throws IOException
     */
    public static boolean deleteWinDirectoryIfExists(Path dir) throws IOException {
        if (!Files.exists(dir) || !Files.isDirectory(dir) || !dir.getFileSystem().supportedFileAttributeViews().contains("dos")) {
            return false;
        }

        IOException[] exception = new IOException[]{null};
        Files.walkFileTree(dir, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                try {
                    Files.setAttribute(file, "dos:hidden", false);
                    Files.setAttribute(file, "dos:readonly", false);
                    Files.delete(file);
                } catch (IOException e) {
                    if (exception[0] == null) exception[0] = e;
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
                if (exc != null && exception[0] == null) {
                    exception[0] = exc;
                }

                try {
                    Files.setAttribute(dir, "dos:hidden", false);
                    Files.setAttribute(dir, "dos:readonly", false);
                    Files.delete(dir);
                } catch (IOException e) {
                    if (exception[0] == null) exception[0] = e;
                }
                return FileVisitResult.CONTINUE;
            }
        });
        if (exception[0] != null) {
            throw exception[0];
        }
        return true;
    }

    /**
     * Удаляет директорию со всеми вложенными объектами. Перед удалением снимает аттрибуты, блокирующие удаление.
     * Используется на ОС Windows, чтобы избежать ошибки java.nio.file.AccessDeniedException.
     * @param dir Директория для удаления
     * @param attempts Количество попыток
     * @param attemptTimeout Таймаут между попытками
     * @return true при успешном удалении.
     * @throws IOException
     */
    public static boolean deleteWinDirectoryIfExists(Path dir, int attempts, Duration attemptTimeout) throws IOException {
        int attemptCount = 0;
        while (true) {
            try {
                return deleteWinDirectoryIfExists(dir);
            } catch (IOException e) {
                if ((attemptCount++) >= attempts) {
                    throw e;
                }
                try {
                    Thread.sleep(attemptTimeout.toMillis());
                } catch (InterruptedException ie) {
                    // do nothing
                }
            }
        }
    }

    public static long sizeOfDirectory(final Path directory) {
        long size = 0;
        try (Stream<Path> walk = Files.walk(directory)) {
            size = walk
                    .filter(Files::isRegularFile)
                    .mapToLong(p -> {
                        try {
                            return Files.size(p);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .sum();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return size;
    }

    /**
     * Makes a directory. If a file already exists with specified name but it is
     * not a directory then an IOException is thrown.
     * @param dir
     * @throws IOException
     * @throws  SecurityException
     */
    public static void ensureDirectory(Path dir) throws IOException {
        if (!Files.exists(dir) || !Files.isDirectory(dir)) {
            Files.createDirectory(dir);
        }
    }

    /**
     * Makes a directory, including any necessary but nonexistent parent
     * directories. If a file already exists with specified name but it is
     * not a directory then an IOException is thrown.
     * @param dir
     * @throws IOException
     * @throws SecurityException
     */
    public static void ensureDirectories(Path dir) throws IOException {
        if (!Files.exists(dir) || !Files.isDirectory(dir)) {
            ensureDirectories(dir.getParent());
            Files.createDirectory(dir);
        }
    }

    public static void copyDirectory(Path source, Path target) throws IOException {
        String sourceDirectoryLocation = source.toAbsolutePath().toString();
        String destinationDirectoryLocation = target.toAbsolutePath().toString();
        try (Stream<Path> stream = Files.walk(Paths.get(sourceDirectoryLocation))) {
            stream.forEach(sourcePathFile -> {
                Path destination = Paths.get(destinationDirectoryLocation, sourcePathFile.toString()
                        .substring(sourceDirectoryLocation.length()));
                try {
                    Files.copy(sourcePathFile, destination);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    public static void copyDirectory(Path source, Path target, CopyOption...options) throws IOException {
        String sourceDirectoryLocation = source.toAbsolutePath().toString();
        String destinationDirectoryLocation = target.toAbsolutePath().toString();
        try (Stream<Path> stream = Files.walk(Paths.get(sourceDirectoryLocation))) {
            stream.forEach(sourcePathFile -> {
                Path destination = Paths.get(destinationDirectoryLocation, sourcePathFile.toString()
                        .substring(sourceDirectoryLocation.length()));
                try {
                    Files.copy(sourcePathFile, destination, options);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    public static boolean isLocked(Path filePath) throws IOException {
        try (FileChannel channel = FileChannel.open(filePath, StandardOpenOption.WRITE);
             FileLock lock = channel.tryLock()) {
            return lock == null;
        } catch (OverlappingFileLockException e) {
            return true;
        }
    }

    public static void deleteQuietly(Collection<Path> pathes) {
        if (pathes == null) {
            return;
        }

        for (Path path : pathes) {
            try {
                Files.deleteIfExists(path);
            } catch (IOException ignore) {}
        }
    }
}
