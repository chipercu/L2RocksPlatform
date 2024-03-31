package com.fuzzy.config.configsystem.CompactJsonFormat;


import com.fuzzy.config.configsystem.exception.ConfigBuilderException;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by a.kiperku
 * Date: 07.02.2024
 */

public class CompactJsonObject {
    private String name;
    private final Map<Integer, CompactJsonField> fieldsMap = new HashMap<>();
    private int tabs = 1;
    private int order = 0;

    public CompactJsonObject(String name) {
        this.name = name;
    }

    public CompactJsonObject() {
    }

    public Map<Integer, CompactJsonField> getFieldsMap() {
        return fieldsMap;
    }


    public String getName() {
        return name;
    }

    public void setTabs(int tabs) {
        this.tabs = tabs;
    }

    private int getOrder() {
        return order;
    }

    private void setOrder(int order) {
        this.order = order;
    }

    public void putField(String key, Object value, boolean new_line) {
        fieldsMap.put(fieldsMap.size(), new CompactJsonField(key, value, new_line));
    }

    public void putField(String key, Object value) {
        fieldsMap.put(fieldsMap.size(), new CompactJsonField(key, value));
    }

    public void putField(CompactJsonObject value) {
        value.setTabs(tabs + 1);
        incrementOrder(value);
        fieldsMap.put(fieldsMap.size(), new CompactJsonField(value.getName(), value));
    }

    public void putField(String key, CompactJsonObject value) {
        value.setTabs(tabs + 1);
        incrementOrder(value);
        fieldsMap.put(fieldsMap.size(), new CompactJsonField(key, value));
    }

    public void putField(String key, CompactJsonArray value) {
        incrementOrderArray(value);
        value.setTabs(tabs + 1);
        fieldsMap.put(fieldsMap.size(), new CompactJsonField(key, value));
    }

    private void incrementOrderArray(CompactJsonArray compactJsonArray) {
        for (CompactJsonObject object : compactJsonArray.getObjectList()) {
            incrementOrder(object);
        }
    }

    private void incrementOrder(CompactJsonObject compactJsonObject) {
        final int order = compactJsonObject.getOrder();
        compactJsonObject.setOrder(order + 1);
        for (Map.Entry<Integer, CompactJsonField> field : compactJsonObject.getFieldsMap().entrySet()) {
            final Object value = field.getValue().getValue();
            if (value instanceof CompactJsonObject val) {
                incrementOrder(val);
            }
        }
    }

    private void appendFields(StringBuilder result) {
        for (Map.Entry<Integer, CompactJsonField> entry : fieldsMap.entrySet()) {
            final CompactJsonField value = entry.getValue();
            if (value.isNew_line()) {
                result.append("\n").append(" ".repeat((tabs + order) * 2));
            }
            if (entry.getKey() == fieldsMap.size() - 1) {
                result.append(value);
            } else {
                result.append(value).append(", ");
            }
        }
    }

    public String build() {
        StringBuilder result = new StringBuilder();
        result.append("{");
        appendFields(result);
        result.append("\n").append(" ".repeat((Math.max(0, tabs - 1) + order) * 2)).append("}");
        return result.toString();
    }

    public void write(Path path) {
        try (OutputStream outputStream = Files.newOutputStream(path, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
             PrintWriter writer = new PrintWriter(outputStream)) {
            writer.write(build());
        } catch (IOException e) {
            throw new ConfigBuilderException(e);
        }
    }


}
