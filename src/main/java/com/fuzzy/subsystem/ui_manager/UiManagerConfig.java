package com.fuzzy.subsystem.ui_manager;

import com.fuzzy.cluster.struct.Info;
import com.fuzzy.main.SubsystemsConfig;
import com.fuzzy.subsystem.core.config.LogonType;
import com.fuzzy.subsystems.exception.runtime.ConfigBuilderException;
import com.fuzzy.subsystems.subsystem.SubsystemConfig;
import net.minidev.json.JSONObject;
import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Locale;
import java.util.Objects;

public class UiManagerConfig extends SubsystemConfig {

    public static final String JSON_DEBUG_MODE = "debug";
    public static final boolean DEFAULT_DEBUG_MODE = false;
    private final boolean debugMode;

    private UiManagerConfig(Builder builder) {
        super(builder);

        this.debugMode = builder.debugMode;

    }

    public boolean isDebugMode() {
        return debugMode;
    }

    public static class Builder extends SubsystemConfig.Builder {

        private boolean debugMode;

        public Builder(Info subSystemInfo, SubsystemsConfig subsystemsConfig) throws ConfigBuilderException {
            super(subSystemInfo, subsystemsConfig);
            init();
        }

        @Override
        public UiManagerConfig build() {
            return new UiManagerConfig(this);
        }

        private void init() throws ConfigBuilderException {
            JSONObject json = readJSON();
            if (json.isEmpty()) {
                initDefault();
                save();
            } else {
                loadFrom(json);
            }
        }

        private void initDefault() {
            this.debugMode = DEFAULT_DEBUG_MODE;
        }

        private void loadFrom(JSONObject json) {
            loadDebugModeConfig(json);
        }

        private void loadDebugModeConfig(JSONObject json) {
            if (json.containsKey(JSON_DEBUG_MODE)) {
                debugMode = (boolean) json.get(JSON_DEBUG_MODE);
            } else {
                debugMode = DEFAULT_DEBUG_MODE;
            }
        }

        public void save() {
            JSONObject json = new JSONObject();
            json.appendField(JSON_DEBUG_MODE, DEFAULT_DEBUG_MODE);
            saveJSON(json);
        }
    }

}