package com.fuzzy.subsystem.frontend.struct.config;

import com.infomaximum.main.Subsystems;
import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.subsystem.core.CoreSubsystemConsts;
import com.fuzzy.subsystem.core.crypto.CryptoPassword;
import com.fuzzy.subsystems.exception.runtime.ConfigBuilderException;
import com.fuzzy.subsystems.function.Function;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Objects;

public class ConnectorConfig {

    private static final String JSON_PROTOCOL = "protocol";
    private static final String JSON_HOST = "host";
    private static final String JSON_PORT = "port";
    private static final String JSON_SSL_CERT_STORE = "ssl_cert_store";
    private static final String JSON_SSL_CERT_STORE_PASSWORD = "ssl_cert_store_password";
    private static final String JSON_ADD_EXCLUDE_PROTOCOLS = "add_exclude_protocols";
    private static final String JSON_ADD_EXCLUDE_CIPHER_SUITES = "add_exclude_cipher_suites";
    private static final String JSON_TRUST_STORE = "trust_store";
    private static final String JSON_TRUST_STORE_PASSWORD = "trust_store_password";
    private static final String JSON_CRL = "crl";
    private static final String JSON_ENCRYPTED_TRUST_STORE_PASSWORD = "encrypted_trust_store_password";
    private static final String JSON_ENCRYPTED_SSL_CERT_STORE_PASSWORD = "encrypted_ssl_cert_store_password";

    public static class Builder {

        private Protocol protocol;
        private String host;
        private int port;
        private Path sslCertStore;
        private String sslCertStorePassword;
        private String[] excludeProtocols;
        private String[] excludeCipherSuites;
        private Path truststore;
        private String truststorePassword;
        private Path crl;

        public Builder() {
            protocol = Protocol.HTTP;
            host = "0.0.0.0";
            port = getAvailablePortOrDefault();
            sslCertStore = null;
            sslCertStorePassword = null;
            excludeProtocols = new String[]{ "TLSv1" };
            excludeCipherSuites = new String[]{ "TLS_RSA_WITH_AES_128_CBC_SHA256", "TLS_RSA_WITH_AES_128_GCM_SHA256", "TLS_RSA_WITH_AES_256_CBC_SHA256", "TLS_RSA_WITH_AES_256_GCM_SHA384" };
            truststore = null;
            truststorePassword = null;
            crl = null;
        }

        public Builder(JSONObject json) {
            String protocolValue = json.getAsString(JSON_PROTOCOL);
            if (protocolValue == null) {
                throw buildNotFoundException(JSON_PROTOCOL);
            }
            withProtocol(Protocol.of(protocolValue));

            host = json.getAsString(JSON_HOST);
            if (host == null) {
                throw buildNotFoundException(JSON_HOST);
            }

            Number portNumber = json.getAsNumber(JSON_PORT);
            if (portNumber == null) {
                throw buildNotFoundException(JSON_PORT);
            }
            port = portNumber.intValue();

            if (protocol == Protocol.HTTPS) {
                String sslCertStore = json.getAsString(JSON_SSL_CERT_STORE);
                if (sslCertStore == null) {
                    throw buildNotFoundException(JSON_SSL_CERT_STORE);
                }
                withSslCertStore(Paths.get(sslCertStore));

                String sslCertPassword = json.getAsString(JSON_SSL_CERT_STORE_PASSWORD);
                if (sslCertPassword != null) {
                    withSslCertStorePassword(sslCertPassword);
                } else {
                    withEncryptedPassword(json.getAsString(JSON_ENCRYPTED_SSL_CERT_STORE_PASSWORD), this::withSslCertStorePassword);
                }
                JSONArray excludeProtocolsArray = (JSONArray) json.get(JSON_ADD_EXCLUDE_PROTOCOLS);
                if (excludeProtocolsArray != null) {
                    addExcludeProtocols(excludeProtocolsArray.toArray(new String[0]));
                }
                JSONArray excludeCipherSuitesArray = (JSONArray) json.get(JSON_ADD_EXCLUDE_CIPHER_SUITES);
                if (excludeCipherSuitesArray != null) {
                    addExcludeCipherSuites(excludeCipherSuitesArray.toArray(new String[0]));
                }
                String sslTrustStore = json.getAsString(JSON_TRUST_STORE);
                if (sslTrustStore != null) {
                    withTrustStore(Paths.get(sslTrustStore));
                }
                String trustStorePassword = json.getAsString(JSON_TRUST_STORE_PASSWORD);
                if (trustStorePassword != null) {
                    withTrustStorePassword(trustStorePassword);
                } else {
                    withEncryptedPassword(json.getAsString(JSON_ENCRYPTED_TRUST_STORE_PASSWORD), this::withTrustStorePassword);
                }
                String crlPath = json.getAsString(JSON_CRL);
                if (crlPath != null) {
                    withCrl(Paths.get(crlPath));
                }
            }
        }

        private void withEncryptedPassword(String param, Function<String, Builder> function) {
            if (Objects.nonNull(param) && !param.isEmpty()) {
                Path coreConfigJsonPath = Subsystems.getInstance().getConfig().getConfigDir().resolve(CoreSubsystemConsts.UUID + ".json");
                JSONObject coreJSON;
                if (Files.exists(coreConfigJsonPath)) {
                    try (InputStream is = Files.newInputStream(coreConfigJsonPath, StandardOpenOption.READ)) {
                        coreJSON = (JSONObject) new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE).parse(is);
                    } catch (Exception e) {
                        throw new ConfigBuilderException(e);
                    }
                } else {
                    throw new ConfigBuilderException("Core config not found");
                }
                Path secretKeyPath = Path.of(coreJSON.getAsString("secret_key_path"));
                if (!secretKeyPath.isAbsolute()) {
                    secretKeyPath = Subsystems.getInstance().getConfig().getDataDir().resolve(secretKeyPath).toAbsolutePath();
                }
                if (!secretKeyPath.toFile().isFile()) {
                    throw new ConfigBuilderException("Encrypted password is present but no secret key found.");
                }
                try {
                    CryptoPassword cryptoPassword = new CryptoPassword(secretKeyPath);
                    function.apply(cryptoPassword.decryptBase64String(param));
                } catch (PlatformException e) {
                    throw new ConfigBuilderException(e);
                }
            }
        }

        public Builder withProtocol(Protocol protocol) {
            this.protocol = protocol;
            return this;
        }

        public Builder withSslCertStore(Path sslCertStore) {
            if (!Files.exists(sslCertStore) || Files.isDirectory(sslCertStore)) {
                throw buildNotFoundException(JSON_SSL_CERT_STORE);
            }
            this.sslCertStore = sslCertStore;
            return this;
        }

        public Builder withSslCertStorePassword(String sslCertStorePassword) {
            this.sslCertStorePassword = sslCertStorePassword;
            return this;
        }

        public Builder withTrustStore(Path trustStore) {
            this.truststore = trustStore;
            return this;
        }

        public Builder withTrustStorePassword(String password) {
            this.truststorePassword = password;
            return this;
        }

        public Builder withCrl(Path crl) {
            this.crl = crl;
            return this;
        }

        public Builder addExcludeProtocols(String... protocols) {
            this.excludeProtocols = ArrayUtils.addAll(this.excludeProtocols, protocols);
            return this;
        }

        public Builder addExcludeCipherSuites(String... cipherSuites) {
            this.excludeCipherSuites = ArrayUtils.addAll(this.excludeCipherSuites, cipherSuites);
            return this;
        }

        public int getPort() {
            return port;
        }

        public Protocol getProtocol() {
            return protocol;
        }

        public JSONObject toJson() {
            JSONObject json = new JSONObject();
            json.put(JSON_PROTOCOL, protocol.getValue());
            json.put(JSON_HOST, host);
            json.put(JSON_PORT, port);
            json.put(JSON_SSL_CERT_STORE, sslCertStore != null ? sslCertStore.toString() : null);
            json.put(JSON_SSL_CERT_STORE_PASSWORD, sslCertStorePassword);
            json.put(JSON_ADD_EXCLUDE_PROTOCOLS, excludeProtocols == null ? null : new JSONArray() {{
                addAll(Arrays.asList(excludeProtocols));
            }});
            json.put(JSON_ADD_EXCLUDE_CIPHER_SUITES, excludeCipherSuites == null ? null : new JSONArray() {{
                addAll(Arrays.asList(excludeCipherSuites));
            }});
            json.put(JSON_TRUST_STORE, truststore != null ? truststore.toString() : null);
            json.put(JSON_TRUST_STORE_PASSWORD, truststorePassword);
            json.put(JSON_CRL, crl != null ? crl.toString() : null);
            return json;
        }

        public ConnectorConfig build() {
            return new ConnectorConfig(this);
        }
    }

    private final Protocol protocol;
    private final String host;
    private final int port;
    private final Path sslCertStore;
    private final String sslCertStorePassword;
    private final String[] excludeProtocols;
    private final String[] excludeCipherSuites;
    private final Path truststore;
    private final String truststorePassword;
    private final Path crl;


    private ConnectorConfig(Builder builder) {
        this.protocol = builder.protocol;
        this.host = builder.host;
        this.port = builder.port;
        this.sslCertStore = builder.sslCertStore;
        this.sslCertStorePassword = builder.sslCertStorePassword;
        this.excludeProtocols = builder.excludeProtocols;
        this.excludeCipherSuites = builder.excludeCipherSuites;
        this.truststore = builder.truststore;
        this.truststorePassword = builder.truststorePassword;
        this.crl = builder.crl;
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public Path getSslCertStore() {
        return sslCertStore;
    }

    public String getSslCertStorePassword() {
        return sslCertStorePassword;
    }

    public String[] getExcludeProtocols() {
        return excludeProtocols;
    }

    public String[] getExcludeCipherSuites() {
        return excludeCipherSuites;
    }

    public Path getTruststore() {
        return truststore;
    }

    public String getTruststorePassword() {
        return truststorePassword;
    }

    public Path getCrl() {
        return crl;
    }

    private static int getAvailablePortOrDefault() {
        for (int port = 8010; port < 8080; ++port) {
            if (isAvailablePort(port)) {
                return port;
            }
        }
        return 8010;
    }

    private static boolean isAvailablePort(int port) {
        try (ServerSocket ss = new ServerSocket(port)) {
            ss.setReuseAddress(true);
        } catch (IOException ignored) {
            return false;
        }

        try (DatagramSocket ds = new DatagramSocket(port)) {
            ds.setReuseAddress(true);
        } catch (IOException ignored) {
            return false;
        }

        return true;
    }

    private static RuntimeException buildNotFoundException(String fieldName) {
        return new RuntimeException(fieldName + " not found.");
    }
}
