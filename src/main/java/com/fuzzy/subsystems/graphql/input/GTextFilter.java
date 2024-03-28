package com.fuzzy.subsystems.graphql.input;

import com.fuzzy.main.cluster.graphql.anotation.GraphQLDescription;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLName;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLTypeInput;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.Nullable;

@GraphQLTypeInput("text_filter")
public class GTextFilter {

    private final String text;

    public GTextFilter(
            @GraphQLDescription("Значение фильтра")
            @Nullable @GraphQLName("text") String text) {
        this.text = text;
    }

    public @Nullable String getText() {
        return text;
    }

    public boolean isSpecified() {
        return !StringUtils.isEmpty(text);
    }

    @Override
    public @Nullable String toString() {
        return text;
    }
}
