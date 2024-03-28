package com.fuzzy.main.platform.component.frontend.engine.network.subscriber;

import com.fuzzy.main.cluster.graphql.executor.struct.GExecutionResult;
import com.fuzzy.main.network.packet.IPacket;
import com.fuzzy.main.network.session.TransportSession;
import com.fuzzy.main.platform.component.frontend.engine.network.protocol.GraphQLSubscriber;
import com.fuzzy.main.platform.component.frontend.engine.service.graphqlrequestexecute.GraphQLRequestExecuteService;
import com.fuzzy.main.platform.component.frontend.engine.service.graphqlrequestexecute.struct.GraphQLResponse;
import graphql.ExecutionResult;
import net.minidev.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow;

public abstract class WebSocketSubscriber implements Flow.Subscriber {

    private final static Logger log = LoggerFactory.getLogger(WebSocketSubscriber.class);

    public final Serializable packetId;
    public final TransportSession transportSession;

    protected final CompletableFuture<IPacket> firstResponseCompletableFuture;

    private Flow.Subscription subscription;

    public WebSocketSubscriber(GraphQLSubscriber graphQLSubscriber, Serializable packetId, TransportSession transportSession) {
        this.packetId = packetId;
        this.transportSession = transportSession;
        graphQLSubscriber.registry(this);

        this.firstResponseCompletableFuture = new CompletableFuture<>();
    }

    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        this.subscription = subscription;
        subscription.request(1);
    }

    public void unSubscriber(){
        if (subscription!=null) {
            subscription.cancel();
        }
    }

    @Override
    public void onNext(Object nextExecutionResult) {
        try {
            GraphQLResponse nextGraphQLResponse = GraphQLRequestExecuteService.buildResponse(
                    new GExecutionResult((ExecutionResult) nextExecutionResult), null
            );

            IPacket responsePacket = buildPacket(nextGraphQLResponse);

            if (firstResponseCompletableFuture.isDone()) {
                try {
                    transportSession.send(responsePacket);
                } catch (Throwable e) {
                    log.error("Exception", e);
                    subscription.cancel();
                }
            } else {
                firstResponseCompletableFuture.complete(responsePacket);
            }

            subscription.request(1);
        } catch (Exception e) {
            transportSession.getUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
        }
    }

    public abstract IPacket buildPacket(GraphQLResponse<JSONObject> nextGraphQLResponse);

    @Override
    public void onError(Throwable throwable) {
        subscription.cancel();
        if (firstResponseCompletableFuture.isDone()) {
            firstResponseCompletableFuture.completeExceptionally(throwable);
        }
        log.debug("OnError", throwable);
    }

    @Override
    public void onComplete() {
        transportSession.getUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), new RuntimeException("Not implemented"));
    }

    public CompletableFuture<IPacket> getFirstResponseCompletableFuture() {
        return firstResponseCompletableFuture;
    }
}
