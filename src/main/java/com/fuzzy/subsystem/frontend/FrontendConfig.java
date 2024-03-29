package com.fuzzy.subsystem.frontend;

import com.google.common.base.CharMatcher;
import com.infomaximum.cluster.struct.Info;
import com.fuzzy.main.SubsystemsConfig;
import com.fuzzy.subsystem.frontend.struct.config.ConnectorConfig;
import com.fuzzy.subsystems.exception.runtime.ConfigBuilderException;
import com.fuzzy.subsystems.subsystem.SubsystemConfig;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class FrontendConfig extends SubsystemConfig {

    public static final Duration DEFAULT_REQUEST_TIMEOUT = Duration.ofMinutes(10);
    public static final Duration DEFAULT_SESSION_TIMEOUT = Duration.ofDays(7);
    public static final boolean DEFAULT_SUPPORT_IFRAME = false;
    public static final String DEFAULT_CORS_POLICY = null;

    public static final String DEFAULT_WEB_DIR = "web";
    public static final String DEFAULT_TEMP_DIR = "temp";

    private final static Logger log = LoggerFactory.getLogger(FrontendConfig.class);

    private static final String JSON_CONNECTORS = "connectors";
    private static final String JSON_URL = "url";
    private static final String JSON_WEB_DIR = "web_dir";
    private static final String JSON_TEMP_DIR = "temp_dir";
    private static final String JSON_REQUEST_TIMEOUT = "request_timeout";
    private static final String JSON_SESSION_TIMEOUT = "session_timeout";
    private static final String JSON_SERVICE_MODE = "service_mode";
    private static final String JSON_SERVICE_MODE_MESSAGE = "service_mode_message";
    public static final String JSON_SUPPORT_IFRAME = "support_iframe";

    public static final String JSON_CORS_POLICY = "cors_policy";

    private final List<ConnectorConfig> connectors;
    private final URL url;
    private final Path webPath;
    private final Path tempPath;
    private final boolean serviceMode;
    private final String serviceModeMessage;
    private final boolean supportIFrame;
    private final String corsPolicy;

    private FrontendConfig(Builder builder) {
        super(builder);

        this.connectors = Collections.unmodifiableList(
                builder.connectors.stream().map(ConnectorConfig.Builder::build).collect(Collectors.toList())
        );
        this.url = builder.url;
        this.webPath = builder.webPath;
        this.tempPath = builder.tempPath;
        this.requestTimeout = builder.requestTimeout;
        this.sessionTimeout = builder.sessionTimeout;
        this.serviceMode = builder.serviceMode;
        this.serviceModeMessage = builder.serviceModeMessage;
        this.corsPolicy = builder.corsPolicy;
        this.supportIFrame = builder.supportIFrame;
    }

    private final Duration sessionTimeout;
    private final Duration requestTimeout;

    public Path getTempPath() {
        return tempPath;
    }

    public List<ConnectorConfig> getConnectors() {
        return connectors;
    }

    public URL getUrl() {
        return url;
    }

    public Path getWebPath() {
        return webPath;
    }

    public boolean isServiceMode() {
        return serviceMode;
    }

    public String getServiceModeMessage() {
        return serviceModeMessage;
    }

    public boolean isSupportIFrame() {
        return supportIFrame;
    }

    public String getCorsPolicy() {
        return corsPolicy;
    }

    public static class Builder extends SubsystemConfig.Builder {

        private List<ConnectorConfig.Builder> connectors;
        private URL url;
        private Path webPath;
        private Path tempPath;
        private Duration requestTimeout;
        private Duration sessionTimeout;
        private boolean serviceMode;
        private String serviceModeMessage;
        private boolean supportIFrame;
        private String corsPolicy;

        public Builder(Info subSystemInfo, SubsystemsConfig subsystemsConfig) throws ConfigBuilderException {
            super(subSystemInfo, subsystemsConfig);
            init();
        }

        public Builder withConnectors(List<ConnectorConfig.Builder> value) {
            this.connectors = value;
            return this;
        }

        public Builder withURL(URL value) {
            this.url = value;
            return this;
        }

        public Builder withBuildURL(ConnectorConfig.Builder httpConnectorBuilder) {
            try {
                this.url = new URL(httpConnectorBuilder.getProtocol().getValue(), getCurrentHost(), httpConnectorBuilder.getPort(), "");
            } catch (MalformedURLException e) {
                throw new ConfigBuilderException(e);
            }
            return this;
        }

        public Builder withWebPath(Path value) {
            if (!(Files.exists(value) && Files.isDirectory(value))) {
                log.warn("WebPath is not exists: {}", value.toString());
            }

            this.webPath = value;
            return this;
        }

        public Builder withTempPath(Path value) {
            this.tempPath = value;
            return this;
        }

        public Builder withRequestTimeout(Duration value) {
            this.requestTimeout = value;
            return this;
        }

        public Builder withSessionTimeout(Duration value) {
            this.sessionTimeout = value;
            return this;
        }

        public List<ConnectorConfig.Builder> getConnectors() {
            return connectors;
        }

        @Override
        public FrontendConfig build() {
            return new FrontendConfig(this);
        }

        private void init() throws ConfigBuilderException {
            JSONObject json = readJSON();
            if (json.isEmpty()) {
                initDefault();
                save();
            } else {
                loadFrom(json);
            }
        }

        private void initDefault() {
            ConnectorConfig.Builder httpConnectorBuilder = new ConnectorConfig.Builder();
            withBuildURL(httpConnectorBuilder);
            withConnectors(Collections.singletonList(httpConnectorBuilder));
            withWebPath(getPath(JSON_WEB_DIR, new JSONObject(), DEFAULT_WEB_DIR, getSubsystemsConfig().getWorkDir()));
            withTempPath(getPath(JSON_TEMP_DIR, new JSONObject(), DEFAULT_TEMP_DIR, getSubsystemsConfig().getDataDir()));
            withRequestTimeout(DEFAULT_REQUEST_TIMEOUT);
            withSessionTimeout(DEFAULT_SESSION_TIMEOUT);
            this.serviceMode = false;
            this.serviceModeMessage = StringUtils.EMPTY;
            this.supportIFrame = DEFAULT_SUPPORT_IFRAME;
            this.corsPolicy = DEFAULT_CORS_POLICY;
        }

        private void loadFrom(JSONObject json) {
            if (!json.containsKey(JSON_URL)) {
                throw new RuntimeException("Frontend configure. Url not found.");
            }
            try {
                withURL(new URL(json.getAsString(JSON_URL)));
            } catch (MalformedURLException e) {
                throw new ConfigBuilderException(e);
            }

            if (!json.containsKey(JSON_WEB_DIR)) {
                throw new RuntimeException("Frontend configure. Web path not found.");
            }
            withWebPath(getPath(JSON_WEB_DIR, json, DEFAULT_WEB_DIR, getSubsystemsConfig().getWorkDir()));

            withTempPath(getPath(JSON_TEMP_DIR, json, DEFAULT_TEMP_DIR, getSubsystemsConfig().getDataDir()));

            JSONArray jConnectors = (JSONArray) json.get(JSON_CONNECTORS);
            if (jConnectors == null || jConnectors.isEmpty()) {
                throw new RuntimeException("Frontend configure. Connectors not found.");
            }
            ArrayList<ConnectorConfig.Builder> connectors = new ArrayList<>(jConnectors.size());
            jConnectors.forEach(o -> connectors.add(new ConnectorConfig.Builder((JSONObject) o)));
            withConnectors(connectors);

            if (json.containsKey(JSON_REQUEST_TIMEOUT)) {
                requestTimeout = parseDuration(json.getAsString(JSON_REQUEST_TIMEOUT));
            } else {
                requestTimeout = DEFAULT_REQUEST_TIMEOUT;
            }

            if (json.containsKey(JSON_SESSION_TIMEOUT)) {
                sessionTimeout = parseDuration(json.getAsString(JSON_SESSION_TIMEOUT));
            } else {
                sessionTimeout = DEFAULT_SESSION_TIMEOUT;
            }

            if (json.containsKey(JSON_SERVICE_MODE)) {
                serviceMode = (boolean)json.get(JSON_SERVICE_MODE);
            } else {
                serviceMode = false;
            }

            if (json.containsKey(JSON_SERVICE_MODE_MESSAGE)) {
                serviceModeMessage = json.getAsString(JSON_SERVICE_MODE_MESSAGE);
            } else {
                serviceModeMessage = StringUtils.EMPTY;
            }

            if (json.containsKey(JSON_SUPPORT_IFRAME)) {
                this.supportIFrame = Boolean.parseBoolean(json.getAsString(JSON_SUPPORT_IFRAME));
            } else {
                this.supportIFrame = DEFAULT_SUPPORT_IFRAME;
            }

            if (json.containsKey(JSON_CORS_POLICY)) {
                corsPolicy = json.getAsString(JSON_CORS_POLICY);
                if (corsPolicy!=null && corsPolicy.trim().isEmpty()) {
                    corsPolicy = null;
                }
            } else {
                this.corsPolicy = DEFAULT_CORS_POLICY;
            }

            if (supportIFrame && corsPolicy == null) {
                throw new ConfigBuilderException("Enabled iframe support but no cors policy defined");
            }
        }

        private String getCurrentHost() {
            try {
                String currentHost = InetAddress.getLocalHost().getHostName().toLowerCase();
                if (CharMatcher.ascii().matchesAllOf(currentHost)) {
                    return currentHost;
                }
            } catch (UnknownHostException ignored) {
            }

            return "127.0.0.1";
        }

        public void save() {
            JSONObject json = new JSONObject();
            json.put(JSON_URL, url.toExternalForm());
            json.put(JSON_WEB_DIR, tryRelativizePath(webPath, getSubsystemsConfig().getWorkDir()).toString());
            json.put(JSON_TEMP_DIR, tryRelativizePath(tempPath, getSubsystemsConfig().getDataDir()).toString());
            JSONArray jConnectors = new JSONArray();
            for (ConnectorConfig.Builder connector : connectors) {
                jConnectors.add(connector.toJson());
            }
            json.put(JSON_CONNECTORS, jConnectors);
            json.put(JSON_REQUEST_TIMEOUT, packDuration(requestTimeout));
            json.put(JSON_SESSION_TIMEOUT, packDuration(sessionTimeout));
            json.put(JSON_SERVICE_MODE, serviceMode);
            json.put(JSON_SERVICE_MODE_MESSAGE, serviceModeMessage);
            json.put(JSON_SUPPORT_IFRAME, DEFAULT_SUPPORT_IFRAME);
            json.put(JSON_CORS_POLICY, DEFAULT_CORS_POLICY);
            saveJSON(json);
        }
    }

    public Duration getSessionTimeout() {
        return sessionTimeout;
    }

    public Duration getRequestTimeout() {
        return requestTimeout;
    }

}
