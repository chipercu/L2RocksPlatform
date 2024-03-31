package com.fuzzy.platform.component.frontend.engine.controller.http.graphql;

import com.fuzzy.platform.component.frontend.engine.service.graphqlrequestexecute.struct.GExecutionStatistics;
import com.fuzzy.cluster.graphql.executor.struct.GSubscriptionPublisher;
import com.fuzzy.cluster.graphql.struct.GRequest;
import com.fuzzy.cluster.graphql.subscription.SingleSubscriber;
import com.fuzzy.platform.component.frontend.engine.FrontendEngine;
import com.fuzzy.platform.component.frontend.engine.filter.FilterGRequest;
import com.fuzzy.platform.component.frontend.engine.service.graphqlrequestexecute.GraphQLRequestExecuteService;
import com.fuzzy.platform.component.frontend.engine.service.graphqlrequestexecute.struct.GraphQLResponse;
import com.fuzzy.platform.component.frontend.engine.service.requestcomplete.RequestCompleteCallbackService;
import com.fuzzy.platform.component.frontend.engine.service.statistic.StatisticService;
import com.fuzzy.platform.component.frontend.request.graphql.GraphQLRequest;
import com.fuzzy.platform.component.frontend.utils.GRequestUtils;
import com.fuzzy.platform.exception.GraphQLWrapperPlatformException;
import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.sdk.graphql.out.GOutputFile;
import com.fuzzy.platform.utils.EscapeUtils;
import jakarta.servlet.http.HttpServletRequest;
import net.minidev.json.JSONObject;
import org.eclipse.jetty.server.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.PathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;

public class GraphQLController {

    private final static Logger log = LoggerFactory.getLogger(GraphQLController.class);

    private final FrontendEngine frontendEngine;

    public GraphQLController(FrontendEngine frontendEngine) {
        this.frontendEngine = frontendEngine;
    }

    public CompletableFuture<ResponseEntity> execute(HttpServletRequest request) {
        GraphQLRequest graphQLRequest;
        try {
            graphQLRequest = frontendEngine.getGraphQLRequestBuilder().build(request);
        } catch (PlatformException e) {
            GraphQLWrapperPlatformException graphQLWrapperSubsystemException = GraphQLRequestExecuteService.coercionGraphQLPlatformException(e);
            return CompletableFuture.completedFuture(buildResponseEntity(null, graphQLWrapperSubsystemException));
        }

        GRequest gRequest = graphQLRequest.getGRequest();

        log.debug("Request {}, xTraceId: {}, remote address: {}, query: {}",
                GRequestUtils.getTraceRequest(gRequest),
                gRequest.getXTraceId(),
                gRequest.getRemoteAddress().endRemoteAddress,
                gRequest.getQuery().replaceAll("[\\s\\t\\r\\n]+", " ")
        );

        if (frontendEngine.getFilterGRequests() != null) {
            try {
                for (FilterGRequest filter : frontendEngine.getFilterGRequests()) {
                    filter.filter(gRequest);
                }
            } catch (PlatformException e) {
                GraphQLWrapperPlatformException graphQLWrapperSubsystemException = GraphQLRequestExecuteService.coercionGraphQLPlatformException(e);
                return CompletableFuture.completedFuture(buildResponseEntity(null, graphQLWrapperSubsystemException));
            }
        }


        return frontendEngine.getGraphQLRequestExecuteService().execute(gRequest)
                .whenComplete((graphQLResponse, throwable) -> {//Встраиваемся в поток, и прокидавыем все(включая ошибки) дальше
                    graphQLRequest.close();//Все чистим
                })
                .thenCompose(out -> {//Возвращаем так же future
                    Object data = out.data;
                    if (data instanceof JSONObject) {
                        return CompletableFuture.completedFuture(
                                buildResponseEntity(gRequest, out)
                        );
                    } else if (data instanceof GSubscriptionPublisher completionPublisher) {
                        SingleSubscriber singleSubscriber = new SingleSubscriber();
                        completionPublisher.subscribe(singleSubscriber);
                        return singleSubscriber.getCompletableFuture().thenApply(executionResult -> {
                            GraphQLResponse graphQLResponse =
                                    GraphQLRequestExecuteService.buildResponse(executionResult, null);
                            return buildResponseEntity(gRequest, graphQLResponse);
                        });
                    } else if (data instanceof GOutputFile) {
                        GOutputFile gOutputFile = (GOutputFile) data;

                        long fileSize = gOutputFile.getSize();

                        HttpHeaders header = new HttpHeaders();
                        header.add("Content-Disposition", "attachment; filename*=UTF-8''" + EscapeUtils.escapeFileNameFromContentDisposition(gOutputFile.fileName));
                        header.setContentType(MediaType.valueOf(gOutputFile.mimeType.value));
                        header.setContentLength(fileSize);
                        if (gOutputFile.cache) {
                            header.setCacheControl("public, max-age=86400");
                        } else {
                            header.setCacheControl("no-cache, no-store, must-revalidate");
                            header.setPragma("no-cache");
                            header.setExpires(0);
                        }

                        //Помечаем инфу для сервиса сбора статистики
                        request.setAttribute(StatisticService.ATTRIBUTE_DOWNLOAD_FILE_SIZE, fileSize);

                        if (gOutputFile.temp) {
                            //Добавляем callback, что бы после отдачи файла, его удалить
                            request.setAttribute(
                                    RequestCompleteCallbackService.ATTRIBUTE_COMPLETE_REQUEST_CALLBACK,
                                    new RequestCompleteCallbackService.Callback() {
                                        @Override
                                        public void exec(Request request) {
                                            try {
                                                Files.delete(Paths.get(gOutputFile.uri));
                                            } catch (IOException e) {
                                                log.error("Exception clear temp file", e);//Падать из-за этого не стоит
                                            }
                                        }
                                    }
                            );
                        }

                        Object body;
                        if (gOutputFile.body != null) {
                            body = gOutputFile.body;
                        } else {
                            Path pathOutputFile = Paths.get(gOutputFile.uri);
                            body = new PathResource(pathOutputFile);
                        }

                        return CompletableFuture.completedFuture(
                                new ResponseEntity(body, header, HttpStatus.OK)
                        );
                    } else {
                        throw new RuntimeException("Not support type out: " + out);
                    }
                });
    }

    public ResponseEntity buildResponseEntity(GRequest gRequest, GraphQLWrapperPlatformException graphQLWrapperSubsystemException) {
        GraphQLRequestExecuteService graphQLRequestExecuteService = frontendEngine.getGraphQLRequestExecuteService();

        GraphQLResponse<JSONObject> graphQLResponse = graphQLRequestExecuteService.buildResponse(graphQLWrapperSubsystemException);
        return buildResponseEntity(gRequest, graphQLResponse);
    }

    private ResponseEntity buildResponseEntity(GRequest gRequest, GraphQLResponse<JSONObject> graphQLResponse) {
        HttpStatus httpStatus;
        JSONObject out = new JSONObject();
        if (!graphQLResponse.error) {
            httpStatus = HttpStatus.OK;
            out.put(GraphQLRequestExecuteService.JSON_PROP_DATA, graphQLResponse.data);
        } else {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            out.put(GraphQLRequestExecuteService.JSON_PROP_ERROR, graphQLResponse.data);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setCacheControl("no-cache, no-store, must-revalidate");
        headers.setPragma("no-cache");
        headers.setExpires(0);

        String sout = out.toString();
        byte[] bout = sout.getBytes(StandardCharsets.UTF_8);

        GExecutionStatistics statistics = graphQLResponse.statistics;
        if (statistics == null) {
            log.debug("Request {}, http code: {}, response: {}",
                    (gRequest != null) ? GRequestUtils.getTraceRequest(gRequest) : null,
                    httpStatus.value(),
                    (graphQLResponse.error) ? sout : "hide(" + bout.length + " bytes)"
            );
        } else {
            log.debug("Request {}, auth: {}, priority: {}, wait: {}, exec: {} ({}), http code: {}, response: {}{}",
                    (gRequest != null) ? GRequestUtils.getTraceRequest(gRequest) : null,
                    statistics.authContext(),
                    statistics.priority(),
                    statistics.timeWait(),
                    statistics.timeExec(), statistics.timeAuth(),
                    httpStatus.value(),
                    (graphQLResponse.error) ? sout : "hide(" + bout.length + " bytes)",
                    (statistics.accessDenied() != null)?", access_denied: [ " + statistics.accessDenied() + "]": ""
            );
        }

        return new ResponseEntity(bout, headers, httpStatus);
    }

}
