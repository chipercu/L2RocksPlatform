package com.fuzzy.platform.sdk.subscription;

import com.fuzzy.cluster.graphql.struct.GSubscribeEvent;
import com.fuzzy.platform.querypool.QueryTransaction;
import com.fuzzy.platform.sdk.component.Component;
import com.fuzzy.platform.sdk.context.Context;
import com.fuzzy.platform.sdk.context.ContextTransaction;

public class GraphQLSubscribeEvent {

    private final com.fuzzy.cluster.graphql.executor.subscription.GraphQLSubscribeEvent graphQLSubscribeEvent;

    public GraphQLSubscribeEvent(Component component) {
        this.graphQLSubscribeEvent = new com.fuzzy.cluster.graphql.executor.subscription.GraphQLSubscribeEvent(component);
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
