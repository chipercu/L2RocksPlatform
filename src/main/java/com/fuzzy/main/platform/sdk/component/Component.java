package com.fuzzy.main.platform.sdk.component;

import com.fuzzy.main.cluster.core.service.transport.executor.ComponentExecutorTransportImpl;
import com.fuzzy.main.cluster.graphql.remote.graphql.executor.RControllerGraphQLExecutorImpl;
import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.sdk.dbprovider.ComponentDBProvider;
import com.fuzzy.main.platform.sdk.exception.GeneralExceptionBuilder;
import com.fuzzy.main.platform.sdk.remote.QueryRemotes;
import com.fuzzy.main.platform.sdk.struct.ClusterContext;
import com.fuzzy.main.platform.sdk.struct.querypool.QuerySystem;
import com.fuzzy.main.platform.sdk.subscription.GraphQLSubscribeEvent;
import com.fuzzy.main.rdao.database.anotation.Entity;
import com.fuzzy.main.rdao.database.domainobject.DomainObjectSource;
import com.fuzzy.main.rdao.database.exception.DatabaseException;
import com.fuzzy.main.rdao.database.exception.SchemaException;
import com.fuzzy.main.rdao.database.maintenance.ChangeMode;
import com.fuzzy.main.rdao.database.maintenance.SchemaService;
import com.fuzzy.main.rdao.database.provider.DBProvider;
import com.fuzzy.main.rdao.database.schema.Schema;
import com.fuzzy.main.rdao.database.schema.StructEntity;
import org.reflections.Reflections;

import java.util.HashSet;
import java.util.Set;

public abstract class Component extends com.fuzzy.main.cluster.struct.Component {

    protected DBProvider dbProvider;
    protected DomainObjectSource domainObjectSource;
    private Schema schema;

    private QueryRemotes queryRemotes;

    private GraphQLSubscribeEvent graphQLSubscribeEvent;
    private RControllerGraphQLExecutorImpl rControllerGraphQLExecutor;

    protected DBProvider initDBProvider() throws PlatformException {
        if (dbProvider != null) {
            return dbProvider;
        }
        return new ComponentDBProvider(getCluster(), this);
    }

    @Override
    protected void registerComponent() {
        super.registerComponent();
        this.rControllerGraphQLExecutor.init();
        onCreate();
    }

    @Override
    protected ComponentExecutorTransportImpl.Builder getExecutorTransportBuilder() {
        ClusterContext clusterContext = getCluster().getContext();
        this.rControllerGraphQLExecutor = clusterContext.platform.getGraphQLEngine().buildRemoteControllerGraphQLExecutor(this);//Обработчик GraphQL запросов
        return super.getExecutorTransportBuilder()
                .withRemoteController(rControllerGraphQLExecutor);
    }

    @Override
    public void destroy() {
        onDestroy();
        super.destroy();
    }

    public void onCreate() {
    }

    public ComponentType getType() {
        return null;
    }

    public QuerySystem<Void> onInstall() {
        return null;
    }

    public QuerySystem<Void> onStart() {
        return null;
    }

    public QuerySystem<Void> onStop() {
        return null;
    }

    public void onDestroy() {
    }

    public void onStarting() throws PlatformException {
        try {
            Set<StructEntity> domains = new HashSet<>();
            for (Class domainObjectClass : new Reflections(getInfo().getUuid()).getTypesAnnotatedWith(Entity.class, true)) {
                domains.add(Schema.getEntity(domainObjectClass));
            }
            schema.checkSubsystemIntegrity(domains, getInfo().getUuid());
            buildSchemaService()
                    .setChangeMode(ChangeMode.CREATION)
                    .setValidationMode(false)
                    .execute();
        } catch (DatabaseException e) {
            throw GeneralExceptionBuilder.buildDatabaseException(e);
        }

        this.graphQLSubscribeEvent = new GraphQLSubscribeEvent(this);
    }

    public SchemaService buildSchemaService() {
        return new SchemaService(getDbProvider())
                .setNamespace(getInfo().getUuid())
                .setSchema(getSchema());
    }

    public final DBProvider getDbProvider() {
        return dbProvider;
    }

    public final DomainObjectSource getDomainObjectSource() {
        return domainObjectSource;
    }

    public Schema getSchema() {
        return schema;
    }

    public void initialize() throws PlatformException {
        if (!getClass().getPackage().getName().equals(getInfo().getUuid())) {
            throw new RuntimeException(getClass() + " is not correspond to uuid: " + getInfo().getUuid());
        }

        this.dbProvider = initDBProvider();
        this.schema = initializeSchema(dbProvider);

        this.domainObjectSource = new DomainObjectSource(dbProvider, true);

        this.queryRemotes = new QueryRemotes(this);
    }

    //TODO Ulitin V. Временное решение - после переноса механизма обновления в платформу - убрать
    public void reloadSchema(DBProvider dbProvider) {
        try {
            this.schema = Schema.read(dbProvider);
        } catch (DatabaseException e) {
            throw new SchemaException(e);
        }
    }

    public final QueryRemotes getQueryRemotes() {
        return queryRemotes;
    }

    public final GraphQLSubscribeEvent getGraphQLSubscribeEvent() {
        return graphQLSubscribeEvent;
    }

    private Schema initializeSchema(DBProvider dbProvider) {
        try {
            Schema schema;
            if (Schema.exists(dbProvider)) {
                schema = Schema.read(dbProvider);
            } else {
                schema = Schema.create(dbProvider);
            }
            for (Class domainObjectClass : new Reflections(getInfo().getUuid()).getTypesAnnotatedWith(Entity.class, true)) {
                Schema.resolve(domainObjectClass); //todo убрать resolve когда переведу функционал из StructEntity
            }
            return schema;
        } catch (DatabaseException e) {
            throw new SchemaException(e);
        }
    }
}
