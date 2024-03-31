package com.fuzzy.cluster.graphql.preparecustomfield;

import graphql.language.Field;
import graphql.language.SourceLocation;
import graphql.schema.DataFetchingEnvironment;

public class PrepareCustomFieldUtils {

    public static String getKeyField(DataFetchingEnvironment dataFetchingEnvironment) {
        if (dataFetchingEnvironment.getMergedField().getFields().size() < 1) {
            throw new RuntimeException("Not support zero field");
        }
        Field field = dataFetchingEnvironment.getMergedField().getFields().get(0);

        return getKeyField(field);
    }

    public static String getKeyField(Field field) {
        SourceLocation sourceLocation = field.getSourceLocation();

        return new StringBuilder()
                .append(sourceLocation.getLine()).append(':')
                .append(sourceLocation.getColumn())
                .toString();
    }


}
