package com.fuzzy.utils;

import org.junit.jupiter.api.*;

import java.nio.file.Path;

/**
 * Created by a.kiperku
 * Date: 28.03.2024
 */

public class FileUtilsTest {

    @Test
    @DisplayName("Размер папки")
    public void sizeOfDirectory(){
        final long size = FileUtils.sizeOfDirectory(Path.of("C:\\Users\\a.kiperku\\Desktop\\console_testing_agent_cta231101"));
        Assertions.assertTrue(size > 0);

    }




}
