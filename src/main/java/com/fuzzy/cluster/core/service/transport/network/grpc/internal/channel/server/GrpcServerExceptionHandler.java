package com.fuzzy.cluster.core.service.transport.network.grpc.internal.channel.server;

import io.grpc.*;

public class GrpcServerExceptionHandler implements ServerInterceptor {

    private final Thread.UncaughtExceptionHandler uncaughtExceptionHandler;

    public GrpcServerExceptionHandler(Thread.UncaughtExceptionHandler uncaughtExceptionHandler) {
        this.uncaughtExceptionHandler = uncaughtExceptionHandler;
    }

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> serverCall, Metadata metadata, ServerCallHandler<ReqT, RespT> serverCallHandler) {
        ServerCall.Listener<ReqT> listener = serverCallHandler.startCall(serverCall, metadata);
        return new ExceptionHandlingServerCallListener<>(listener, serverCall, metadata, uncaughtExceptionHandler);
    }


    private class ExceptionHandlingServerCallListener<ReqT, RespT> extends ForwardingServerCallListener.SimpleForwardingServerCallListener<ReqT> {
        private ServerCall<ReqT, RespT> serverCall;
        private Metadata metadata;

        private final Thread.UncaughtExceptionHandler uncaughtExceptionHandler;

        ExceptionHandlingServerCallListener(ServerCall.Listener<ReqT> listener,
                                            ServerCall<ReqT, RespT> serverCall,
                                            Metadata metadata,
                                            Thread.UncaughtExceptionHandler uncaughtExceptionHandler
                                            ) {
            super(listener);
            this.serverCall = serverCall;
            this.metadata = metadata;
            this.uncaughtExceptionHandler = uncaughtExceptionHandler;
        }

        @Override
        public void onHalfClose() {
            try {
                super.onHalfClose();
            } catch (RuntimeException ex) {
                handleException(ex, serverCall, metadata);
                throw ex;
            }
        }

        @Override
        public void onReady() {
            try {
                super.onReady();
            } catch (RuntimeException ex) {
                handleException(ex, serverCall, metadata);
                throw ex;
            }
        }

        private void handleException(RuntimeException exception, ServerCall<ReqT, RespT> serverCall, Metadata metadata) {
            serverCall.close(Status.UNKNOWN, metadata);
            uncaughtExceptionHandler.uncaughtException(Thread.currentThread(), exception);
        }
    }
}
