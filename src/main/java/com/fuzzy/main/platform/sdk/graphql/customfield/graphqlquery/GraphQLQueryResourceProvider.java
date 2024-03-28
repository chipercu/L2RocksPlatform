package com.fuzzy.main.platform.sdk.graphql.customfield.graphqlquery;

import com.fuzzy.main.platform.querypool.QueryPool;
import com.fuzzy.main.platform.querypool.ResourceProviderImpl;
import com.fuzzy.main.platform.sdk.component.Component;

import java.util.HashMap;

/**
 * Для того, что бы ограничить "не правильное" использование ResourceProvider мы унаследоваись в нужном нам пакете
 */
public class GraphQLQueryResourceProvider extends ResourceProviderImpl {

    GraphQLQueryResourceProvider(Component component) {
        super(component);
    }

    @Override
    protected HashMap<String, QueryPool.LockType> getResources() {
        return super.getResources();
    }

}
