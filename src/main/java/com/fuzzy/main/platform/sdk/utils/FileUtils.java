package com.fuzzy.main.platform.sdk.utils;

import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.sdk.exception.GeneralExceptionBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileUtils {

    public static void ensureDirectory(Path dir) throws PlatformException {
        try {
            if (!Files.exists(dir) || !Files.isDirectory(dir)) {
                Files.createDirectory(dir);
            }
        } catch (IOException e) {
            throw GeneralExceptionBuilder.buildIOErrorException(e);
        } catch (SecurityException e) {
            throw GeneralExceptionBuilder.buildSecurityException(e);
        }
    }

}
