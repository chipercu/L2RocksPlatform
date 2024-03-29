package com.fuzzy.subsystems.graphql.input;

import com.infomaximum.cluster.graphql.anotation.GraphQLDescription;
import com.infomaximum.cluster.graphql.anotation.GraphQLName;
import com.infomaximum.cluster.graphql.anotation.GraphQLTypeInput;
import org.checkerframework.checker.nullness.qual.NonNull;

@GraphQLTypeInput("input_key_value")
public class GInputKeyValue {

    private final String key;
    private final String value;

    public GInputKeyValue(
            @GraphQLDescription("Ключ")
            @NonNull @GraphQLName("key") String key,
            @GraphQLDescription("Значение")
            @NonNull @GraphQLName("value") String value) {
        this.key = key;
        this.value = value;
    }

    public @NonNull String getKey() {
        return key;
    }

    public @NonNull String getValue() {
        return value;
    }
}