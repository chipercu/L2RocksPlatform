package com.fuzzy.main.platform.component.frontend.engine.service.errorhandler;

import com.fuzzy.main.platform.utils.ExceptionUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.eclipse.jetty.io.EofException;
import org.eclipse.jetty.server.Dispatcher;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.ErrorHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.util.NestedServletException;

import java.util.List;

public class PlatformErrorHandler extends ErrorHandler {

    private final static Logger log = LoggerFactory.getLogger(PlatformErrorHandler.class);

    private final ActionErrorHandler actionErrorHandler;
    private final Thread.UncaughtExceptionHandler uncaughtExceptionHandler;

    public PlatformErrorHandler(
            ActionErrorHandler actionErrorHandler,
            Thread.UncaughtExceptionHandler uncaughtExceptionHandler
    ) {
        this.actionErrorHandler = actionErrorHandler;
        this.uncaughtExceptionHandler = uncaughtExceptionHandler;
    }

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) {
        try {
            if (response.getStatus() == HttpStatus.NOT_FOUND.value()) {
                actionErrorHandler.handlerNotFound(response);
            } else if (response.getStatus() == HttpStatus.SERVICE_UNAVAILABLE.value()) {
                ResponseEntity<byte[]> responseEntity = actionErrorHandler.handlerServiceUnavailable();

                response.setStatus(responseEntity.getStatusCodeValue());
                responseEntity.getHeaders().forEach((name, values) -> {
                    response.setHeader(name, values.get(0));
                    for (int i = 1; i < values.size(); i++) {
                        response.addHeader(name, values.get(i));
                    }
                });
                response.getOutputStream().write(responseEntity.getBody());

                log.error("SERVICE_UNAVAILABLE", (Throwable) request.getAttribute(Dispatcher.ERROR_EXCEPTION));
            } else if (response.getStatus() >= 400 && response.getStatus() < 500) {
                //Ошибки построения запроса клиентом - игнорируем и прокидываем ответ напрямую
            } else {
                Throwable throwable = (Throwable) request.getAttribute(Dispatcher.ERROR_EXCEPTION);
                if (throwable == null) {
                    throw new RuntimeException("Unknown state errorHandler, response status:" + response.getStatus() + ", " + response);
                } else {
                    throw throwable;
                }
            }
        } catch (Throwable ex) {
            processingException(ex, baseRequest, response);
        }
    }

    private void processingException(Throwable ex, Request baseRequest, HttpServletResponse response) {
        List<Throwable> chainThrowables = ExceptionUtils.getThrowableList(ex);

        if (ex instanceof EofException) {
            //Обычный разрыв соединения во время передачи данных
            return;
        } else if (chainThrowables.size() == 4
                && chainThrowables.get(0) instanceof jakarta.servlet.ServletException
                && chainThrowables.get(1) instanceof jakarta.servlet.ServletException
                && chainThrowables.get(2) instanceof org.springframework.web.multipart.MultipartException
                && chainThrowables.get(3) instanceof java.io.IOException
        ) {
            //Missing initial multi part boundary
            return;
        } else if (chainThrowables.size() == 5
                && chainThrowables.get(0) instanceof jakarta.servlet.ServletException
                && chainThrowables.get(1) instanceof jakarta.servlet.ServletException
                && chainThrowables.get(2) instanceof org.springframework.web.multipart.MultipartException
                && chainThrowables.get(3) instanceof java.io.IOException
                && chainThrowables.get(4) instanceof java.util.concurrent.TimeoutException
        ) {
            //поставил низкую скорость, начал импортировать в пространство таблицу, "выдернул" сетевой кабель.
            return;
        } else if (chainThrowables.size() == 4
                && chainThrowables.get(0) instanceof jakarta.servlet.ServletException
                && chainThrowables.get(1) instanceof jakarta.servlet.ServletException
                && chainThrowables.get(2) instanceof org.springframework.web.multipart.MultipartException
                && chainThrowables.get(3) instanceof org.eclipse.jetty.io.EofException
        ) {
            //поставил на клиенте медленную скорость, выбрал файл для импорта в то же пространство нажал кнопку загрузки и сразу же кнопку отмены.
            return;
        } else if (chainThrowables.size() == 3
                && chainThrowables.get(1) instanceof NestedServletException
                && chainThrowables.get(2) instanceof IllegalArgumentException
        ) {
            //Exception в случае невалидного url, например:
            // _build/static/%uff0e%uff0e/%uff0e%uff0e/%uff0e%uff0e/%uff0e%uff0e/%uff0e%uff0e/%uff0e%uff0e/%uff0e%uff0e/%uff0e%uff0e/%uff0e%uff0e/%uff0e%uff0e/%uff0e%uff0e/%uff0e%uff0e/%uff0e%uff0e/%uff0e%uff0e/%uff0e%uff0e/%uff0e%uff0e//etc/passwd
            return;
        } else if (chainThrowables.size() == 3
                && chainThrowables.get(0) instanceof ServletException
                && chainThrowables.get(1) instanceof MultipartException
                && chainThrowables.get(2) instanceof EofException
        ) {
            //Разрыв соединение
            return;
        } else if (chainThrowables.size() == 4
                && chainThrowables.get(0) instanceof jakarta.servlet.ServletException
                && chainThrowables.get(1) instanceof org.springframework.web.multipart.MultipartException
                && chainThrowables.get(2) instanceof java.io.IOException
                && chainThrowables.get(3) instanceof java.util.concurrent.TimeoutException
        ) {
            //Разрыв соединение
            return;
        } else if (chainThrowables.size() == 1
                && chainThrowables.get(0) instanceof java.io.IOException
        ) {
            //Разрыв соединение
            return;
        } else if (chainThrowables.size() == 3
                && chainThrowables.get(0) instanceof ServletException
                && chainThrowables.get(1) instanceof MaxUploadSizeExceededException
                && chainThrowables.get(2) instanceof IllegalStateException
        ) {
            //Exception если загружаемый файл превышает по лимитам
            //java.lang.IllegalStateException: Request exceeds maxRequestSize (33554432)
            return;
        } else if (chainThrowables.size() == 3
                && chainThrowables.get(0) instanceof jakarta.servlet.ServletException
                && chainThrowables.get(1) instanceof org.springframework.web.multipart.MultipartException
                && chainThrowables.get(2) instanceof java.io.IOException
        ) {
            //Request processing failed: org.springframework.web.multipart.MultipartException: Failed to parse multipart servlet request
            return;
        }

        String msgException = "BaseRequest: " + baseRequest.toString() + ", response.status: " + response.getStatus();
        log.error(msgException, ex);

        //Пишем отладочную информацию
        log.error("ChainThrowables, size: {}", chainThrowables.size());
        for (int i = 0; i < chainThrowables.size(); i++) {
            log.error("ChainThrowables, i: {}, exception: {}", i, chainThrowables.get(i).getClass().getName());
        }

        uncaughtExceptionHandler.uncaughtException(Thread.currentThread(), new Exception(msgException, ex));
    }
}
