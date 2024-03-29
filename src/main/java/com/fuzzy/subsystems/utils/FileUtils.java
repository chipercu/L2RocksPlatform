package com.fuzzy.subsystems.utils;

import com.infomaximum.cluster.struct.Component;
import com.fuzzy.main.Subsystems;
import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.sdk.struct.ClusterFile;
import com.fuzzy.subsystems.exception.GeneralExceptionBuilder;
import org.apache.commons.lang3.RandomStringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;

public class FileUtils {

    public static void ensureDirectories(Path dir) throws PlatformException {
        try {
            com.fuzzy.utils.FileUtils.ensureDirectories(dir);
        } catch (IOException e) {
            throw GeneralExceptionBuilder.buildIOErrorException(e);
        } catch (SecurityException e) {
            throw GeneralExceptionBuilder.buildSecurityException(e);
        }
    }

    /**
     * Создает временный файл который забираем из uri.
     * <br>
     * Вернет готовый Path для работы.
     * <br>
     * За удаление временных файлов отвечает ОС или вызывающая данный метод сторона.
     *
     * @param uri Адрес файла в файловой системе.
     * @return вернет Path файла который обработан для работы при многосерверной развертке системы.
     * @throws PlatformException Может вернуть {@link GeneralExceptionBuilder#buildIOErrorException(IOException)} если файл в процессе работы был удален или перемещен.
     */
    public static Path copyFileToTempDir(@NonNull Component component, @NonNull URI uri) throws PlatformException {
        try {
            final Path temp = Files.createTempFile("temp_", "");
            final ClusterFile clusterFile = new ClusterFile(component, uri);
            clusterFile.copyTo(temp, StandardCopyOption.REPLACE_EXISTING);
            return temp;
        } catch (IOException e) {
            throw GeneralExceptionBuilder.buildIOErrorException(e);
        }
    }

    public static void saveToFile(byte[] data, Path path) throws PlatformException {
        if (data == null) throw new IllegalArgumentException();
        try (OutputStream outputStream = Files.newOutputStream(path,
                StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)) {
            outputStream.write(data);
        } catch (IOException e) {
            throw GeneralExceptionBuilder.buildIOErrorException(e);
        }
    }

    public static Path saveToFileIntoSubsystemsTempDir(byte[] data, String fileName) throws PlatformException {
        Path path = Subsystems.getInstance().getConfig().getTempDir().resolve(fileName);
        saveToFile(data, path);
        return path;
    }

    public static Path saveToFileIntoSubsystemsTempDir(byte[] data) throws PlatformException {
        Path tempDir = Subsystems.getInstance().getConfig().getTempDir();
        Path path;
        do {
            String fileName = RandomStringUtils.randomAlphanumeric(20, 21);
            path = tempDir.resolve(fileName);
        } while (Files.exists(path));
        saveToFile(data, path);
        return path;
    }

    public static byte[] readBytesFromFile(Path path) throws PlatformException {
        try (InputStream inputStream = Files.newInputStream(path, StandardOpenOption.READ)) {
            final ByteArrayOutputStream output = new ByteArrayOutputStream();
            inputStream.transferTo(output);
            return output.toByteArray();
        } catch (IOException e) {
            throw GeneralExceptionBuilder.buildIOErrorException(e);
        }
    }
}
