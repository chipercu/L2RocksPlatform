package com.fuzzy.main.config;

import com.fuzzy.config.LoginConfig;
import com.fuzzy.config.configsystem.ConfigBuilder;
import com.fuzzy.config.configsystem.ConfigClass;
import com.fuzzy.config.ConfigManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Created by a.kiperku
 * Date: 04.04.2024
 */

public class ConfigTest {

    @Test
    @DisplayName("Инициализация всех конфигов")
    public void loadAllConfigs(){
        ConfigManager.loadAll();
    }
    @Test
    @DisplayName("Инициализация всех конфигов")
    public void loadConfigByName() throws IOException {
        final String name = LoginConfig.class.getAnnotation(ConfigClass.class).name();
        final Path configPath = Path.of(ConfigBuilder.CONFIG_DIRECTORY + name + ".json");
        Files.deleteIfExists(configPath);
        Assertions.assertFalse(Files.exists(configPath));
        ConfigManager.loadByName(name);
        Assertions.assertTrue(Files.exists(configPath));
        Assertions.assertEquals(LoginConfig.Debug, false);

    }

}
