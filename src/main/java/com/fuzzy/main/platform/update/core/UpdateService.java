package com.fuzzy.main.platform.update.core;

import com.fuzzy.main.platform.sdk.component.Component;
import com.fuzzy.main.platform.sdk.component.Info;
import com.fuzzy.main.platform.sdk.component.version.Version;
import com.fuzzy.main.platform.sdk.domainobject.module.ModuleEditable;
import com.fuzzy.main.platform.update.UpdateTask;
import com.fuzzy.main.platform.update.util.UpdateUtil;
import com.fuzzy.main.rdao.database.anotation.Entity;
import com.fuzzy.main.rdao.database.domainobject.Transaction;
import com.fuzzy.main.rdao.database.domainobject.filter.HashFilter;
import com.fuzzy.main.rdao.database.domainobject.iterator.IteratorEntity;
import com.fuzzy.main.rdao.database.exception.DatabaseException;
import com.fuzzy.main.rdao.database.schema.Schema;
import com.fuzzy.main.rdao.database.schema.StructEntity;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class UpdateService {

    private final static Logger log = LoggerFactory.getLogger(UpdateService.class);

    public static void updateComponents(Transaction transaction,
                                        ModuleUpdateEntity... updates) throws DatabaseException {
        Schema.resolve(ModuleEditable.class); //todo V.Bukharkin вынести отсюда
        List<UpdateUtil.ModuleTaskUpdate> moduleTaskUpdates = UpdateUtil.getUpdatesInCorrectOrder(updates);
        for (UpdateUtil.ModuleTaskUpdate moduleTaskUpdate : moduleTaskUpdates) {
            updateComponent(moduleTaskUpdate, transaction);
        }
    }

    public static void beforeUpdateComponents(Transaction transaction,
                                              ModuleUpdateEntity... updates) throws DatabaseException {
        Schema.resolve(ModuleEditable.class); //todo V.Bukharkin вынести отсюда
        List<UpdateUtil.ModuleTaskUpdate> moduleTaskUpdates = UpdateUtil.getUpdatesInCorrectOrder(updates);
        for (UpdateUtil.ModuleTaskUpdate moduleTaskUpdate : moduleTaskUpdates) {
            beforeUpdateComponent(moduleTaskUpdate, transaction);
        }
    }

    private static void beforeUpdateComponent(UpdateUtil.ModuleTaskUpdate moduleTaskUpdate,
                                              Transaction transaction) throws DatabaseException {
        Info componentInfo = (Info) moduleTaskUpdate.getComponent().getInfo();

        try (IteratorEntity<ModuleEditable> iter = transaction.find(ModuleEditable.class, new HashFilter(ModuleEditable.FIELD_UUID, componentInfo.getUuid()))) {
            if (iter.hasNext()) {
                ModuleEditable moduleEditable = iter.next();
                log.info("Before updating subsystem: " + componentInfo.getUuid() + ". From version " + moduleEditable.getVersion() + " to version " + componentInfo.getVersion());
                UpdateTask<? extends Component> updateTask = moduleTaskUpdate.getUpdateTask();
                if (updateTask != null) {
                    updateTask.executeBeforeUpdate(moduleEditable, transaction);
                }
            }
        }
    }

    private static void updateComponent(UpdateUtil.ModuleTaskUpdate moduleTaskUpdate,
                                        Transaction transaction) throws DatabaseException {
        Info componentInfo = (Info) moduleTaskUpdate.getComponent().getInfo();

        try (IteratorEntity<ModuleEditable> iter = transaction.find(ModuleEditable.class, new HashFilter(ModuleEditable.FIELD_UUID, componentInfo.getUuid()))) {
            if (iter.hasNext()) {
                ModuleEditable moduleEditable = iter.next();
                log.info("Updating subsystem: " + componentInfo.getUuid() + ". From version " + moduleEditable.getVersion() + " to version " + componentInfo.getVersion());

                UpdateTask<? extends Component> updateTask = moduleTaskUpdate.getUpdateTask();
                if (updateTask != null) {
                    updateTask.execute(transaction);
                }

                //Сохраняем
                Version codeVersion = componentInfo.getVersion();
                moduleEditable.setVersion(codeVersion);
                transaction.save(moduleEditable);
            }
        }
        Set<StructEntity> domains = new HashSet<>();
        for (Class domainObjectClass : new Reflections(componentInfo.getUuid()).getTypesAnnotatedWith(Entity.class, true)) {
            domains.add(Schema.getEntity(domainObjectClass));
        }
        Schema.read(transaction.getDbProvider()).checkSubsystemIntegrity(domains, componentInfo.getUuid());
    }
}
