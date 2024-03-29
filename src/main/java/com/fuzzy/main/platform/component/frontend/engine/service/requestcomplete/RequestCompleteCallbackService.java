package com.fuzzy.main.platform.component.frontend.engine.service.requestcomplete;

import org.eclipse.jetty.server.HttpChannel;
import org.eclipse.jetty.server.Request;

public class RequestCompleteCallbackService implements HttpChannel.Listener {

    public final static String ATTRIBUTE_COMPLETE_REQUEST_CALLBACK = "com.fuzzy.request.complete.callback";

    public abstract static class Callback {
        public abstract void exec(Request request) throws Throwable;
    }

    private final Thread.UncaughtExceptionHandler uncaughtExceptionHandler;

    public RequestCompleteCallbackService(Thread.UncaughtExceptionHandler uncaughtExceptionHandler) {
        this.uncaughtExceptionHandler = uncaughtExceptionHandler;
    }

    @Override
    public void onComplete(Request request) {
        execCallback(request);
    }

    @Override
    public void onResponseFailure(Request request, Throwable failure) {
        //Ulitin V. Нет уверености в порядке вызовов - поэтому на всякий случай вызывем - хуже не будет
        execCallback(request);
    }

    private void execCallback(Request request) {
        Callback callback = (Callback) request.getAttribute(ATTRIBUTE_COMPLETE_REQUEST_CALLBACK);
        if (callback != null) {
            try {
                callback.exec(request);
            } catch (Throwable throwable) {
                uncaughtExceptionHandler.uncaughtException(Thread.currentThread(), throwable);
            }
            request.removeAttribute(ATTRIBUTE_COMPLETE_REQUEST_CALLBACK);
        }
    }

}
