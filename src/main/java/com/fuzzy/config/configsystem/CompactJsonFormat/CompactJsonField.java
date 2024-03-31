package com.fuzzy.config.configsystem.CompactJsonFormat;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Created by a.kiperku
 * Date: 07.02.2024
 */

public class CompactJsonField {

    private final String key;
    private final Object value;
    private final boolean new_line;

    public CompactJsonField(String key, Object value, boolean new_line) {
        this.key = key;
        this.value = value;
        this.new_line = new_line;
    }
    public CompactJsonField(String key, Object value) {
        this.key = key;
        this.value = value;
        this.new_line = true;
    }

    public boolean isNew_line() {
        return new_line;
    }

    public Object getValue() {
        return value;
    }

    private void appendStringArr(StringBuilder result, String[] arr){
        final String collect = Arrays.stream(arr).map(s -> "\"" + s + "\"").collect(Collectors.joining(","));
        result.append("[").append(collect).append("]");
    }
    private void appendIntArr(StringBuilder result, int[] arr){
        int max = arr.length - 1;
        result.append("[");
        for (int i = 0; i < arr.length; i++) {
            if (i == max){
                result.append(arr[i]);
            }else {
                result.append(arr[i]).append(", ");
            }
        }
        result.append("]");
    }
    private void appendLongArr(StringBuilder result, long[] arr){
        int max = arr.length - 1;
        result.append("[");
        for (int i = 0; i < arr.length; i++) {
            if (i == max){
                result.append(arr[i]);
            }else {
                result.append(arr[i]).append(", ");
            }
        }
        result.append("]");
    }
    private void appendDoubleArr(StringBuilder result, double[] arr){
        int max = arr.length - 1;
        result.append("[");
        for (int i = 0; i < arr.length; i++) {
            if (i == max){
                result.append(arr[i]);
            }else {
                result.append(arr[i]).append(", ");
            }
        }
        result.append("]");
    }
    private void appendBooleanArr(StringBuilder result, boolean[] arr){
        int max = arr.length - 1;
        result.append("[");
        for (int i = 0; i < arr.length; i++) {
            if (i == max){
                result.append(arr[i]);
            }else {
                result.append(arr[i]).append(", ");
            }
        }
        result.append("]");
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder().append("\"").append(key).append("\": ");
        if (value instanceof String){
            result.append("\"").append(value).append("\"");
        } else if (value instanceof String[] strings) {
            appendStringArr(result, strings);
        } else if (value instanceof int[] ints) {
            appendIntArr(result, ints);
        } else if (value instanceof long[] longs) {
            appendLongArr(result, longs);
        } else if (value instanceof double[] doubles) {
            appendDoubleArr(result, doubles);
        } else if (value instanceof boolean[] booleans) {
            appendBooleanArr(result, booleans);
        } else if (value instanceof CompactJsonObject object) {
            result.append(object.build());
        } else if (value instanceof CompactJsonArray object) {
            result.append(object.build());
        } else {
            result.append(value);
        }
        return result.toString();
    }
}
