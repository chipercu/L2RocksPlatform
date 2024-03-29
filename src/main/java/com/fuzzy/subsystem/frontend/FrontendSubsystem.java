package com.fuzzy.subsystem.frontend;

import com.fuzzy.subsystem.core.CoreSubsystemConsts;
import com.infomaximum.cluster.Cluster;
import com.infomaximum.cluster.component.memory.MemoryComponent;
import com.infomaximum.cluster.core.service.transport.executor.ComponentExecutorTransportImpl;
import com.infomaximum.cluster.exception.ClusterException;
import com.infomaximum.database.domainobject.filter.HashFilter;
import com.fuzzy.main.Subsystems;
import com.fuzzy.main.SubsystemsConfig;
import com.infomaximum.network.Network;
import com.infomaximum.network.builder.BuilderNetwork;
import com.infomaximum.network.exception.NetworkException;
import com.infomaximum.network.transport.http.builder.ConfigUploadFiles;
import com.infomaximum.network.transport.http.builder.HttpBuilderTransport;
import com.infomaximum.network.transport.http.builder.connector.BuilderHttpConnector;
import com.infomaximum.network.transport.http.builder.connector.BuilderJHttpsConnector;
import com.infomaximum.network.transport.http.builder.filter.BuilderFilter;
import com.infomaximum.platform.component.database.DatabaseComponent;
import com.infomaximum.platform.component.frontend.engine.FrontendEngine;
import com.infomaximum.platform.component.frontend.engine.controller.Controllers;
import com.infomaximum.platform.component.frontend.engine.network.protocol.graphqltransportws.GraphqlTransportWSProtocolBuilder;
import com.infomaximum.platform.component.frontend.engine.network.protocol.graphqltransportws.handler.graphql.GraphQLTransportWSHandler;
import com.infomaximum.platform.component.frontend.engine.network.protocol.graphqlws.GraphqlWSProtocolBuilder;
import com.infomaximum.platform.component.frontend.engine.network.protocol.graphqlws.handler.graphql.GraphQLWSHandler;
import com.infomaximum.platform.component.frontend.engine.provider.ProviderGraphQLRequestExecuteService;
import com.infomaximum.platform.component.frontend.engine.service.errorhandler.PlatformErrorHandler;
import com.infomaximum.platform.component.frontend.engine.service.graphqlrequestexecute.GraphQLRequestExecuteService;
import com.infomaximum.platform.component.frontend.engine.service.statistic.StatisticService;
import com.infomaximum.platform.component.frontend.request.graphql.builder.impl.DefaultGraphQLRequestBuilder;
import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.ReadableResource;
import com.infomaximum.platform.querypool.ResourceProvider;
import com.infomaximum.platform.sdk.component.ComponentType;
import com.infomaximum.platform.sdk.context.ContextTransaction;
import com.infomaximum.platform.sdk.struct.ClusterContext;
import com.infomaximum.platform.sdk.struct.querypool.QuerySystem;
import com.fuzzy.subsystem.core.CoreSubsystem;
import com.fuzzy.subsystem.core.domainobject.accessrole.AccessRoleReadable;
import com.fuzzy.subsystem.core.remote.accessroleprivileges.PrivilegeValue;
import com.fuzzy.subsystem.core.remote.accessroleprivileges.RControllerAccessRolePrivileges;
import com.fuzzy.subsystem.frontend.access.FrontendPrivilege;
import com.fuzzy.subsystem.frontend.network.request.graphql.builder.GraphQLRequestAttributeBuilderImpl;
import com.fuzzy.subsystem.frontend.service.authorize.RequestAuthorizeImpl;
import com.fuzzy.subsystem.frontend.service.errorhandler.ActionErrorHandlerImpl;
import com.fuzzy.subsystem.frontend.service.session.SessionServiceEmployee;
import com.fuzzy.subsystem.frontend.service.spring.SpringConfigurationMvc;
import com.fuzzy.subsystem.frontend.service.spring.filter.DisallowedMethodFilter;
import com.fuzzy.subsystem.frontend.service.spring.filter.HttpHeaderFilter;
import com.fuzzy.subsystem.frontend.struct.config.ConnectorConfig;
import com.fuzzy.subsystems.access.AccessOperation;
import com.fuzzy.subsystems.access.AccessOperationCollection;
import com.fuzzy.subsystems.exception.GeneralExceptionBuilder;
import com.fuzzy.subsystems.subsystem.Info;
import com.fuzzy.subsystems.subsystem.SdkInfoBuilder;
import com.fuzzy.subsystems.subsystem.Subsystem;
import com.fuzzy.subsystems.utils.CompositeSystemQuery;
import com.fuzzy.utils.FileUtils;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

@com.infomaximum.cluster.anotation.Info(uuid = FrontendSubsystemConsts.UUID)
public class FrontendSubsystem extends Subsystem implements ProviderGraphQLRequestExecuteService {

    private final static int REQUEST_HEADER_SIZE = 20 * 1024;
    private final static int RESPONSE_HEADER_SIZE = 20 * 1024;
    private final static String DOCS_DIR_NAME = FrontendSubsystemConsts.UUID + ".docs";

    public static final Info INFO = new SdkInfoBuilder(FrontendSubsystemConsts.UUID, FrontendSubsystem.class)
            .withDependence(MemoryComponent.class)
            .withDependence(DatabaseComponent.class)
            .withSdkDependence(CoreSubsystem.INFO)
            .build();

    private FrontendConfig config;
    private Path docsDirPath;

    private FrontendEngine frontendEngine;

    private SessionServiceEmployee sessionService;

    public FrontendSubsystem() {}

    @Override
    public void onInitialized() {
        this.sessionService = new SessionServiceEmployee(this);
    }

    @Override
    public ComponentType getType() {
        return ComponentType.FRONTEND;
    }

    @Override
    public QuerySystem<Void> onStart() {
        return new CompositeSystemQuery(List.of(
                getDocsUnpackingQuery(),
                getFrontendEngineStartQuery(),
                new DocsAccessAdminAccessRoleInitQuery()
        ));
    }

    @Override
    protected ComponentExecutorTransportImpl.Builder getExecutorTransportBuilder() {
        Cluster cluster = getCluster();
        ClusterContext clusterContext = cluster.getContext();

        Subsystems subsystems = clusterContext.getContext();
        SubsystemsConfig subsystemsConfig = subsystems.getConfig();
        this.config = new FrontendConfig.Builder(INFO, subsystemsConfig).build();
        this.docsDirPath = subsystemsConfig.getTempDir().resolve(DOCS_DIR_NAME);

        this.frontendEngine = new FrontendEngine.Builder(clusterContext.platform, this)
                .withBuilderNetwork(buildNetwork(cluster.getUncaughtExceptionHandler()))
                .withRequestAuthorizeBuilder(new RequestAuthorizeImpl.Builder())
                .withGraphQLRequestBuilder(new DefaultGraphQLRequestBuilder.Builder()
                        .withAttributeBuilder(new GraphQLRequestAttributeBuilderImpl())
                )
                .build();
        return frontendEngine.registerControllers(super.getExecutorTransportBuilder());
    }

    @Override
    public Info getInfo() {
        return INFO;
    }

    @Override
    public FrontendConfig getConfig() {
        return config;
    }

    public Path getDocsDirPath() {
        return docsDirPath;
    }

    public SessionServiceEmployee getSessionService() {
        return sessionService;
    }

    public StatisticService getStatisticService() {
        return frontendEngine.getStatisticService();
    }

    public Network getNetwork() {
        return frontendEngine.getNetwork();
    }

    public Controllers getControllers() {
        return frontendEngine.getControllers();
    }

    @Override
    public GraphQLRequestExecuteService getGraphQLRequestExecuteService() {
        return frontendEngine.getGraphQLRequestExecuteService();
    }

    @Override
    public void onDestroy() {
        try {
            frontendEngine.close();
        } catch (Exception e) {
            throw new ClusterException(e);
        }
    }

    private QuerySystem<Void> getFrontendEngineStartQuery() {
        FrontendEngine frontendEngine = this.frontendEngine;
        return new QuerySystem<>() {
            @Override
            public void prepare(ResourceProvider resources) {

            }

            @Override
            public Void execute(ContextTransaction context) {
                try {
                    frontendEngine.start();
                } catch (NetworkException e) {
                    throw new RuntimeException(e);
                }
                return null;
            }
        };
    }

    private QuerySystem<Void> getDocsUnpackingQuery() {
        return new QuerySystem<>() {

            @Override
            public void prepare(ResourceProvider resources) {

            }

            @Override
            public Void execute(ContextTransaction context) throws PlatformException {
                try {
                    FileUtils.deleteDirectoryIfExists(docsDirPath);
                    URL docsArchive = this.getClass().getClassLoader().getResource("docs/docs.tar");
                    if (docsArchive != null) {
                        try (TarArchiveInputStream tarStream = new TarArchiveInputStream(docsArchive.openStream())) {
                            TarArchiveEntry archiveEntry;
                            while ((archiveEntry = tarStream.getNextTarEntry()) != null) {
                                Path path = docsDirPath.resolve(archiveEntry.getName());
                                FileUtils.ensureDirectories(path.getParent());
                                if (archiveEntry.isDirectory()) {
                                    FileUtils.ensureDirectory(path);
                                } else {
                                    try (OutputStream output = Files.newOutputStream(path, StandardOpenOption.CREATE_NEW)) {
                                        tarStream.transferTo(output);
                                    }
                                }
                            }
                        }
                    }
                } catch (IOException e) {
                    throw GeneralExceptionBuilder.buildIOErrorException(e);
                }
                return null;
            }
        };
    }

    private BuilderNetwork buildNetwork(Thread.UncaughtExceptionHandler uncaughtExceptionHandler) {
        SpringConfigurationMvc.init(
                config.getWebPath(),
                config.getRequestTimeout(),
                docsDirPath,
                uncaughtExceptionHandler
        );

        HttpBuilderTransport httpBuilderTransport = new HttpBuilderTransport(
                SpringConfigurationMvc.class,
                new ConfigUploadFiles.Builder()
                        .withLocation(config.getTempPath())
                        .withMaxFileSize(-1).withMaxRequestSize(-1)
                        .withFileSizeThreshold(100*1024)//100kb
                        .build()
                );
        httpBuilderTransport.withErrorHandler(new PlatformErrorHandler(
                new ActionErrorHandlerImpl(), uncaughtExceptionHandler
        ));
        httpBuilderTransport.addFilter(new BuilderFilter(DisallowedMethodFilter.class, "/*"));
        httpBuilderTransport.addFilter(new BuilderFilter(HttpHeaderFilter.class, "/*"));
        httpBuilderTransport.setCORS(config.getCorsPolicy());
        for (ConnectorConfig connectorConfig : config.getConnectors()) {
            BuilderHttpConnector builderHttpConnector = null;
            switch (connectorConfig.getProtocol()) {
                case HTTP -> builderHttpConnector = new BuilderHttpConnector(connectorConfig.getPort())
                        .withHost(connectorConfig.getHost());
                case HTTPS -> {
                    BuilderJHttpsConnector builderHttpsConnector = new BuilderJHttpsConnector(connectorConfig.getPort());
                    builderHttpsConnector.withHost(connectorConfig.getHost());
                    BuilderJHttpsConnector.BuilderSslContextFactory builderSslContextFactory = builderHttpsConnector
                            .withSslContext(connectorConfig.getSslCertStore().toString());
                    builderSslContextFactory.resetExcludeProtocolsAndCipherSuites();//Сбрасываем встроенный запрет на небезопасные подключения
                    if (connectorConfig.getSslCertStorePassword() != null) {
                        builderSslContextFactory.setKeyStorePassword(connectorConfig.getSslCertStorePassword());
                    }
                    if (connectorConfig.getExcludeProtocols() != null) {
                        builderSslContextFactory.addExcludeProtocols(connectorConfig.getExcludeProtocols());
                    }
                    if (connectorConfig.getExcludeCipherSuites() != null) {
                        builderSslContextFactory.addExcludeCipherSuites(connectorConfig.getExcludeCipherSuites());
                    }
                    if (connectorConfig.getTruststore() != null) {
                        builderSslContextFactory.setTrustStorePath(connectorConfig.getTruststore().toString());
                        if (connectorConfig.getTruststorePassword() != null) {
                            builderSslContextFactory.setTrustStorePassword(connectorConfig.getTruststorePassword());
                        }
                    }
                    if (connectorConfig.getCrl() != null) {
                        builderSslContextFactory.setCrlPath(connectorConfig.getCrl().toString());
                    }
                    builderHttpConnector = builderSslContextFactory.build();
                }
                default -> throw new RuntimeException("Not support protocol: " + connectorConfig.getProtocol());
            }
            builderHttpConnector.withRequestHeaderSize(REQUEST_HEADER_SIZE)
                    .withResponseHeaderSize(RESPONSE_HEADER_SIZE);
            httpBuilderTransport.addConnector(builderHttpConnector);
        }
        return new BuilderNetwork()
                .withTransport(httpBuilderTransport)
                .withProtocol(new GraphqlWSProtocolBuilder(new GraphQLWSHandler(this)))
                .withProtocol(new GraphqlTransportWSProtocolBuilder(new GraphQLTransportWSHandler(this)))
                .withUncaughtExceptionHandler(uncaughtExceptionHandler);
    }

    private static class DocsAccessAdminAccessRoleInitQuery extends QuerySystem<Void> {

        private ReadableResource<AccessRoleReadable> accessRoleReadableResource;
        private RControllerAccessRolePrivileges rControllerAccessRolePrivileges;

        @Override
        public void prepare(ResourceProvider resources) throws PlatformException {
            accessRoleReadableResource = resources.getReadableResource(AccessRoleReadable.class);
            rControllerAccessRolePrivileges = resources.getQueryRemoteController(FrontendSubsystem.class, RControllerAccessRolePrivileges.class);
        }

        @Override
        public Void execute(ContextTransaction context) throws PlatformException {
            PrivilegeValue[] privilegeValues = new PrivilegeValue[]{ new PrivilegeValue(FrontendPrivilege.DOCUMENTATION_ACCESS.getUniqueKey(),
                    new AccessOperationCollection(AccessOperation.READ)) };
            HashFilter filter = new HashFilter(AccessRoleReadable.FIELD_ADMIN, true);
            accessRoleReadableResource.forEach(filter, accessRole -> rControllerAccessRolePrivileges.setPrivilegesToAccessRole(
                    accessRole.getId(), privilegeValues, context), context.getTransaction());
            return null;
        }
    }
}