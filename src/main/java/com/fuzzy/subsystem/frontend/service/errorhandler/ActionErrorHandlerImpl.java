package com.fuzzy.subsystem.frontend.service.errorhandler;

import com.fuzzy.main.Subsystems;
import com.fuzzy.platform.component.frontend.engine.service.errorhandler.ActionErrorHandler;
import com.fuzzy.platform.exception.GraphQLWrapperPlatformException;
import com.fuzzy.subsystem.frontend.FrontendSubsystem;
import com.fuzzy.subsystem.frontend.controller.http.IndexController;
import com.fuzzy.subsystems.exception.GeneralExceptionBuilder;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

public class ActionErrorHandlerImpl implements ActionErrorHandler {

    @Override
    public void handlerNotFound(HttpServletResponse response) throws IOException {
        IndexController.responseIndexHtml(response);
    }

    @Override
    public ResponseEntity<byte[]> handlerServiceUnavailable() {
        FrontendSubsystem frontEndSubSystem = Subsystems.getInstance().getCluster().getAnyLocalComponent(FrontendSubsystem.class);

        GraphQLWrapperPlatformException graphQLWrapperPlatformException = new GraphQLWrapperPlatformException(GeneralExceptionBuilder.buildServerTimeoutException());
        return frontEndSubSystem.getControllers().http.graphQLController.buildResponseEntity(null, graphQLWrapperPlatformException);
    }
}
