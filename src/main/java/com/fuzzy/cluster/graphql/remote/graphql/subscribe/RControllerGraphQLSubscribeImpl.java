package com.fuzzy.cluster.graphql.remote.graphql.subscribe;

import com.fuzzy.cluster.core.remote.AbstractRController;
import com.fuzzy.cluster.graphql.exception.GraphQLExecutorException;
import com.fuzzy.cluster.graphql.executor.subscription.GraphQLSubscribeEngineImpl;
import com.fuzzy.cluster.graphql.remote.graphql.subscribe.RControllerGraphQLSubscribe;
import com.fuzzy.cluster.graphql.struct.subscribe.SubscribeKey;
import com.fuzzy.cluster.struct.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Optional;


/**
 * Created by kris on 19.11.16.
 */
public class RControllerGraphQLSubscribeImpl<T extends Component> extends AbstractRController<T> implements RControllerGraphQLSubscribe {

    private final static Logger log = LoggerFactory.getLogger(RControllerGraphQLSubscribeImpl.class);

    private final GraphQLSubscribeEngineImpl subscribeEngine;

    public RControllerGraphQLSubscribeImpl(T component, GraphQLSubscribeEngineImpl subscribeEngine) throws GraphQLExecutorException {
        super(component);
        this.subscribeEngine = subscribeEngine;
    }

    @Override
    public void pushEvent(SubscribeKey subscribeKey, Optional<? extends Serializable> value) {
        subscribeEngine.pushEvent(subscribeKey, value);
    }
}
