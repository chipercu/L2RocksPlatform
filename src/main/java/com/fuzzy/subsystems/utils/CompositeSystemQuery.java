package com.fuzzy.subsystems.utils;

import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.ResourceProvider;
import com.fuzzy.platform.sdk.context.ContextTransaction;
import com.fuzzy.platform.sdk.struct.querypool.QuerySystem;

import java.util.List;

public class CompositeSystemQuery extends QuerySystem<Void> {

    private List<QuerySystem<Void>> queries;

    public CompositeSystemQuery(List<QuerySystem<Void>> queries) {
        this.queries = queries;
    }

    @Override
    public void prepare(ResourceProvider resources) throws PlatformException {
        for (QuerySystem<Void> query : queries) {
            query.prepare(resources);
        }
    }

    @Override
    public Void execute(ContextTransaction context) throws PlatformException {
        for (QuerySystem<Void> query : queries) {
            query.execute(context);
        }
        return null;
    }
}
