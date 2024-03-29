package com.fuzzy.main.cluster.core.service.transport.network.grpc;

import com.fuzzy.main.cluster.NetworkTransit;
import com.fuzzy.main.cluster.core.service.transport.TransportManager;
import com.infomaximum.cluster.core.service.transport.network.grpc.internal.GrpcNetworkTransitImpl;
import com.infomaximum.cluster.core.service.transport.network.grpc.internal.utils.CertificateUtils;

import javax.net.ssl.TrustManagerFactory;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public interface GrpcNetworkTransit {

    class Builder extends NetworkTransit.Builder {

        public static final Duration DEFAULT_TIMEOUT_CONFIRMATION_WAIT_RESPONSE = Duration.ofSeconds(20);

        public final String nodeName;
        public final int port;
        private final List<GrpcRemoteNode> targets;

        private Duration timeoutConfirmationWaitResponse;

        private final Thread.UncaughtExceptionHandler uncaughtExceptionHandler;
        private byte[] fileCertChain;
        private byte[] filePrivateKey;
        private TrustManagerFactory trustStore;

        public Builder(String nodeName, int port, Thread.UncaughtExceptionHandler uncaughtExceptionHandler) {
            this.nodeName = nodeName;
            this.port = port;
            this.targets = new ArrayList<>();
            this.timeoutConfirmationWaitResponse = DEFAULT_TIMEOUT_CONFIRMATION_WAIT_RESPONSE;
            this.uncaughtExceptionHandler = uncaughtExceptionHandler;
        }

        public Builder addTarget(GrpcRemoteNode target) {
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

        public List<GrpcRemoteNode> getTargets() {
            return Collections.unmodifiableList(targets);
        }

        public Duration getTimeoutConfirmationWaitResponse() {
            return timeoutConfirmationWaitResponse;
        }

        public GrpcNetworkTransitImpl build(TransportManager transportManager) {
            return new GrpcNetworkTransitImpl(this, transportManager, fileCertChain, filePrivateKey, trustStore, uncaughtExceptionHandler);
        }

    }

}
