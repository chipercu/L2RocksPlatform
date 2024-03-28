package com.fuzzy.main.network.transport.http.builder.connector;

import com.fuzzy.main.network.exception.NetworkException;
import com.fuzzy.main.network.struct.info.HttpConnectorInfo;
import com.fuzzy.main.network.utils.TempKeyStore;
import org.eclipse.jetty.http3.server.HTTP3ServerConnectionFactory;
import org.eclipse.jetty.http3.server.HTTP3ServerConnector;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.util.ssl.SslContextFactory;

import java.security.KeyStore;
import java.util.function.Supplier;

public class BuilderHttp3Connector extends BuilderHttpConnector {

    private KeyStore keyStore;

    //Из-за внутренних особенносте jetty - не принимаются приватные ключи без пароля, потом этот кода - обертку надо удалить
    private TempKeyStore tempKeyStore;

    public BuilderHttp3Connector(int port) {
        super(port);
    }

    public BuilderHttp3Connector withSsl(byte[] certChain, byte[] privateKey) {
        //Как jetty поддержит незашированные приватные ключи перейти на этот механизм - а старый код подчистить
        //keyStore = CertificateUtils.buildKeyStore(certChain, privateKey);
        tempKeyStore = new TempKeyStore(certChain, privateKey);
        return this;
    }

    @Override
    public Connector build(Server server) throws NetworkException {

        HttpConfiguration httpConfig = new HttpConfiguration();
        httpConfig.setSendServerVersion(false);
        httpConfig.addCustomizer(new SecureRequestCustomizer());
        if (requestHeaderSize != null) {
            httpConfig.setRequestHeaderSize(requestHeaderSize);
        }
        if (responseHeaderSize != null) {
            httpConfig.setResponseHeaderSize(responseHeaderSize);
        }
        HTTP3ServerConnectionFactory http3ConnectionFactory = new HTTP3ServerConnectionFactory(httpConfig);


        if (keyStore == null && tempKeyStore == null) {
            throw new RuntimeException("Not set ssl context");
        }

        SslContextFactory.Server sslContextFactory = tempKeyStore.sslContextFactory;
        //Как jetty поддержит незашированные приватные ключи перейти на этот механизм - а старый код подчистить
//        SslContextFactory.Server sslContextFactory = new SslContextFactory.Server();
//        sslContextFactory.setKeyStore(keyStore);


        HTTP3ServerConnector connector = new HTTP3ServerConnector(server, sslContextFactory, http3ConnectionFactory);
        connector.setPort(port);
        connector.setHost(host);

        return connector;
    }

    public Supplier<? extends HttpConnectorInfo> getInfoSupplier() {
        return () -> new HttpConnectorInfo(host, port);
    }

}
