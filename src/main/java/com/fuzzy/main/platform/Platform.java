package com.fuzzy.main.platform;

import com.fuzzy.main.cluster.Cluster;
import com.fuzzy.main.cluster.graphql.GraphQLEngine;
import com.fuzzy.main.platform.control.PlatformStartStop;
import com.fuzzy.main.platform.control.PlatformUpgrade;
import com.fuzzy.main.platform.exception.ClusterExceptionBuilder;
import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.QueryPool;
import com.fuzzy.main.platform.sdk.component.version.Version;
import com.fuzzy.main.platform.sdk.graphql.customfield.graphqlquery.GraphQLQueryCustomField;
import com.fuzzy.main.platform.sdk.graphql.datafetcher.PlatformDataFetcher;
import com.fuzzy.main.platform.sdk.graphql.datafetcher.PlatformDataFetcherExceptionHandler;
import com.fuzzy.main.platform.sdk.graphql.fieldconfiguration.TypeGraphQLFieldConfigurationBuilderImpl;
import com.fuzzy.main.platform.sdk.graphql.scalartype.GraphQLScalarTypePlatform;
import com.fuzzy.main.platform.sdk.struct.ClusterContext;
import com.fuzzy.main.platform.service.LogUpdateNodeConnect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Platform implements AutoCloseable {

    public static final String UUID = Platform.class.getPackage().getName();
    public static final Version VERSION = new Version(0, 0, 1, 0);

    private final static Logger log = LoggerFactory.getLogger(Platform.class);

    private static volatile Platform instant;

    private final Thread.UncaughtExceptionHandler uncaughtExceptionHandler;

    private final GraphQLEngine graphQLEngine;
    private final Cluster cluster;
    private final QueryPool queryPool;

    private Platform(Builder builder) {
        synchronized (Platform.class) {
            if (instant != null) throw new IllegalStateException();

            this.uncaughtExceptionHandler = builder.uncaughtExceptionHandler;
            this.graphQLEngine = builder.graphQLEngineBuilder.build();
            this.cluster = builder.clusterBuilder
                    .withContext(new ClusterContext(this, builder.clusterContext))
                    .withExceptionBuilder(new ClusterExceptionBuilder())
                    .withListenerUpdateConnect(new LogUpdateNodeConnect())
                    .build();
            this.queryPool = new QueryPool(builder.uncaughtExceptionHandler);

            instant = this;
        }
    }

    public void install() throws PlatformException {
        new PlatformUpgrade(this).install();
    }

    public void upgrade() throws Exception {
        new PlatformUpgrade(this).upgrade();
    }

    public void checkUpgrade() throws Exception {
        new PlatformUpgrade(this).checkUpgrade();
    }

    public void start() throws PlatformException {
        new PlatformStartStop(this).start(false);
    }

    public void stop() throws PlatformException {
        new PlatformStartStop(this).stop(false);
    }

    public Thread.UncaughtExceptionHandler getUncaughtExceptionHandler() {
        return uncaughtExceptionHandler;
    }

    public Cluster getCluster() {
        return cluster;
    }

    public GraphQLEngine getGraphQLEngine() {
        return graphQLEngine;
    }

    public QueryPool getQueryPool() {
        return queryPool;
    }

    @Override
    public void close() {
        try {
            // дождемся конца всех работ с БД, иначе может быть креш в RocksDB
            queryPool.shutdownAwait();
        } catch (InterruptedException ignore) {
        }
        cluster.close();
        instant = null;
    }


    public static Platform get() {
        return instant;
    }

    public static class Builder {

        public final Thread.UncaughtExceptionHandler uncaughtExceptionHandler;

        public final Cluster.Builder clusterBuilder;

        public final GraphQLEngine.Builder graphQLEngineBuilder;

        private Object clusterContext;

        public Builder() {
            this(
                    new Thread.UncaughtExceptionHandler() {
                        @Override
                        public void uncaughtException(Thread t, Throwable e) {
                            log.error("Uncaught exception", e);
                        }
                    }
            );
        }

        public Builder(Thread.UncaughtExceptionHandler uncaughtExceptionHandler) {
            this.uncaughtExceptionHandler = uncaughtExceptionHandler;

            this.clusterBuilder = new Cluster.Builder(uncaughtExceptionHandler);

            this.graphQLEngineBuilder = new GraphQLEngine.Builder()
                    .withFieldConfigurationBuilder(new TypeGraphQLFieldConfigurationBuilderImpl())
                    .withDataFetcher(PlatformDataFetcher.class)
                    .withDataFetcherExceptionHandler(new PlatformDataFetcherExceptionHandler())
                    .withPrepareCustomField(new GraphQLQueryCustomField())
                    .withTypeScalar(GraphQLScalarTypePlatform.GraphQLDuration)
                    .withTypeScalar(GraphQLScalarTypePlatform.GraphQLGOutputFile);
        }

        public Builder withClusterContext(Object clusterContext) {
            this.clusterContext = clusterContext;
            return this;
        }

        public Platform build() {
            return new Platform(this);
        }
    }
}
