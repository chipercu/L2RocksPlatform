package com.fuzzy.cluster.core.service.transport.network.grpc;

import com.fuzzy.cluster.NetworkTransit;
import com.fuzzy.cluster.core.service.transport.TransportManager;
import com.fuzzy.cluster.core.service.transport.network.grpc.GrpcRemoteNode;
import com.fuzzy.cluster.core.service.transport.network.grpc.internal.GrpcNetworkTransitImpl;
import com.fuzzy.cluster.core.service.transport.network.grpc.internal.utils.CertificateUtils;

import javax.net.ssl.TrustManagerFactory;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public interface GrpcNetworkTransit {

    class Builder extends NetworkTransit.Builder {

        public record Server(int port){} //String host

        public static final Duration DEFAULT_TIMEOUT_CONFIRMATION_WAIT_RESPONSE = Duration.ofSeconds(20);

        public static final Duration DEFAULT_PING_PONG_INTERVAL = Duration.ofSeconds(5);
        public static final Duration DEFAULT_PING_PONG_TIMEOUT = Duration.ofSeconds(3);

        private String nodeName;
        private Server server;;
        private final List<com.fuzzy.cluster.core.service.transport.network.grpc.GrpcRemoteNode> targets;

        private Duration timeoutConfirmationWaitResponse;

        private Duration pingPongInterval;
        private Duration pingPongTimeout;

        private final Thread.UncaughtExceptionHandler uncaughtExceptionHandler;
        private byte[] fileCertChain;
        private byte[] filePrivateKey;
        private TrustManagerFactory trustStore;

        public Builder(Thread.UncaughtExceptionHandler uncaughtExceptionHandler) {
            this.targets = new ArrayList<>();
            this.timeoutConfirmationWaitResponse = DEFAULT_TIMEOUT_CONFIRMATION_WAIT_RESPONSE;
            this.pingPongInterval = DEFAULT_PING_PONG_INTERVAL;
            this.pingPongTimeout = DEFAULT_PING_PONG_TIMEOUT;
            this.uncaughtExceptionHandler = uncaughtExceptionHandler;
        }

        public Builder withNodeName(String nodeName) {
            this.nodeName = nodeName;
            return this;
        }

        public Builder withServer(Server server) {
            this.server = server;
            return this;
        }

        public Builder addTarget(com.fuzzy.cluster.core.service.transport.network.grpc.GrpcRemoteNode target) {
            this.targets.add(target);
            return this;
        }

        public Builder withTransportSecurity(byte[] fileCertChain, byte[] filePrivateKey, byte[]... trustCertificates) {
            if (fileCertChain == null) {
                throw new IllegalArgumentException();
            }
            if (filePrivateKey == null) {
                throw new IllegalArgumentException();
            }
            this.fileCertChain = fileCertChain;
            this.filePrivateKey = filePrivateKey;
            trustStore = CertificateUtils.buildTrustStore(fileCertChain, trustCertificates);
            return this;
        }

        public Builder withTimeoutConfirmationWaitResponse(Duration value) {
            this.timeoutConfirmationWaitResponse = value;
            return this;
        }

        public Builder withPingPongTimeout(Duration interval, Duration timeout) {
            this.pingPongInterval = interval;
            this.pingPongTimeout = timeout;
            return this;
        }

        public String getNodeName() {
            return nodeName;
        }

        public Server getServer() {
            return server;
        }

        public List<GrpcRemoteNode> getTargets() {
            return Collections.unmodifiableList(targets);
        }

        public Duration getTimeoutConfirmationWaitResponse() {
            return timeoutConfirmationWaitResponse;
        }

        public Duration getPingPongInterval() {
            return pingPongInterval;
        }

        public Duration getPingPongTimeout() {
            return pingPongTimeout;
        }

        public GrpcNetworkTransitImpl build(TransportManager transportManager) {
            return new GrpcNetworkTransitImpl(this, transportManager, fileCertChain, filePrivateKey, trustStore, uncaughtExceptionHandler);
        }

    }

}
