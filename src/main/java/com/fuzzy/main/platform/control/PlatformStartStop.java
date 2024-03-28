package com.fuzzy.main.platform.control;

import com.fuzzy.main.platform.Platform;
import com.fuzzy.main.platform.component.database.DatabaseComponent;
import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.Query;
import com.fuzzy.main.platform.querypool.QueryTransaction;
import com.fuzzy.main.platform.querypool.ResourceProvider;
import com.fuzzy.main.platform.sdk.component.Component;
import com.fuzzy.main.platform.sdk.component.ComponentType;
import com.fuzzy.main.platform.sdk.context.ContextTransaction;
import com.fuzzy.main.platform.sdk.context.impl.ContextTransactionImpl;
import com.fuzzy.main.platform.sdk.context.source.impl.SourceSystemImpl;
import com.fuzzy.main.platform.sdk.domainobject.module.ModuleReadable;
import com.fuzzy.main.platform.sdk.exception.GeneralExceptionBuilder;
import com.fuzzy.main.platform.sdk.struct.querypool.QuerySystem;
import com.fuzzy.main.rdao.database.exception.DatabaseException;
import com.fuzzy.main.rdao.database.schema.Schema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class PlatformStartStop {

    private final static Logger log = LoggerFactory.getLogger(PlatformStartStop.class);

    private final Platform platform;

    public PlatformStartStop(Platform platform) {
        this.platform = platform;
    }

    /**
     * Запуск происходит в несколько фаз:
     * 1) onStarting - инициализирующая фаза старта компонента
     * 2) onStart - Все необходимые фазы пройденны - пользовательский запуск
     *
     * checkUpgrade - флаг указывающий что старт "урезанный", для проверки обновления
     * @throws PlatformException
     */
    public void start(boolean checkUpgrade) throws PlatformException {
        //initialize
        initialize();

        //TODO Ulitin V. - где то потерялась валидация схемы база данных!!!

        //onStarting
        for (Component component : platform.getCluster().getDependencyOrderedComponentsOf(Component.class)) {
            component.onStarting();
        }


        //Режим обновления, упрощенный режим старт/стоп
        if (checkUpgrade) {
            return;
        }

        //onStart
        DatabaseComponent databaseComponent = platform.getCluster().getAnyLocalComponent(DatabaseComponent.class);
        List<QuerySystem<Void>> startQueries = platform.getCluster().getDependencyOrderedComponentsOf(Component.class)
                .stream()
                .sorted((o1, o2) -> {//Необходимо, что бы фронт запустился самым последним
                    if (o1.getType() == ComponentType.FRONTEND) {
                        return 1;
                    } else if (o2.getType() == ComponentType.FRONTEND) {
                        return -1;
                    } else {
                        return 0;
                    }
                })
                .map(component -> component.onStart())
                .filter(query -> query != null)
                .collect(Collectors.toList());
        try {
            platform.getQueryPool().execute(databaseComponent, new Query<Void>() {

                @Override
                public void prepare(ResourceProvider resources) throws PlatformException {
                    for (QuerySystem<Void> query : startQueries) {
                        query.prepare(resources);
                    }
                }

                @Override
                public Void execute(QueryTransaction transaction) throws PlatformException {
                    ContextTransaction contextTransaction = new ContextTransactionImpl(new SourceSystemImpl(), transaction);
                    for (QuerySystem<Void> query : startQueries) {
                        query.execute(contextTransaction);
                    }
                    return null;
                }
            }).get();
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof PlatformException) {
                throw (PlatformException) cause;
            } else {
                throw new RuntimeException(e);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void initialize() throws PlatformException {
        for (Component component : platform.getCluster().getDependencyOrderedComponentsOf(Component.class)) {
            if (component.getDbProvider() == null) {
                component.initialize();
            }
        }

        //Инициализируем ModuleReadable
        try {
            DatabaseComponent databaseSubsystem = platform.getCluster().getAnyLocalComponent(DatabaseComponent.class);
            Schema schema = Schema.read(databaseSubsystem.getRocksDBProvider());
//            log.warn("Schema on start: " + schema.getDbSchema().toTablesJsonString());
            log.warn("Schema on start: " + schema.getDbSchema().getTables().size());
            Schema.resolve(ModuleReadable.class);
        } catch (DatabaseException e) {
            throw GeneralExceptionBuilder.buildDatabaseException(e);
        }
    }

    /**

     * @param checkUpgrade - флаг указывающий что старт был "урезанный", для проверки обновления, поэтому и onStop вызывать ен следует
     * @throws PlatformException
     */
    public void stop(boolean checkUpgrade) throws PlatformException {
        //Режим проверки обновления, упрощенный режим старт/стоп
        if (checkUpgrade) {
            return;
        }

        List<Component> reverseDependencyOrderedComponents = new ArrayList(platform.getCluster().getDependencyOrderedComponentsOf(Component.class));
        Collections.reverse(reverseDependencyOrderedComponents);
        Collections.sort(reverseDependencyOrderedComponents, (o1, o2) -> {//Необходимо, что бы фронт остановился самым первым
            if (o1.getType() == ComponentType.FRONTEND) {
                return -1;
            } else if (o2.getType() == ComponentType.FRONTEND) {
                return 1;
            } else {
                return 0;
            }
        });
        List<QuerySystem<Void>> stopQueries = reverseDependencyOrderedComponents
                .stream()
                .map(component -> component.onStop())
                .filter(query -> query != null)
                .collect(Collectors.toList());
        try {
            DatabaseComponent databaseComponent = platform.getCluster().getAnyLocalComponent(DatabaseComponent.class);
            platform.getQueryPool().execute(databaseComponent, new Query<Void>() {
                @Override
                public void prepare(ResourceProvider resources) throws PlatformException {
                    for (QuerySystem<Void> query : stopQueries) {
                        query.prepare(resources);
                    }
                }

                @Override
                public Void execute(QueryTransaction transaction) throws PlatformException {
                    ContextTransaction contextTransaction = new ContextTransactionImpl(new SourceSystemImpl(), transaction);
                    for (QuerySystem<Void> query : stopQueries) {
                        query.execute(contextTransaction);
                    }
                    return null;
                }
            }).get();
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof PlatformException) {
                throw (PlatformException) cause;
            } else {
                throw new RuntimeException(e);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
