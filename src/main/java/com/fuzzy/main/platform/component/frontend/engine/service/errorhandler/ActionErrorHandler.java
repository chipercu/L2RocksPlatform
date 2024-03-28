package com.fuzzy.main.platform.component.frontend.engine.service.errorhandler;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

public interface ActionErrorHandler {

    void handlerNotFound(HttpServletResponse response) throws IOException;

    ResponseEntity<byte[]> handlerServiceUnavailable();
}
