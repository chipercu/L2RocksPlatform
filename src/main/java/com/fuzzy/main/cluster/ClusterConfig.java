package com.fuzzy.main.cluster;

import com.fuzzy.main.SubsystemsConfig;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class ClusterConfig {

    private final ComponentsConfig componentsConfig;
    private final NetworkConfig networkConfig;

    private ClusterConfig(ComponentsConfig componentsConfig, NetworkConfig networkConfig) {
        this.componentsConfig = componentsConfig;
        this.networkConfig = networkConfig;
    }

    public ComponentsConfig getComponentsConfig() {
        return componentsConfig;
    }

    public NetworkConfig getNetworkConfig() {
        return networkConfig;
    }

    public static ClusterConfig load(SubsystemsConfig subsystemsConfig) {
        Path configPath = subsystemsConfig.getConfigDir().resolve("cluster.json");
        if (!Files.exists(configPath)) {
            return null;
        }
        JSONObject json;
        try (InputStream is = Files.newInputStream(configPath, StandardOpenOption.READ)) {
            json = (JSONObject) new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE).parse(is);
        } catch (Exception e) {
            throw new RuntimeException("Invalid components config", e);
        }
        ComponentsConfig componentsConfig = null;
        JSONObject jComponents = (JSONObject) json.get("components");
        if (jComponents != null) {
            componentsConfig = ComponentsConfig.load((JSONObject) json.get("components"));
        }
        NetworkConfig networkConfig = null;
        JSONObject jNetwork = (JSONObject) json.get("network");
        if (jNetwork != null) {
            networkConfig = NetworkConfig.load((JSONObject) json.get("network"), subsystemsConfig.getDataDir());
        }
        return new ClusterConfig(componentsConfig, networkConfig);
    }
}
