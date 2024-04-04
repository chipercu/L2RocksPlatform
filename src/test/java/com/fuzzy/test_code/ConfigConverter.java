package com.fuzzy.test_code;

import com.fuzzy.subsystem.config.ConfigValue;
import com.google.common.io.Files;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.FileUtils;
import org.junit.jupiter.api.Test;
import org.reflections.Reflections;
import org.springframework.util.ReflectionUtils;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by a.kiperku
 * Date: 04.04.2024
 */

public class ConfigConverter {

    @Test
    public void convert() throws IOException, NoSuchFieldException, IllegalAccessException {

        final ZipParameters zipParameters = new ZipParameters();

        final List<File> filesInDirectoryRecursive = FileUtils.getFilesInDirectoryRecursive(new File("data/config"), zipParameters);

        for (File file: filesInDirectoryRecursive){
            if (!file.isDirectory() && file.getName().endsWith(".properties") && !file.getName().equals("log.properties")){
                Properties properties = new Properties();
                properties.load(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));

                for (Object key: properties.keySet()){
                    final Field declaredField = ConfigValue.class.getDeclaredField((String) key);
                    System.out.println(key + " = " + declaredField.get(null).toString());
                }
            }
        }
//        final Field[] declaredFields = ConfigValue.class.getDeclaredFields();
//
//        for (Field field : declaredFields){
//            if (field.getType().equals(String.class)){
//
//            }
//        }


    }

    @Test
    public void convert2(){

        List<Config> configs = new ArrayList<>();
        Config currentConfig = null;
        StringBuilder descriptionBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader("data/config/items.properties"))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("# ")) {
                    if (currentConfig != null) {
                        currentConfig.setDescription(descriptionBuilder.toString().trim());
                        descriptionBuilder.setLength(0); // Очищаем StringBuilder
                    }
                    // Создаем новый Config объект для комментария
                    currentConfig = new Config();
                } else if (line.contains("=") && currentConfig != null) {
                    // Найдено ключ-значение
                    String[] parts = line.split("=");
                    currentConfig.setKey(parts[0].trim());
                    currentConfig.setValue(parts[1].trim());

                    configs.add(currentConfig);
                    currentConfig = null; // Сбрасываем текущий объект Config
                } else {
                    if (currentConfig != null) {
                        // Пополняем описание для текущего Config объекта
                        descriptionBuilder.append(line.trim()).append(" ");
                    }
                }
            }

            // Обработка последнего описания после последнего Config
            if (currentConfig != null) {
                currentConfig.setDescription(descriptionBuilder.toString().trim());
                configs.add(currentConfig);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        // Выводим полученные объекты Config
        for (Config config : configs) {
            System.out.println("@ConfigField(desc=\"" + config.getDescription() + "\"" + ",\n " +
                    "intValue = " + config.getValue() + ") public static int " + config.getKey() + ";\n");

        }
    }

    public class Config {
        private String key;
        private String value;
        private String description;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        // Геттеры и сеттеры
    }


}
