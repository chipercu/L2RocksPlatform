package com.fuzzy.cluster.core.service.transport.network.grpc.internal.utils;

import com.fuzzy.cluster.core.service.transport.network.grpc.exception.ClusterGrpcException;

import javax.net.ssl.TrustManagerFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

public class CertificateUtils {

    public static X509Certificate build(byte[] certificate) throws CertificateException {
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        try (InputStream in = new ByteArrayInputStream(certificate)) {
            return (X509Certificate) certificateFactory.generateCertificate(in);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static TrustManagerFactory buildTrustStore(byte[] fileCertChain, byte[]... trustCertificates) {

        //Создаем KeyStore
        KeyStore keyStore;
        try {
            keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, new char[0]);
        } catch (KeyStoreException | IOException | NoSuchAlgorithmException | CertificateException e) {
            throw new ClusterGrpcException(e);
        }

        //Добавляем встроенный сертификат
        X509Certificate defaultCertificate;
        try {
            defaultCertificate = build(fileCertChain);
        } catch (CertificateException ce) {
            throw new ClusterGrpcException("Error build certificate", ce);
        }
        try {
            keyStore.setCertificateEntry("ext-default", defaultCertificate);
        } catch (KeyStoreException e) {
            throw new ClusterGrpcException(e);
        }

        //Добавляем все доверенные сертификаты
        for (int i = 0; i < trustCertificates.length; i++) {
            byte[] trustCertificate = trustCertificates[i];
            //Предполагаем, что перед нами truststore
            try {
                KeyStore iKeyStore = KeyStore.getInstance(KeyStore.getDefaultType());
                iKeyStore.load(new ByteArrayInputStream(trustCertificate), new char[0]);

                for (Enumeration<String> e = iKeyStore.aliases(); e.hasMoreElements(); ) {
                    String alias = e.nextElement();
                    keyStore.setCertificateEntry(alias, iKeyStore.getCertificate(alias));
                }
                continue;
            } catch (KeyStoreException | IOException | NoSuchAlgorithmException | CertificateException ignore) {
            }

            //Предполагаем что перед нами обычный файл с сертификатами
            X509Certificate certificate;
            try {
                certificate = build(trustCertificate);
            } catch (CertificateException ce) {
                throw new ClusterGrpcException("Error build certificate from truststore (" + i + ")", ce);
            }
            try {
                keyStore.setCertificateEntry("ext-" + i, certificate);
            } catch (KeyStoreException e) {
                throw new ClusterGrpcException(e);
            }
        }

        TrustManagerFactory trustManagerFactory;
        try {
            trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);
        } catch (NoSuchAlgorithmException | KeyStoreException e) {
            throw new ClusterGrpcException(e);
        }
        return trustManagerFactory;
    }
}
