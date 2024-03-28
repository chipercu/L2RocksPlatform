package com.fuzzy.subsystems.utils;

import com.fuzzy.subsystems.Subsystems;

import java.nio.file.Files;
import java.nio.file.Path;

public class TempFiles {

    public static Path buildTempFilePath() {
        Path tempDirPath = Subsystems.getInstance().getConfig().getTempDir();
        Path filePath;
        do {
            String fileName = RandomStringUtils.randomAlphanumeric(20, 21);
            filePath = tempDirPath.resolve(fileName);
        } while (Files.exists(filePath));
        return filePath;
    }

}
