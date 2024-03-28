package com.fuzzy.main.cluster.graphql.executor.struct;

import graphql.ExecutionResult;
import graphql.execution.reactive.SubscriptionPublisher;
import org.reactivestreams.FlowAdapters;
import org.reactivestreams.Subscriber;

import java.util.concurrent.Flow;

public class GSubscriptionPublisher<D, U> implements Flow.Publisher<D> {

    private final SubscriptionPublisher sp;

    public GSubscriptionPublisher(SubscriptionPublisher sp) {
        this.sp = sp;
    }

    @Override
    public void subscribe(Flow.Subscriber<? super D> subscriber) {
        sp.subscribe((Subscriber<? super ExecutionResult>) FlowAdapters.toSubscriber(subscriber));
    }
}
