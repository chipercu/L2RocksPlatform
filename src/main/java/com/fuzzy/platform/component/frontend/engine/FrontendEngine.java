package com.fuzzy.platform.component.frontend.engine;

import com.fuzzy.cluster.core.remote.controller.clusterfile.RControllerClusterFileImpl;
import com.fuzzy.cluster.core.service.transport.executor.ComponentExecutorTransportImpl;
import com.fuzzy.cluster.graphql.GraphQLEngine;
import com.fuzzy.cluster.graphql.executor.subscription.GraphQLSubscribeEngine;
import com.fuzzy.network.Network;
import com.fuzzy.network.builder.BuilderNetwork;
import com.fuzzy.network.builder.BuilderTransport;
import com.fuzzy.network.exception.NetworkException;
import com.fuzzy.network.transport.http.builder.HttpBuilderTransport;
import com.fuzzy.platform.Platform;
import com.fuzzy.platform.component.frontend.engine.authorize.RequestAuthorize;
import com.fuzzy.platform.component.frontend.engine.controller.Controllers;
import com.fuzzy.platform.component.frontend.engine.filter.FilterGRequest;
import com.fuzzy.platform.component.frontend.engine.service.graphqlrequestexecute.GraphQLRequestExecuteService;
import com.fuzzy.platform.component.frontend.engine.service.requestcomplete.RequestCompleteCallbackService;
import com.fuzzy.platform.component.frontend.engine.service.statistic.StatisticService;
import com.fuzzy.platform.component.frontend.engine.service.statistic.StatisticServiceImpl;
import com.fuzzy.platform.component.frontend.engine.uploadfile.FrontendMultipartSource;
import com.fuzzy.platform.component.frontend.request.graphql.builder.GraphQLRequestBuilder;
import com.fuzzy.platform.component.frontend.request.graphql.builder.impl.DefaultGraphQLRequestBuilder;
import com.fuzzy.platform.sdk.component.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FrontendEngine implements AutoCloseable {

    private final Builder builder;

    private final Platform platform;
    private final Component component;

    private final GraphQLEngine graphQLEngine;
    private final GraphQLSubscribeEngine graphQLSubscribeEngine;

    private final RequestAuthorize.Builder requestAuthorizeBuilder;
    private final FrontendMultipartSource frontendMiltipartSource;

    private final GraphQLRequestBuilder graphQLRequestBuilder;

    private GraphQLRequestExecuteService graphQLRequestExecuteService;

    private List<FilterGRequest> filterGRequests;

    private Network network;

    private final StatisticService statisticService;
    private final RequestCompleteCallbackService requestCompleteCallbackService;

    private final Controllers controllers;

    private FrontendEngine(Builder builder) {
        this.builder = builder;

        this.platform = builder.platform;
        this.component = builder.component;

        this.graphQLEngine = builder.platform.getGraphQLEngine();
        this.graphQLSubscribeEngine = graphQLEngine.buildSubscribeEngine();

        this.filterGRequests = (builder.filterGRequests.isEmpty())? null : Collections.unmodifiableList(builder.filterGRequests);

        this.requestAuthorizeBuilder = builder.requestAuthorizeBuilder;

        this.frontendMiltipartSource = new FrontendMultipartSource(builder.component);

        this.graphQLRequestBuilder = builder.graphQLRequestBuilder.build(frontendMiltipartSource);

        this.statisticService = builder.statisticService;
        this.requestCompleteCallbackService = new RequestCompleteCallbackService(
                builder.builderNetwork.getUncaughtExceptionHandler()
        );

        //Регистрируем подписчиков
        for (BuilderTransport builderTransport: builder.builderNetwork.getBuilderTransports()) {
            if (builderTransport instanceof HttpBuilderTransport) {
                HttpBuilderTransport httpBuilderTransport = (HttpBuilderTransport) builderTransport;

                httpBuilderTransport.addListener(((StatisticServiceImpl) statisticService).getListener());
                httpBuilderTransport.addListener(requestCompleteCallbackService);
            } else {
                throw new RuntimeException("Not support builder transport: " + builderTransport);
            }
        }

        this.controllers = new Controllers(this);
    }

    public ComponentExecutorTransportImpl.Builder registerControllers(ComponentExecutorTransportImpl.Builder builder) {
        return builder
                .withRemoteController(
                        platform.getGraphQLEngine().buildRemoteControllerGraphQLSubscribe(component, graphQLSubscribeEngine)
                )
                .withRemoteController(
                        new RControllerClusterFileImpl.Builder(component, frontendMiltipartSource).build()//Обработчик ClusterFiles
                );
    }

    public void start() throws NetworkException {
        graphQLRequestExecuteService = new GraphQLRequestExecuteService(
                component,
                platform.getQueryPool(),
                graphQLEngine, graphQLSubscribeEngine,
                requestAuthorizeBuilder,
                platform.getUncaughtExceptionHandler()
        );

        network = builder.builderNetwork.build();
    }

    public Network getNetwork() {
        return network;
    }

    public FrontendMultipartSource getFrontendMiltipartSource() {
        return frontendMiltipartSource;
    }

    public GraphQLRequestBuilder getGraphQLRequestBuilder() {
        return graphQLRequestBuilder;
    }

    public List<FilterGRequest> getFilterGRequests() {
        return filterGRequests;
    }

    public GraphQLRequestExecuteService getGraphQLRequestExecuteService() {
        return graphQLRequestExecuteService;
    }

    public GraphQLSubscribeEngine getGraphQLSubscribeEngine() {
        return graphQLSubscribeEngine;
    }

    public Controllers getControllers() {
        return controllers;
    }

    public StatisticService getStatisticService() {
        return statisticService;
    }

    @Override
    public void close() {
        if (network != null) {
            network.close();
        }
    }

    public static class Builder {

        private final Platform platform;
        private final Component component;

        private BuilderNetwork builderNetwork;
        private RequestAuthorize.Builder requestAuthorizeBuilder;

        private GraphQLRequestBuilder.Builder graphQLRequestBuilder = new DefaultGraphQLRequestBuilder.Builder();

        private List<FilterGRequest> filterGRequests = new ArrayList<>();

        private StatisticService statisticService = new StatisticServiceImpl();

        public Builder(Platform platform, Component component) {
            this.platform = platform;
            this.component = component;
        }

        public Builder withBuilderNetwork(BuilderNetwork builderNetwork) {
            this.builderNetwork = builderNetwork;
            return this;
        }

        public Builder withRequestAuthorizeBuilder(RequestAuthorize.Builder requestAuthorizeBuilder) {
            this.requestAuthorizeBuilder = requestAuthorizeBuilder;
            return this;
        }

        public Builder withGraphQLRequestBuilder(GraphQLRequestBuilder.Builder graphQLRequestBuilder) {
            this.graphQLRequestBuilder = graphQLRequestBuilder;
            return this;
        }

        public Builder withFilterGRequest(FilterGRequest filter) {
            filterGRequests.add(filter);
            return this;
        }

        public Builder withStatisticService(StatisticService statisticService) {
            this.statisticService = statisticService;
            return this;
        }

        public FrontendEngine build() {
            return new FrontendEngine(this);
        }
    }
}
