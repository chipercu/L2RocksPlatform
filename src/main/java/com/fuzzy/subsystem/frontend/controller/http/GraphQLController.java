package com.fuzzy.subsystem.frontend.controller.http;

import com.infomaximum.main.Subsystems;
import com.fuzzy.subsystem.frontend.FrontendSubsystem;
import com.fuzzy.subsystem.frontend.controller.http.utils.RevalidateHashFromWebClientUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

/**
 * Created by kris on 26.12.16.
 */
@Controller
@RequestMapping("/")
public class GraphQLController {

    private final static Logger log = LoggerFactory.getLogger(GraphQLController.class);

    @RequestMapping(value = "/graphql")
    public CompletableFuture<ResponseEntity> execute(HttpServletRequest request) {
        FrontendSubsystem frontEndSubSystem = Subsystems.getInstance().getCluster().getAnyLocalComponent(FrontendSubsystem.class);

        return frontEndSubSystem.getControllers().http.graphQLController
                .execute(request)
                .thenApply(responseEntity -> {

                    HttpHeaders headers = new HttpHeaders();
                    headers.setAll(responseEntity.getHeaders().toSingleValueMap());
                    try {
                        headers.set("Revalidate-Hash", RevalidateHashFromWebClientUtils.getRevalidateHash());
                    } catch (IOException e) {
                        //Падать не стоит: возможно нам понадобится аварийное ручное выполнение graphql-запросов
                        log.warn("Error count revalidate_hash", e);
                    }

                    return new ResponseEntity(
                            responseEntity.getBody(),
                            headers,
                            responseEntity.getStatusCode()
                    );
                });
    }
}



