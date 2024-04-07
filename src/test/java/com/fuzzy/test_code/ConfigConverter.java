package com.fuzzy.test_code;

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
    public void convert2() throws NoSuchFieldException {
        ConfigSystem.load();
        List<Config> configs = new ArrayList<>();
        Config currentConfig;
        StringBuilder descriptionBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader("data/config/items.properties"))) {
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


        // Выводим полученные объекты Config
        for (Config config : configs) {
            Field field = ConfigValue.class.getField(config.key);

            String configName = "items_config";

            Class<?> type = field.getType();
            String typeSimpleName = type.getSimpleName();
            String replace = typeSimpleName.replace("float", "double")
                    .replace("byte", "int");

            StringBuilder configBuilder = new StringBuilder();


            configBuilder.append("package com.fuzzy.config;\n");
            configBuilder.append("""
                                    import com.fuzzy.config.configsystem.ConfigClass;
                                    import com.fuzzy.config.configsystem.ConfigField;
                                    \n
                                    """);

            configBuilder.append("@ConfigClass(name = \"").append(configName).append("\")\n");
            configBuilder.append("public class LoginConfig {\n");
            configBuilder.append("");




            File file = new File(configName);



            System.out.println("@ConfigField(desc=\"" + config.getDescription() + "\"" + ",\n " +
                    "intValue = " + config.getValue() + ") public static " + replace + " " + config.getKey() + ";\n");


            try (FileWriter writer = new FileWriter(file)) {
                writer.write(generatedObject.getStringClass());
            } catch (IOException e) {
                System.out.println("Ошибка при записи в файл: " + e.getMessage());
            }

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
    }


}
