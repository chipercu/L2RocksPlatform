package com.fuzzy.config.configsystem.CompactJsonFormat;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by a.kiperku
 * Date: 07.02.2024
 */

public class CompactJsonArray {

    private final List<CompactJsonObject> objectList = new ArrayList<>();
    private int tabs = 1;

    public void add(CompactJsonObject compactJsonObject){
        objectList.add(compactJsonObject);
    }

    public List<CompactJsonObject> getObjectList() {
        return objectList;
    }

    public String build() {
        StringBuilder result = new StringBuilder();
        result.append("[");

        int i = 0;
        for (CompactJsonObject object: objectList){
            result.append(object.build());
            if (i != objectList.size() - 1){
                result.append(",");
            }
            i++;
        }

        result.append("\n").append(" ".repeat((Math.max(0, tabs + 4)) * 2)).append("]");
        return result.toString();
    }

    public void setTabs(int tabs) {
        this.tabs = tabs;
    }
}
