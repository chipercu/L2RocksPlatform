package com.fuzzy.platform.control;

import com.fuzzy.database.anotation.Entity;
import com.fuzzy.database.domainobject.DomainObject;
import com.fuzzy.database.domainobject.DomainObjectSource;
import com.fuzzy.database.domainobject.Transaction;
import com.fuzzy.database.domainobject.filter.EmptyFilter;
import com.fuzzy.database.domainobject.filter.HashFilter;
import com.fuzzy.database.domainobject.iterator.IteratorEntity;
import com.fuzzy.database.exception.DatabaseException;
import com.fuzzy.database.exception.SchemaException;
import com.fuzzy.database.maintenance.SchemaService;
import com.fuzzy.database.provider.DBProvider;
import com.fuzzy.database.schema.Schema;
import com.fuzzy.database.schema.StructEntity;
import com.fuzzy.platform.Platform;
import com.fuzzy.platform.component.database.DatabaseComponent;
import com.fuzzy.platform.control.PlatformStartStop;
import com.fuzzy.platform.exception.DowngradingException;
import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.Query;
import com.fuzzy.platform.querypool.QueryTransaction;
import com.fuzzy.platform.querypool.ResourceProvider;
import com.fuzzy.platform.sdk.component.Component;
import com.fuzzy.platform.sdk.component.Info;
import com.fuzzy.platform.sdk.component.version.Version;
import com.fuzzy.platform.sdk.context.ContextTransaction;
import com.fuzzy.platform.sdk.context.impl.ContextTransactionImpl;
import com.fuzzy.platform.sdk.context.source.impl.SourceSystemImpl;
import com.fuzzy.platform.sdk.domainobject.module.ModuleEditable;
import com.fuzzy.platform.sdk.domainobject.module.ModuleReadable;
import com.fuzzy.platform.sdk.exception.GeneralExceptionBuilder;
import com.fuzzy.platform.sdk.struct.querypool.QuerySystem;
import com.fuzzy.platform.update.core.ModuleUpdateEntity;
import com.fuzzy.platform.update.core.UpdateService;
import com.fuzzy.platform.update.core.UpgradeAction;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class PlatformUpgrade {

    private final static Logger log = LoggerFactory.getLogger(PlatformUpgrade.class);

    private final Platform platform;

    public PlatformUpgrade(Platform platform) {
        this.platform = platform;
    }

    public void install() throws PlatformException {
        try {
            DatabaseComponent databaseSubsystem = platform.getCluster().getAnyLocalComponent(DatabaseComponent.class);
            databaseSubsystem.initialize();
            DBProvider provider = databaseSubsystem.getRocksDBProvider();
            Schema schema = Schema.read(provider);

            new DomainObjectSource(databaseSubsystem.getRocksDBProvider(), true).executeTransactional(transaction -> {
                schema.createTable(new StructEntity(ModuleReadable.class));

                //Регистрируем и устанавливаем модули
                List<Component> components = platform.getCluster().getDependencyOrderedComponentsOf(Component.class);
                for (Component component : components) {
                    installComponent(component, transaction);
                }
            });

            fireOnInstall(databaseSubsystem,
                    platform.getCluster().getDependencyOrderedComponentsOf(Component.class)
                            .stream()
                            .filter(component -> (component.getInfo() instanceof Info))
                            .filter(component -> ((Info) component.getInfo()).getVersion() != null)
                            .collect(Collectors.toList())
            );
        } catch (DatabaseException e) {
            throw GeneralExceptionBuilder.buildDatabaseException(e);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }


    public void upgrade() throws Exception {
//		DatabaseComponent databaseSubsystem = platform.getCluster().getAnyComponent(DatabaseComponent.class);
//		DBProvider provider = databaseSubsystem.getRocksDBProvider();
//		Schema schema = Schema.read(provider);

        List<Component> modules = platform.getCluster().getDependencyOrderedComponentsOf(Component.class);
        //Грузим сперва DatabaseSubsystem
        DatabaseComponent databaseComponent = platform.getCluster().getAnyLocalComponent(DatabaseComponent.class);
        databaseComponent.initialize();
        log.info("Database initialized...");

        new DomainObjectSource(databaseComponent.getRocksDBProvider(), true).executeTransactional(transaction -> {
            ensureSchema(transaction.getDbProvider());
            updateInstallModules(modules, transaction);
            removeRedundantModules(modules, transaction);

            //После обновления - перечитываем схему
            for (Component component : platform.getCluster().getDependencyOrderedComponentsOf(Component.class)) {
                component.reloadSchema(transaction.getDbProvider());
            }
        });

        new PlatformStartStop(platform).start(true);
        new PlatformStartStop(platform).stop(true);
    }

    private void fireOnInstall(DatabaseComponent databaseComponent, List<Component> components) throws PlatformException {
        try {
            List<QuerySystem<Void>> installQueries = components.stream()
                    .map(component -> component.onInstall())
                    .filter(query -> query != null)
                    .collect(Collectors.toList());
            if (!installQueries.isEmpty()) {
                platform.getQueryPool().execute(databaseComponent, new Query<Void>() {

                    @Override
                    public void prepare(ResourceProvider resources) throws PlatformException {
                        for (QuerySystem<Void> query : installQueries) {
                            query.prepare(resources);
                        }
                    }

                    @Override
                    public Void execute(QueryTransaction transaction) throws PlatformException {
                        ContextTransaction contextTransaction = new ContextTransactionImpl(new SourceSystemImpl(), transaction);
                        for (QuerySystem<Void> query : installQueries) {
                            query.execute(contextTransaction);
                        }
                        return null;
                    }
                }).get();
            }
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof PlatformException pe) {
                throw pe;
            } else {
                throw new RuntimeException(e);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    public void checkBeforeUpgrade() throws Exception {
        List<Component> modules = platform.getCluster().getDependencyOrderedComponentsOf(Component.class);
        DatabaseComponent databaseComponent = platform.getCluster().getAnyLocalComponent(DatabaseComponent.class);
        databaseComponent.initialize();
        log.info("Database initialized...");
        new DomainObjectSource(databaseComponent.getRocksDBProvider(), true).executeTransactional(transaction -> checkBeforeUpgradeInstallModules(modules, transaction));
    }


    private void ensureSchema(DBProvider dbProvider) throws DatabaseException {
        if (!Schema.exists(dbProvider)) {
            Schema.create(dbProvider);
        }
    }

    private void installComponent(Component component, Transaction transaction) throws DatabaseException {
        if (!(component.getInfo() instanceof Info)) {
            return;
        }

        Info info = (Info) component.getInfo();
        if (info.getVersion() == null) {
            return;
        }

        //Регистрируем компонент
        ModuleEditable moduleEditable = transaction.create(ModuleEditable.class);
        moduleEditable.setUuid(info.getUuid());
        moduleEditable.setVersion(info.getVersion());
        transaction.save(moduleEditable);

        //Создаем доменные сущности
        Set<Class<? extends DomainObject>> objects = new HashSet<>();
        for (Class domainObjectClass : new Reflections(info.getUuid()).getTypesAnnotatedWith(Entity.class, true)) {
            objects.add(domainObjectClass);
        }
        try {
            SchemaService.install(objects, transaction.getDbProvider());
        } catch (DatabaseException e) {
            throw new SchemaException(e);
        }
    }

    private void updateInstallModules(List<Component> modules, Transaction transaction) throws Exception {
        Schema.resolve(ModuleReadable.class);

        List<Component> componentsForInstall = new ArrayList<>();
        List<ModuleUpdateEntity> modulesForUpdate = new ArrayList<>();
        for (Component module : modules) {
            ModuleEditable moduleInDB = getModuleByUuid(module.getInfo().getUuid(), transaction);
            UpgradeAction upgradeAction = getUpgradeAction(module, moduleInDB);
            switch (upgradeAction) {
                case NONE:
                    log.warn("Module " + module.getInfo().getUuid() + " has actual version");
                    break;
                case INSTALL:
                    log.warn("Module " + module.getInfo().getUuid() + " installing");
                    new PlatformUpgrade(platform).installComponent(module, transaction);
                    componentsForInstall.add(module);
                    break;
                case UPDATE:
                    log.warn("Module " + module.getInfo().getUuid() + " ready for update");
                    ModuleUpdateEntity updateEntity = new ModuleUpdateEntity(moduleInDB.getVersion(),
                            ((Info) module.getInfo()).getVersion(),
                            module.getInfo().getUuid());
                    updateEntity.setComponent(module);
                    modulesForUpdate.add(updateEntity);
                    break;
            }
        }
        for (Component module : modules) {
            module.initialize();
        }

        DatabaseComponent databaseComponent = platform.getCluster().getAnyLocalComponent(DatabaseComponent.class);
        fireOnInstall(databaseComponent, componentsForInstall);

        update(modulesForUpdate, transaction);
    }


    private void checkBeforeUpgradeInstallModules(List<Component> modules, Transaction transaction) throws PlatformException {
        Schema.resolve(ModuleReadable.class);
        List<ModuleUpdateEntity> modulesForUpdate = new ArrayList<>();
        for (Component module : modules) {
            ModuleEditable moduleInDB = getModuleByUuid(module.getInfo().getUuid(), transaction);
            UpgradeAction upgradeAction = getUpgradeAction(module, moduleInDB);
            if (upgradeAction == UpgradeAction.UPDATE) {
                ModuleUpdateEntity updateEntity = new ModuleUpdateEntity(moduleInDB.getVersion(),
                        ((Info) module.getInfo()).getVersion(),
                        module.getInfo().getUuid());
                updateEntity.setComponent(module);
                modulesForUpdate.add(updateEntity);
            }
        }
        for (Component module : modules) {
            module.initialize();
        }
        UpdateService.beforeUpdateComponents(transaction, modulesForUpdate.toArray(ModuleUpdateEntity[]::new));
    }


    public void update(List<ModuleUpdateEntity> updates, Transaction transaction) throws Exception {
        log.warn("Updating versions: " + updates);
        if (updates == null || updates.size() == 0) {
            return;
        }
        UpdateService.beforeUpdateComponents(transaction, updates.toArray(ModuleUpdateEntity[]::new));
        UpdateService.updateComponents(transaction, updates.toArray(ModuleUpdateEntity[]::new));
    }

    private ModuleEditable getModuleByUuid(String uuid, Transaction transaction) throws DatabaseException {
        try (IteratorEntity<ModuleEditable> iter = transaction.find(ModuleEditable.class, new HashFilter(ModuleEditable.FIELD_UUID, uuid))) {
            if (iter.hasNext()) {
                return iter.next();
            } else {
                return null;
            }
        }
    }

    private UpgradeAction getUpgradeAction(Component module, ModuleEditable moduleEditable) throws DatabaseException {
        if (moduleEditable == null) {
            return UpgradeAction.INSTALL;
        }
        Version previousVersion = moduleEditable.getVersion();
        Version nextVersion = ((Info) module.getInfo()).getVersion();
        int cmpResult = Version.compare(nextVersion, previousVersion);
        if (cmpResult == 0) {
            return UpgradeAction.NONE;
        } else if (cmpResult > 0) {
            return UpgradeAction.UPDATE;
        } else {
            throw new DowngradingException(module.getInfo().getUuid(), nextVersion, previousVersion);
        }
    }

    private void removeRedundantModules(List<Component> modules, Transaction transaction) throws DatabaseException {
        Schema schema = Schema.read(transaction.getDbProvider());
        log.info("Checking for unused modules...");
        Set<String> moduleUuids = modules.stream().map(Component::getInfo).map(com.fuzzy.cluster.struct.Info::getUuid).collect(Collectors.toSet());
        log.warn("Debug: " + moduleUuids);
        try (IteratorEntity<ModuleEditable> mi = transaction.find(ModuleEditable.class, EmptyFilter.INSTANCE)) {
            while (mi.hasNext()) {
                ModuleEditable moduleEditable = mi.next();
                log.warn("Debug: " + moduleEditable.getUuid() + " Version: " + moduleEditable.getVersion());
                if (!moduleUuids.contains(moduleEditable.getUuid())
                    //TODO Ulitin V. Удалить эту доп. условие после 01.01.2021
//						&& !moduleEditable.getUuid().equals(Subsystems.UUID)
                ) {
                    removeModule(moduleEditable, schema, transaction);
                }
            }
        }
    }

    private void removeModule(ModuleEditable module, Schema schema, Transaction transaction) throws DatabaseException {
        log.warn("Removing module " + module.getUuid() + "...");
        transaction.remove(module);
        schema.dropTablesByNamespace(module.getUuid());
    }
}
