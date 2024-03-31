package com.fuzzy.config.configsystem;

import com.fuzzy.config.AppConfig;
import com.fuzzy.config.configsystem.CompactJsonFormat.CompactJsonObject;
import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.subsystem.core.crypto.Crypto;
import com.fuzzy.subsystems.utils.FileUtils;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by a.kiperku
 * Date: 05.02.2024
 */

public class ConfigBuilder<T extends ISubsystemConfig> {

    private final Map<String, Object> configObjectMap = new HashMap<>();
    private final boolean encrypt;

    public static void load(Class<? extends ISubsystemConfig> configClass, boolean encrypt) {
        try {
            new ConfigBuilder<>(configClass, encrypt);
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException |
                 PlatformException e) {
            throw new RuntimeException(e);
        }
    }


    private ConfigBuilder(Class<T> configClass, boolean encrypt) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, PlatformException {
        this.encrypt = encrypt;
        final T instance = configClass.getDeclaredConstructor().newInstance();
        JSONObject json = readJSON(instance.getConfigPathName());
        if (json.isEmpty()) {
            initDefault(configClass);
            save(instance.getConfigPathName(), configClass);
        } else {
            loadFrom(json, instance.getConfigPathName(), configClass);
        }
        initConfigFields(instance, configClass);
    }

    private List<Field> getAnnotatedFields(Class<T> configClass) {
        final Field[] declaredFields = configClass.getDeclaredFields();
        final ArrayList<Field> fields = new ArrayList<>();
        for (Field field : declaredFields) {
            if (field.isAnnotationPresent(Config.class)) {
                fields.add(field);
            }
        }
        return fields;
    }

    private void withAnnotation(Field field, Config annotation) {
        if (field.getType() == String.class) {
            configObjectMap.put(field.getName(), annotation.strValue());
        } else if (field.getType() == String[].class) {
            configObjectMap.put(field.getName(), annotation.strArrValue());
        } else if (field.getType() == Integer.class || field.getType() == int.class) {
            configObjectMap.put(field.getName(), annotation.intValue());
        } else if (field.getType() == Long.class || field.getType() == long.class) {
            configObjectMap.put(field.getName(), annotation.longValue());
        } else if (field.getType() == Double.class || field.getType() == double.class) {
            configObjectMap.put(field.getName(), annotation.doubleValue());
        } else if (field.getType() == Boolean.class || field.getType() == boolean.class) {
            configObjectMap.put(field.getName(), annotation.boolValue());
        } else if (field.getType() == Integer[].class || field.getType() == int[].class) {
            configObjectMap.put(field.getName(), annotation.intArrValue());
        } else if (field.getType() == Long[].class || field.getType() == long[].class) {
            configObjectMap.put(field.getName(), annotation.longArrValue());
        } else if (field.getType() == Double[].class || field.getType() == double[].class) {
            configObjectMap.put(field.getName(), annotation.doubleArrValue());
        } else if (field.getType() == Boolean[].class || field.getType() == boolean[].class) {
            configObjectMap.put(field.getName(), annotation.boolArrValue());
        }
    }

    private void initDefault(Class<T> configClass) {
        final List<Field> annotatedFields = getAnnotatedFields(configClass);
        for (Field field : annotatedFields) {
            Config annotation = field.getAnnotation(Config.class);
            withAnnotation(field, annotation);
        }
    }

    private void loadFrom(JSONObject json, String configPathName, Class<T> configClass) throws PlatformException {
        boolean need_save = false;
        final List<Field> annotatedFields = getAnnotatedFields(configClass);
        for (Field field : annotatedFields) {
            Config annotation = field.getAnnotation(Config.class);
            if (json.containsKey(field.getName())) {
                final JSONObject jsonObject = (JSONObject) json.get(field.getName());
                if (field.getType() == String.class) {
                    configObjectMap.put(field.getName(), jsonObject.getAsString("value"));
                } else if (field.getType() == Integer.class || field.getType() == int.class) {
                    configObjectMap.put(field.getName(), jsonObject.getAsNumber("value").intValue());
                } else if (field.getType() == Long.class || field.getType() == long.class) {
                    configObjectMap.put(field.getName(), jsonObject.getAsNumber("value").longValue());
                } else if (field.getType() == Double.class || field.getType() == double.class) {
                    configObjectMap.put(field.getName(), jsonObject.getAsNumber("value").doubleValue());
                } else if (field.getType() == Boolean.class || field.getType() == boolean.class) {
                    configObjectMap.put(field.getName(), Boolean.parseBoolean(jsonObject.getAsString("value")));
                } else if (field.getType() == String[].class) {
                    final JSONArray value = (JSONArray) jsonObject.get("value");
                    String[] array = new String[value.size()];
                    for (int i = 0; i < value.size(); i++) {
                        array[i] = (String) value.get(i);
                    }
                    configObjectMap.put(field.getName(), array);
                } else if (field.getType() == Integer[].class || field.getType() == int[].class) {
                    final JSONArray value = (JSONArray) jsonObject.get("value");
                    int[] array = new int[value.size()];
                    for (int i = 0; i < value.size(); i++) {
                        array[i] = (int) value.get(i);
                    }
                    configObjectMap.put(field.getName(), array);
                } else if (field.getType() == Long[].class || field.getType() == long[].class) {
                    final JSONArray value = (JSONArray) jsonObject.get("value");
                    long[] array = new long[value.size()];
                    for (int i = 0; i < value.size(); i++) {
                        array[i] = Long.valueOf((Integer) value.get(i));
                    }
                    configObjectMap.put(field.getName(), array);
                } else if (field.getType() == Double[].class || field.getType() == double[].class) {
                    final JSONArray value = (JSONArray) jsonObject.get("value");
                    double[] array = new double[value.size()];
                    for (int i = 0; i < value.size(); i++) {
                        array[i] = (double) value.get(i);
                    }
                    configObjectMap.put(field.getName(), array);
                } else if (field.getType() == Boolean[].class || field.getType() == boolean[].class) {
                    final JSONArray value = (JSONArray) jsonObject.get("value");
                    boolean[] array = new boolean[value.size()];
                    for (int i = 0; i < value.size(); i++) {
                        array[i] = (boolean) value.get(i);
                    }
                    configObjectMap.put(field.getName(), array);
                }
            } else {
                withAnnotation(field, annotation);
                need_save = true;
            }
        }
        if (need_save) {
            save(configPathName, configClass);
        }
    }

    public void initConfigFields(ISubsystemConfig config, Class<T> configClass) {
        final List<Field> annotatedFields = getAnnotatedFields(configClass);
        for (Field field : annotatedFields) {
            try {
                field.setAccessible(true);
                if (Modifier.isStatic(field.getModifiers())) {
                    final Object value = configObjectMap.get(field.getName());
                    field.set(null, value);
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        config.init();
    }

    public void save(String configPathName, Class<T> configClass) throws PlatformException {
        final CompactJsonObject mainCompactObject = new CompactJsonObject();
        final List<Field> annotatedFields = getAnnotatedFields(configClass);
        for (Field field : annotatedFields) {
            final CompactJsonObject compactJsonObject = new CompactJsonObject(field.getName());
            Config annotation = field.getAnnotation(Config.class);
            Object value = configObjectMap.get(field.getName());
            compactJsonObject.putField("description", annotation.desc(), true);
            if (value instanceof String val) {
                compactJsonObject.putField("value", val, true);
            } else if (value instanceof Integer val) {
                compactJsonObject.putField("value", val, true);
            } else if (value instanceof Long val) {
                compactJsonObject.putField("value", val, true);
            } else if (value instanceof Double val) {
                compactJsonObject.putField("value", val, true);
            } else if (value instanceof Boolean val) {
                compactJsonObject.putField("value", val, true);
            } else if (value instanceof String[] val) {
                compactJsonObject.putField("value", val, true);
            } else if (value instanceof int[] val) {
                compactJsonObject.putField("value", val, true);
            } else if (value instanceof long[] val) {
                compactJsonObject.putField("value", val, true);
            } else if (value instanceof double[] val) {
                compactJsonObject.putField("value", val, true);
            } else if (value instanceof boolean[] val) {
                compactJsonObject.putField("value", val, true);
            }
            mainCompactObject.putField(compactJsonObject);
        }

        final Path path = Path.of(AppConfig.CONFIG_DIRECTORY + configPathName + ".json");
        saveToFile(path, mainCompactObject.build());
    }

    private void saveToFile(Path path, String json) throws PlatformException {
        if (encrypt){
            try {
                Crypto crypto = new Crypto(Path.of(AppConfig.CONFIG_DIRECTORY + "secret_key"));
                FileUtils.saveToFile(crypto.encrypt(json), path);
            } catch (PlatformException e) {
                throw new RuntimeException(e);
            }
        }else {
            FileUtils.saveToFile(json.getBytes(), path);
        }


    }

    private JSONObject readJSON(String configPathName) throws PlatformException {
        JSONObject configJson;
        final Path path = Path.of(AppConfig.CONFIG_DIRECTORY + configPathName + ".json");
        if (Files.exists(path)) {

            byte[] bytes = FileUtils.readBytesFromFile(path);
            if (encrypt){
                try {
                    Crypto crypto = new Crypto(Path.of(AppConfig.CONFIG_DIRECTORY + "secret_key"));
                    configJson = (JSONObject) new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE).parse(crypto.decrypt(bytes));
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }else {
                try {
                    configJson = (JSONObject) new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE).parse(bytes);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
        } else {
            configJson = new JSONObject();
        }
        return configJson;
    }

}
