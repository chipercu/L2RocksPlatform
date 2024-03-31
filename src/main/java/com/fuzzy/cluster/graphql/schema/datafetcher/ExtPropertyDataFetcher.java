package com.fuzzy.cluster.graphql.schema.datafetcher;

import com.fuzzy.cluster.graphql.schema.datafetcher.utils.ExtResult;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.PropertyDataFetcher;

/**
 * Created by kris on 01.02.17.
 */
public class ExtPropertyDataFetcher extends PropertyDataFetcher {

    public ExtPropertyDataFetcher(String propertyName) {
        super(propertyName);
    }

    @Override
    public Object get(DataFetchingEnvironment environment) {
        Object result = super.get(environment);
        return ExtResult.get(result);
    }
}
