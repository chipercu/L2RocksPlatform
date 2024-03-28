package com.fuzzy.main.cluster;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ComponentsConfig {

    private final List<String> included;
    private final List<String> excluded;

    private ComponentsConfig(List<String> included, List<String> excluded) {
        this.included = included;
        this.excluded = excluded;
    }

    public List<String> getIncluded() {
        return included;
    }

    public List<String> getExcluded() {
        return excluded;
    }

    public static ComponentsConfig load(JSONObject json) {
        List<String> included = loadComponents(json, "included");
        List<String> excluded = loadComponents(json, "excluded");
        return new ComponentsConfig(included, excluded);
    }

    private static List<String> loadComponents(JSONObject json, String key) {
        List<String> components = null;
        JSONArray jComponents = (JSONArray) json.get(key);
        if (jComponents != null) {
            components = new ArrayList<>(jComponents.size());
            for (Object item : jComponents) {
                if (item instanceof String) {
                    components.add((String) item);
                } else {
                    throw new RuntimeException("Invalid components config");
                }
            }
        }
        return components;
    }
}
