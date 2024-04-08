package com.fuzzy.test_code;

import com.fuzzy.config.ConfigManager;
import com.fuzzy.subsystem.config.ConfigSystem;
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
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * Created by a.kiperku
 * Date: 04.04.2024
 */

public class ConfigConverter {

    @Test
    public void convert() throws IOException, NoSuchFieldException, IllegalAccessException {

        final ZipParameters zipParameters = new ZipParameters();

        final List<File> filesInDirectoryRecursive = FileUtils.getFilesInDirectoryRecursive(new File("data/config"), zipParameters);

        for (File file : filesInDirectoryRecursive) {
            if (!file.isDirectory() && file.getName().endsWith(".properties") && !file.getName().equals("log.properties")) {
                Properties properties = new Properties();
                properties.load(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));

                for (Object key : properties.keySet()) {
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
    public void convert2() throws NoSuchFieldException, IllegalAccessException {
        ConfigSystem.load();
        List<Config> configs = new ArrayList<>();
        Config currentConfig;
        StringBuilder descriptionBuilder = new StringBuilder();
        final File proprietesFile = new File("data/config/skills.properties");
        try (BufferedReader br = new BufferedReader(new FileReader(proprietesFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("#")) {
                    descriptionBuilder.append(line.replace("#", ""));
                    // Создаем новый Config объект для комментария
                } else if (line.contains("=")) {
                    currentConfig = new Config();
                    // Найдено ключ-значение
                    String[] parts = line.split("=");
                    currentConfig.setKey(parts[0].trim());
                    currentConfig.setValue(parts[1].trim());
                    currentConfig.setDescription(descriptionBuilder.toString());
                    configs.add(currentConfig);
                    descriptionBuilder.setLength(0);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        StringBuilder configBuilder = new StringBuilder();
        String configName = proprietesFile.getName().replace(".properties", "") + "_config";
        final String packageName = ConfigManager.class.getPackage().getName();
        configBuilder.append("package ").append(packageName).append(";\n\n");
        configBuilder.append("""
                import com.fuzzy.config.configsystem.ConfigClass;
                import com.fuzzy.config.configsystem.ConfigField;
                                                    
                """);

        configBuilder.append("@ConfigClass(name = \"").append(configName).append("\")\n");
        configBuilder.append("public class ").append(configName).append(" {\n\n");


        // Выводим полученные объекты Config
        for (Config config : configs) {
            Field field = ConfigValue.class.getField(config.key);

            Class<?> type = field.getType();
            String typeSimpleName = type.getSimpleName();
            String replace = typeSimpleName.replace("float", "double")
                    .replace("byte", "int");

            String annotationDataType = "strValue";
//            String value = config.getValue();
            Object fieldValue = field.get(null);
            String value = "";

            if (type.equals(String.class)) {
                annotationDataType = "strValue";
                value = (String) fieldValue;
            } else if (type.equals(Boolean.class) || type.equals(boolean.class)) {
                annotationDataType = "boolValue";
                value = String.valueOf(fieldValue);
            } else if (type.equals(Integer.class) || type.equals(int.class)) {
                annotationDataType = "intValue";
                value = String.valueOf(fieldValue);
            } else if (type.equals(Double.class) || type.equals(double.class) || type.equals(Float.class) || type.equals(float.class)) {
                annotationDataType = "doubleValue";
                value = String.valueOf(fieldValue);
            } else if (type.equals(Long.class) || type.equals(long.class)) {
                annotationDataType = "longValue";
                value = String.valueOf(fieldValue);
            } else if(type.equals(String[].class)) {
                annotationDataType = "strArrValue";
                value = "{" + fieldValue  + "}";
            } else if (type.equals(Boolean[].class) || type.equals(boolean[].class)) {
                annotationDataType = "boolArrValue";
                value = "{" + fieldValue  + "}";
            } else if (type.equals(Integer[].class) || type.equals(int[].class)) {
                annotationDataType = "intArrValue";
                value = arrToString(fieldValue);
            } else if (type.equals(Double[].class) || type.equals(double[].class) || type.equals(Float[].class) || type.equals(float[].class)) {
                annotationDataType = "doubleArrValue";
                value = "{" + fieldValue  + "}";
            } else if (type.equals(Long[].class) || type.equals(long[].class)) {
                annotationDataType = "longArrValue";
                value = "{" + fieldValue + "}";
            }


            configBuilder.append("    @ConfigField(desc=\"").append(config.getDescription()).append("\"").append(",\n")
                    .append("            ").append(annotationDataType).append(" = ").append(value).append(")\n    public static ").append(replace).append(" ").append(config.getKey()).append(";\n\n");
        }
        configBuilder.append("}\n");
        File file = new File("src/main/java/" + ConfigManager.class.getPackage().getName().replace(".", "/") + "/" + configName + ".java");
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(configBuilder.toString());
        } catch (IOException e) {
            System.out.println("Ошибка при записи в файл: " + e.getMessage());
        }

    }

    public String arrToString(Object val){
        if (val instanceof int[] value){
            final String collect = Arrays.stream(value).mapToObj(String::valueOf).collect(Collectors.joining(","));
            return "{" + collect + "}";
        }
        return "{}";
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
    }


}
