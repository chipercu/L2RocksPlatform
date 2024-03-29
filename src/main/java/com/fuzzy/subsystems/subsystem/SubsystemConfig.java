package com.fuzzy.subsystems.subsystem;

import com.fuzzy.main.cluster.struct.Info;
import com.fuzzy.subsystems.SubsystemsConfig;
import com.fuzzy.subsystems.exception.runtime.ConfigBuilderException;
import com.fuzzy.subsystems.utils.TimeConsts;
import com.fuzzy.utils.FileUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONStyle;
import net.minidev.json.parser.JSONParser;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Duration;

public abstract class SubsystemConfig {

    private final Path configPath;

    protected SubsystemConfig(Builder builder) {
        this.configPath = builder.configPath;
    }

    public Path getConfigPath() {
        return configPath;
    }



    public static abstract class Builder {

        private final SubsystemsConfig subsystemsConfig;
        private final Path configPath;

        protected Builder(Info subsystemInfo, SubsystemsConfig subsystemsConfig) throws ConfigBuilderException {
            this(subsystemInfo.getUuid(), subsystemsConfig);
        }
        protected Builder(String configName, SubsystemsConfig subsystemsConfig) throws ConfigBuilderException {
            this.subsystemsConfig = subsystemsConfig;
            this.configPath = subsystemsConfig.getConfigDir().resolve(configName + ".json");
        }

        protected SubsystemsConfig getSubsystemsConfig() {
            return subsystemsConfig;
        }

        public abstract SubsystemConfig build();

        protected void saveJSON(JSONObject json) throws ConfigBuilderException {
            saveJSON(json, configPath);
        }
        protected void saveGSON(JsonObject json) throws ConfigBuilderException {
            saveGSON(json, configPath);
        }


        protected JSONObject readJSON() throws ConfigBuilderException {
            JSONObject configJson;
            if (Files.exists(configPath)) {
                try (InputStream is = Files.newInputStream(configPath, StandardOpenOption.READ)) {
                    configJson = (JSONObject) new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE).parse(is);
                } catch (Exception e) {
                    throw new ConfigBuilderException(e);
                }
            } else {
                configJson = new JSONObject();
            }
            return configJson;
        }

        private static void saveGSON(JsonObject jsonObject, Path configPath){
            try (OutputStream outputStream = Files.newOutputStream(configPath, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
                 PrintWriter writer = new PrintWriter(outputStream)) {
                final Gson gson = new GsonBuilder().setPrettyPrinting().create();
                gson.toJson(jsonObject, writer);
            } catch (IOException e) {
                throw new ConfigBuilderException(e);
            }
        }

        private static void saveJSON(JSONObject json, Path configPath) throws ConfigBuilderException {
            try (OutputStream outputStream = Files.newOutputStream(configPath, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
                 PrintWriter writer = new PrintWriter(outputStream)) {
                json.writeJSONString(writer, JSONStyle.NO_COMPRESS);
            } catch (IOException e) {
                throw new ConfigBuilderException(e);
            }
        }

        protected static Path getPath(String jsonKey, JSONObject source, String defaultDir, Path parentPath) throws ConfigBuilderException {
            Path dir = getPathWithoutCreation(jsonKey, source, defaultDir, parentPath);
            ensureDir(dir);
            return dir;
        }

        protected static Path getPathWithoutCreation(String jsonKey, JSONObject source, String defaultDir, Path parentPath) throws ConfigBuilderException {
            Path dir;
            if (source.containsKey(jsonKey)) {
                dir = Paths.get(source.getAsString(jsonKey));
            } else {
                dir = Paths.get(defaultDir);
            }

            if (!dir.isAbsolute()) {
                dir = parentPath.resolve(dir).toAbsolutePath();
            }

            return dir.normalize();
        }

        protected static Path tryRelativizePath(Path relativizedPath, Path parentPath) {
            try {
                return parentPath.relativize(relativizedPath);
            } catch (IllegalArgumentException e) {
                return relativizedPath;
            }
        }

        protected static Path ensureDir(Path dir) throws ConfigBuilderException {
            try {
                FileUtils.ensureDirectory(dir);
                return dir;
            } catch (IOException | SecurityException e) {
                throw new ConfigBuilderException(e);
            }
        }

        public static Duration parseDuration(String value) {
            if (StringUtils.isEmpty(value)) {
                throw new ConfigBuilderException("Exception parse duration: value is empty");
            }
            try {
                char type = value.charAt(value.length() - 1);
                long pValue = Long.parseLong(value.substring(0, value.length() - 1));
                switch (type) {
                    case 's':
                        return Duration.ofSeconds(pValue);
                    case 'm':
                        return Duration.ofMinutes(pValue);
                    case 'h':
                        return Duration.ofHours(pValue);
                    case 'd':
                        return Duration.ofDays(pValue);
                    default:
                        throw new RuntimeException("Unknown type: " + type);
                }
            } catch (Exception e) {
                throw new ConfigBuilderException(e);
            }
        }

        public static String packDuration(Duration value) {
            if (value.getNano() > 0) {
                throw new RuntimeException("Not support duration less second");
            }
            if (value.getSeconds() == 0) {
                return "0s";
            } else if ((value.getSeconds() % TimeConsts.SECONDS_PER_DAY) == 0) {
                return value.toDays() + "d";
            } else if ((value.getSeconds() % TimeConsts.SECONDS_PER_HOUR) == 0) {
                return value.toHours() + "h";
            } else if ((value.getSeconds() % TimeConsts.SECONDS_PER_MINUTE) == 0) {
                return value.toMinutes() + "m";
            } else {
                return value.getSeconds() + "s";
            }
        }
    }
}
