package com.fuzzy.main.cluster.graphql.executor.subscription;

import com.fuzzy.main.cluster.graphql.struct.subscribe.SubscribeKey;
import io.reactivex.ObservableEmitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArraySet;

public class GraphQLSubscribeEngineImpl implements GraphQLSubscribeEngine {

    protected final static Logger log = LoggerFactory.getLogger(GraphQLSubscribeEngineImpl.class);

    private final ConcurrentMap<SubscribeKey, CopyOnWriteArraySet<ObservableEmitter>> subscriber;

    public GraphQLSubscribeEngineImpl() {
        this.subscriber = new ConcurrentHashMap<>();
    }

    public void pushEvent(SubscribeKey subscribeKey, Optional<? extends Serializable> value) {
        CopyOnWriteArraySet<ObservableEmitter> observables = subscriber.get(subscribeKey);
        if (observables == null || observables.isEmpty()) return;
        for (ObservableEmitter emitter : observables) {
            emitter.onNext(value);
        }
    }

    public void subscribe(UUID nodeRuntimeId, int componentId, byte[] bSubscribeKey, ObservableEmitter observable) {
        SubscribeKey subscribeKey = new SubscribeKey(nodeRuntimeId, componentId, bSubscribeKey);
        subscribe(subscribeKey, observable);
    }

    private void subscribe(SubscribeKey subscribeKey, ObservableEmitter observable) {
        //Подписываемся на разрыв соединения и отписку
        observable.setCancellable(() -> {
            unSubscribe(subscribeKey, observable);
        });

        CopyOnWriteArraySet<ObservableEmitter> observables = subscriber.computeIfAbsent(subscribeKey, s -> new CopyOnWriteArraySet<ObservableEmitter>());
        observables.add(observable);
    }

    private void unSubscribe(SubscribeKey subscribeKey, ObservableEmitter observable) {
        CopyOnWriteArraySet<ObservableEmitter> observables = subscriber.get(subscribeKey);
        observables.remove(observable);
    }
}
