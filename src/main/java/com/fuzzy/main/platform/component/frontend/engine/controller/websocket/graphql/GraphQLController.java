package com.fuzzy.main.platform.component.frontend.engine.controller.websocket.graphql;

import com.fuzzy.main.cluster.graphql.executor.struct.GSubscriptionPublisher;
import com.fuzzy.main.cluster.graphql.struct.GRequest;
import com.fuzzy.main.network.mvc.ResponseEntity;
import com.fuzzy.main.network.protocol.standard.packet.RequestPacket;
import com.fuzzy.main.network.protocol.standard.packet.ResponsePacket;
import com.fuzzy.main.network.protocol.standard.packet.TargetPacket;
import com.fuzzy.main.network.protocol.standard.session.StandardTransportSession;
import com.fuzzy.main.network.struct.RemoteAddress;
import com.fuzzy.main.platform.component.frontend.engine.FrontendEngine;
import com.fuzzy.main.platform.component.frontend.engine.filter.FilterGRequest;
import com.fuzzy.main.platform.component.frontend.engine.network.protocol.GraphQLSubscriber;
import com.fuzzy.main.platform.component.frontend.engine.network.protocol.standard.subscriber.WebSocketStandardSubscriber;
import com.fuzzy.main.platform.component.frontend.engine.service.graphqlrequestexecute.GraphQLRequestExecuteService;
import com.fuzzy.main.platform.component.frontend.engine.service.graphqlrequestexecute.struct.GraphQLResponse;
import com.fuzzy.main.platform.component.frontend.request.GRequestWebSocket;
import com.fuzzy.main.platform.exception.GraphQLWrapperPlatformException;
import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.sdk.exception.GeneralExceptionBuilder;
import net.minidev.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import java.io.Serializable;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class GraphQLController {

    private final static Logger log = LoggerFactory.getLogger(GraphQLController.class);

    private final FrontendEngine frontendEngine;
    private final GraphQLSubscriber graphQLSubscriber;

    public GraphQLController(FrontendEngine frontendEngine) {
        this.frontendEngine = frontendEngine;
        this.graphQLSubscriber = new GraphQLSubscriber();
    }

    public CompletableFuture<ResponseEntity> exec(StandardTransportSession transportSession, TargetPacket targetPacket) {
        GraphQLRequestExecuteService graphQLRequestExecuteService = frontendEngine.getGraphQLRequestExecuteService();

        JSONObject data = targetPacket.getData();

        if (data == null) {
            return CompletableFuture.completedFuture(ResponseEntity.error(HttpStatus.BAD_REQUEST.value()));
        }
        String query = data.getAsString("query");
        if (query == null || query.trim().isEmpty()) {
            GraphQLWrapperPlatformException graphQLWrapperSubsystemException = new GraphQLWrapperPlatformException(
                    GeneralExceptionBuilder.buildEmptyValueException("query")
            );
            JSONObject out = graphQLRequestExecuteService.buildResponse(graphQLWrapperSubsystemException).data;
            return CompletableFuture.completedFuture(ResponseEntity.error(out));
        }

        HashMap<String, Serializable> queryVariables;
        if (data.containsKey("variables")) {
            queryVariables = new HashMap<>((Map<String, Serializable>) data.get("variables"));
        } else {
            queryVariables = new HashMap<>();
        }

        String operationName = data.getAsString("operation_name");

        Map<String, String> parameters = new HashMap<>();

        RemoteAddress remoteAddress = transportSession.buildRemoteAddress();
        String xTraceId = transportSession.getXTraceId();

        GRequestWebSocket gRequest = new GRequestWebSocket(
                Instant.now(),
                new GRequest.RemoteAddress(remoteAddress.getRawRemoteAddress(), remoteAddress.getEndRemoteAddress()),
                query, queryVariables, operationName,
                xTraceId,
                transportSession.getSession().getUuid(),
                parameters,
                null
        );


        if (frontendEngine.getFilterGRequests() != null) {
            try {
                for (FilterGRequest filter : frontendEngine.getFilterGRequests()) {
                    filter.filter(gRequest);
                }
            } catch (PlatformException e) {
                GraphQLWrapperPlatformException graphQLWrapperSubsystemException = new GraphQLWrapperPlatformException(e);
                JSONObject out = graphQLRequestExecuteService.buildResponse(graphQLWrapperSubsystemException).data;
                return CompletableFuture.completedFuture(ResponseEntity.error(out));
            }
        }


        return frontendEngine.getGraphQLRequestExecuteService()
                .execute(gRequest)
                .thenCompose(graphQLResponse -> buildResponseEntity(graphQLResponse, transportSession, targetPacket));
    }

    private CompletableFuture<ResponseEntity> buildResponseEntity(
            GraphQLResponse graphQLResponse,
            StandardTransportSession transportSession, TargetPacket packet
    ) {
        if (graphQLResponse.error) {
            return CompletableFuture.completedFuture(
                    ResponseEntity.error((JSONObject) graphQLResponse.data)
            );
        } else {
            Object data = graphQLResponse.data;
            if (data instanceof JSONObject) {
                return CompletableFuture.completedFuture(
                        ResponseEntity.success((JSONObject) graphQLResponse.data)
                );
            } else if (data instanceof GSubscriptionPublisher completionPublisher) {
                WebSocketStandardSubscriber websocketSubscriber = new WebSocketStandardSubscriber(graphQLSubscriber, transportSession, (RequestPacket) packet);
                completionPublisher.subscribe(websocketSubscriber);
                return websocketSubscriber.getFirstResponseCompletableFuture()
                        .thenApply(iPacket -> convert((ResponsePacket) iPacket));
            } else {
                throw new RuntimeException("Not support type out: " + data);
            }
        }
    }

    private static ResponseEntity convert(ResponsePacket responsePacket) {
        if (responsePacket.getCode() == ResponseEntity.RESPONSE_CODE_OK) {
            return ResponseEntity.success(responsePacket.getData());
        } else {
            return ResponseEntity.error(responsePacket.getData());
        }
    }
}
