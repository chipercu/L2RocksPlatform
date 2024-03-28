package com.fuzzy.main.platform.sdk.subscription;

import com.fuzzy.main.cluster.graphql.struct.GSubscribeEvent;
import com.fuzzy.main.platform.querypool.QueryTransaction;
import com.fuzzy.main.platform.sdk.component.Component;
import com.fuzzy.main.platform.sdk.context.Context;
import com.fuzzy.main.platform.sdk.context.ContextTransaction;

public class GraphQLSubscribeEvent {

    private final com.fuzzy.main.cluster.graphql.executor.subscription.GraphQLSubscribeEvent graphQLSubscribeEvent;

    public GraphQLSubscribeEvent(Component component) {
        this.graphQLSubscribeEvent = new com.fuzzy.main.cluster.graphql.executor.subscription.GraphQLSubscribeEvent(component);
    }

    public void push(GSubscribeEvent<?> event, Context context) {
        if (context instanceof ContextTransaction &&
                !((ContextTransaction) context).getTransaction().closed()) {
            ContextTransaction contextTransaction = (ContextTransaction) context;
            push(event, contextTransaction.getTransaction());
        } else {
            graphQLSubscribeEvent.pushEvent(event);
        }
    }

    public void push(GSubscribeEvent<?> event, QueryTransaction transaction) {
        transaction.addCommitListener(
                event.getSubscribeValue().subscribeKey, () -> graphQLSubscribeEvent.pushEvent(event));
    }
}
