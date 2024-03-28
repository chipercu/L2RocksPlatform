package com.fuzzy.main.platform.component.frontend.engine.controller;

import com.fuzzy.main.platform.component.frontend.engine.FrontendEngine;
import com.fuzzy.main.platform.component.frontend.engine.controller.http.graphql.GraphQLController;

public class Controllers {

    public final Http http;
    public final Websocket websocket;

    public Controllers(FrontendEngine frontendEngine) {
        this.http = new Http(frontendEngine);
        this.websocket = new Websocket(frontendEngine);
    }

    public class Http {

        public final GraphQLController graphQLController;

        public Http(FrontendEngine frontendEngine) {
            this.graphQLController = new GraphQLController(frontendEngine);
        }

    }

    public class Websocket {

        public final com.fuzzy.main.platform.component.frontend.engine.controller.websocket.graphql.GraphQLController graphQLController;

        public Websocket(FrontendEngine frontendEngine) {
            this.graphQLController = new com.fuzzy.main.platform.component.frontend.engine.controller.websocket.graphql.GraphQLController(frontendEngine);
        }

    }
}
