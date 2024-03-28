package com.fuzzy.subsystems;

import com.fuzzy.main.cluster.*;
import com.fuzzy.main.cluster.component.service.ServiceComponent;
import com.fuzzy.main.cluster.core.service.componentuuid.ComponentUuidManager;
import com.fuzzy.main.cluster.core.service.transport.network.grpc.GrpcNetworkTransit;
import com.fuzzy.main.cluster.core.service.transport.network.grpc.GrpcRemoteNode;
import com.fuzzy.main.cluster.exception.ClusterException;
import com.fuzzy.main.platform.Platform;
import com.fuzzy.main.platform.component.database.DatabaseComponent;
import com.fuzzy.main.platform.component.database.DatabaseConsts;
import com.fuzzy.main.platform.component.database.configure.DatabaseConfigure;
import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.QueryPool;
import com.fuzzy.main.platform.sdk.component.Component;
import com.fuzzy.main.platform.sdk.component.version.Version;
import com.fuzzy.main.platform.sdk.context.impl.ContextImpl;
import com.fuzzy.main.platform.sdk.context.source.impl.SourceSystemImpl;
import com.fuzzy.main.platform.sdk.remote.packer.RemotePackerContext;
import com.fuzzy.main.platform.sdk.threadpool.ThreadPool;
import com.fuzzy.main.platform.update.core.ModuleUpdateEntity;
import com.fuzzy.main.platform.update.core.UpdateService;
import com.fuzzy.main.rdao.database.maintenance.ChangeMode;
import com.fuzzy.main.rdao.database.maintenance.SchemaService;
import com.fuzzy.main.rdao.database.schema.Schema;
import com.fuzzy.subsystem.core.securitylog.CoreEvent;
import com.fuzzy.subsystem.core.securitylog.CoreTarget;
import com.fuzzy.subsystem.database.DatabaseComponentExtensionImpl;
import com.fuzzy.subsystem.database.DatabaseConfig;
import com.fuzzy.subsystem.telegram.TelegramBotService;
import com.fuzzy.subsystems.exception.CompatibilityException;
import com.fuzzy.subsystems.graphql.customfieldargument.EmployeeAuthContextArgument;
import com.fuzzy.subsystems.graphql.customfieldargument.GRequestStatistic;
import com.fuzzy.subsystems.graphql.customfieldargument.SubsystemEnvironment;
import com.fuzzy.subsystems.scheduler.Scheduler;
import com.fuzzy.subsystems.security.SecurityLog;
import com.fuzzy.subsystems.security.struct.data.event.SyslogStructDataEvent;
import com.fuzzy.subsystems.security.struct.data.target.SyslogStructDataTarget;
import com.fuzzy.subsystems.subsystem.Subsystem;
import com.fuzzy.subsystems.utils.ManifestUtils;
import net.minidev.json.parser.ParseException;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Subsystems implements AutoCloseable {

    public static final String UUID = "com.fuzzy.subsystems";
    public static final Version VERSION = readVersion();

    private final TelegramBotService telegramBotService = new TelegramBotService();

    private final static Logger log = LoggerFactory.getLogger(Subsystems.class);

    private static class SubsystemBuilder extends ComponentBuilder {

        private final Class<? extends Subsystem> subSystemClass;

        SubsystemBuilder(Class<? extends Subsystem> subSystemClass) {
            super(subSystemClass);
            this.subSystemClass = subSystemClass;
        }

        @Override
        protected Component build() throws ClusterException {
            try {
                return subSystemClass.getDeclaredConstructor().newInstance();
            } catch (ReflectiveOperationException ex) {
                throw new ClusterException(ex);
            }
        }
    }

    private static Subsystems subsystems;

    private final Platform platform;

    private final SubsystemsConfig config;

    private final ThreadPool threadPool;
    private final Scheduler scheduler;

    private Schema schema;

    protected Subsystems(Builder builder) throws Exception {
        if (subsystems != null) {
            throw new RuntimeException("Instance of Subsystems already exists.");
        }

        SecurityLog.info(
                new SyslogStructDataEvent(CoreEvent.System.TYPE_INITIALIZE),
                new SyslogStructDataTarget(CoreTarget.TYPE_SYSTEM),
                new ContextImpl(new SourceSystemImpl())
        );

        Set<Class<? extends Subsystem>> subsystemClasses = builder.subsystemClasses;
        Thread.UncaughtExceptionHandler uncaughtExceptionHandler = builder.uncaughtExceptionHandler;

        scheduler = new Scheduler(uncaughtExceptionHandler);

        Platform.Builder platformBuilder = new Platform.Builder(uncaughtExceptionHandler);
        log.info("Subsystems creating ver.{}...", VERSION);

        this.config = builder.config;
        this.threadPool = new ThreadPool(uncaughtExceptionHandler);

        platformBuilder.graphQLEngineBuilder
                .withSDKPackage(Subsystems.UUID)
                .withCustomArgument(new SubsystemEnvironment(this))
                .withCustomArgument(new EmployeeAuthContextArgument())
                .withCustomArgument(new GRequestStatistic.Builder(this));

        subsystems = this;

        log.info("Cluster.Builder creating...");
        ClusterConfig clusterConfig = ClusterConfig.load(config);
        Cluster.Builder clusterBuilder = platformBuilder.clusterBuilder
                .withRemotePackerObject(new RemotePackerContext());
        NetworkConfig networkConfig = null;
        ComponentsConfig componentsConfig = null;
        if (clusterConfig != null) {
            networkConfig = clusterConfig.getNetworkConfig();
            componentsConfig = clusterConfig.getComponentsConfig();
        }
        if (networkConfig != null) {
            GrpcNetworkTransit.Builder networkTransitBuilder = new GrpcNetworkTransit.Builder(
                    networkConfig.getName(), networkConfig.getPort(), uncaughtExceptionHandler);
            if (networkConfig.isSSL()) {
                networkTransitBuilder.withTransportSecurity(
                        networkConfig.getFileContentCertChain(),
                        networkConfig.getFileContentPrivateKey(),
                        networkConfig.getFileContentTrustCertificates()
                );
            }
            for (String node : networkConfig.getNodes()) {
                networkTransitBuilder.addTarget(new GrpcRemoteNode.Builder(node).build());
            }
            clusterBuilder.withNetworkTransport(networkTransitBuilder);
        }

        DatabaseConfig databaseConfig = new DatabaseConfig.Builder(DatabaseConsts.UUID, config).build();
        clusterBuilder.withComponent(new DatabaseComponent(new DatabaseConfigure.Builder()
                .withPath(databaseConfig.getDbPath())
                .withExtension(new DatabaseComponentExtensionImpl(databaseConfig, scheduler))
                .build()));
        clusterBuilder.withComponent(new ServiceComponent());

        if (componentsConfig != null && componentsConfig.getIncluded() != null) {
            ComponentUuidManager componentUuidManager = new ComponentUuidManager();
            Map<String, Class<? extends Subsystem>> components = subsystemClasses.stream()
                    .collect(Collectors.toMap(componentUuidManager::getUuid, Function.identity()));
            for (String componentUuid : componentsConfig.getIncluded()) {
                Class<? extends Subsystem> component = components.get(componentUuid);
                if (component == null) {
                    throw new RuntimeException("Required component not found: " + componentUuid);
                }
                clusterBuilder.withComponentIfNotExist(new SubsystemBuilder(component));
            }
        } else if (componentsConfig != null && componentsConfig.getExcluded() != null) {
            ComponentUuidManager componentUuidManager = new ComponentUuidManager();
            for (Class<? extends Subsystem> subsystemClass : subsystemClasses) {
                if (!componentsConfig.getExcluded().contains(componentUuidManager.getUuid(subsystemClass))) {
                    clusterBuilder.withComponentIfNotExist(new SubsystemBuilder(subsystemClass));
                }
            }
        } else {
            for (Class<? extends Subsystem> subsystemClass : subsystemClasses) {
                clusterBuilder.withComponentIfNotExist(new SubsystemBuilder(subsystemClass));
            }
        }

        platformBuilder.withClusterContext(this);

        this.platform = platformBuilder.build();
    }

    public static Subsystems getInstance() {
        return subsystems;
    }

    public SubsystemsConfig getConfig() {
        return config;
    }

    public Thread.UncaughtExceptionHandler getUncaughtExceptionHandler() {
        return platform.getUncaughtExceptionHandler();
    }

    public Platform getPlatform() {
        return platform;
    }

    public TelegramBotService getTelegramBot(){
        return telegramBotService;
    }

    public Cluster getCluster() {
        return platform.getCluster();
    }

    public QueryPool getQueryPool() {
        return platform.getQueryPool();
    }

    public ThreadPool getThreadPool() {
        return threadPool;
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    public void upgrade() throws Exception {
        log.info("Subsystems upgrade...");
        platform.upgrade();
        log.info("Subsystems upgrade... complete");
    }

    public void checkUpgrade() throws Exception {
        log.info("Subsystems check upgrade...");
        platform.checkUpgrade();
        log.info("Subsystems check upgrade... complete");
    }

    public void install() throws PlatformException {
        log.info("Subsystems installing...");
        platform.install();
    }

    public void update(ModuleUpdateEntity... updates) throws Exception {
        if (updates == null || updates.length == 0) {
            return;
        }
        log.info("Subsystems updating...");

        for (Component component : platform.getCluster().getDependencyOrderedComponentsOf(Component.class)) {
            component.initialize();
        }
        List<Subsystem> modules = platform.getCluster().getDependencyOrderedComponentsOf(Subsystem.class);
        for (Subsystem module : modules) {
            String moduleUuid = module.getInfo().getUuid();
            Arrays.stream(updates)
                    .filter(u -> u.getComponentUuid().equals(moduleUuid))
                    .findAny()
                    .ifPresent(moduleUpdateEntity -> moduleUpdateEntity.setComponent(module));
        }
        DatabaseComponent databaseSubsystem = platform.getCluster().getAnyLocalComponent(DatabaseComponent.class);
        databaseSubsystem.getDomainObjectSource().executeTransactional(transaction -> {
                    UpdateService.beforeUpdateComponents(transaction, updates);
                    UpdateService.updateComponents(transaction, updates);
                    for (Component component : platform.getCluster().getDependencyOrderedComponentsOf(Component.class)) {
                        component.reloadSchema(transaction.getDbProvider());
                    }
                }
        );
    }

    public void start() throws Exception {
        log.info("Subsystems starting...");

        //Валидируем зависимости запускаемых версии компонентов
        validateCompatibility();

        platform.start();

        log.info("Subsystems started.");
        SecurityLog.info(
                new SyslogStructDataEvent(CoreEvent.System.TYPE_START),
                new SyslogStructDataTarget(CoreTarget.TYPE_SYSTEM),
                new ContextImpl(new SourceSystemImpl())
        );
    }

    public void stop() throws Exception {
        log.info("Subsystems stopping...");

        platform.stop();
        scheduler.shutdownAwait();

        log.info("Subsystems stopped.");
        SecurityLog.info(
                new SyslogStructDataEvent(CoreEvent.System.TYPE_STOP),
                new SyslogStructDataTarget(CoreTarget.TYPE_SYSTEM),
                new ContextImpl(new SourceSystemImpl())
        );
    }

    public void remove() {
        //todo remove
        log.info("Subsystems removing...");

        // Удаляем схему БД в начале, поскольку в конце не будет компонента БД.
        // Будем надеяться, что никому в процесе удаления не понадобиться инфа о модулях
        try {
            DatabaseComponent databaseSubsystem = platform.getCluster().getAnyLocalComponent(DatabaseComponent.class);
            new SchemaService(databaseSubsystem.getDbProvider())
                    .setNamespace(UUID)
                    .setSchema(schema)
                    .setChangeMode(ChangeMode.REMOVAL)
                    .setValidationMode(false)
                    .execute();
        } catch (Exception ex) {
            log.error("Subsystems removing crashes", ex);
        }

        List<Subsystem> modules = platform.getCluster().getDependencyOrderedComponentsOf(Subsystem.class);
        for (int i = modules.size() - 1; i > -1; --i) {
            Subsystem module = modules.get(i);
            try {
                module.remove();
                platform.getCluster().removeComponent(module);
            } catch (Exception ex) {
                log.error("Plugin removing crashes", ex);
            }
        }

        log.info("Subsystems removed.");
    }

    @Override
    public void close() {
        platform.close();
        subsystems = null;
    }

    private void validateCompatibility() throws ClusterException {
        for (Subsystem controllableSubsystem : platform.getCluster().getDependencyOrderedComponentsOf(Subsystem.class)) {
            if (!controllableSubsystem.getInfo().getSdkVersion().isCompatibleWith(VERSION)) {
                throw new CompatibilityException(controllableSubsystem, this.getClass(), VERSION);
            }

            //TODO Ulitin V. Необходимо поправить
//            for (Info.DependenceVersion dependence : controllableSubsystem.getInfo().getDependenceVersions()) {
//                Subsystem subsystem = platform.getCluster().getAnyComponent(dependence.subsystem);
//                if (!dependence.version.isCompatibleWith(subsystem.getInfo().getVersion())) {
//                    throw new CompatibilityException(controllableSubsystem, subsystem.getClass(), subsystem.getInfo().getVersion());
//                }
//            }
        }
    }

    private static Version readVersion() {
        try {
            return ManifestUtils.readSdkVersion();
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static class Builder {

        public final SubsystemsConfig config;

        private final Set<Class<? extends Subsystem>> subsystemClasses;
        private final Thread.UncaughtExceptionHandler uncaughtExceptionHandler;

        public Builder(SubsystemsConfig config, Thread.UncaughtExceptionHandler uncaughtExceptionHandler) {
            this.config = config;
            this.subsystemClasses = new HashSet<>();
            withFindAndAddSubsystemClasses("com.fuzzy");
            this.uncaughtExceptionHandler = uncaughtExceptionHandler;
        }

        public Builder(SubsystemsConfig config, Thread.UncaughtExceptionHandler uncaughtExceptionHandler, Collection<Class<? extends Subsystem>> subsystemClasses) {
            this.config = config;
            this.subsystemClasses = new HashSet<>();
            this.uncaughtExceptionHandler = uncaughtExceptionHandler;
            this.subsystemClasses.addAll(subsystemClasses);
        }

        public Builder withFindAndAddSubsystemClasses(String prefix) {
            subsystemClasses.addAll(new Reflections(prefix).getSubTypesOf(Subsystem.class));
            return this;
        }

        public Builder withAddSubsystemClasses(Collection<Class<? extends Subsystem>> subsystemClasses) {
            this.subsystemClasses.addAll(subsystemClasses);
            return this;
        }

        public Subsystems build() throws Exception {
            return new Subsystems(this);
        }
    }
}
