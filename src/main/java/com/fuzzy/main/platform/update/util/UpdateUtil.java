package com.fuzzy.main.platform.update.util;

import com.fuzzy.main.platform.sdk.component.Component;
import com.fuzzy.main.platform.sdk.component.version.Version;
import com.fuzzy.main.platform.update.UpdateTask;
import com.fuzzy.main.platform.update.annotation.Dependency;
import com.fuzzy.main.platform.update.annotation.Update;
import com.fuzzy.main.platform.update.core.ModuleUpdateEntity;
import com.fuzzy.main.platform.update.exception.UpdateException;
import org.reflections.Reflections;

import java.util.*;
import java.util.stream.Collectors;

public class UpdateUtil {

    private final static Version START_VERSION = new Version(1, 0, 0, 0);

    @SuppressWarnings("unchecked")
    public static <T extends UpdateTask<? extends Component>> Update getUpdateAnnotation(Class<T> clazz) {
        while (!clazz.isAnnotationPresent(Update.class)) {
            if (!UpdateTask.class.isAssignableFrom(clazz.getSuperclass())) {
                throw new UpdateException("Not found " + Update.class + " annotation in " + clazz + ".");
            }
            clazz = (Class<T>) clazz.getSuperclass();
        }
        return clazz.getAnnotation(Update.class);
    }

    public static <T extends Component> void checkIntegrity(Set<Class<? extends UpdateTask<T>>> tasks) {
        if (tasks.isEmpty()) {
            return;
        }
        Set<Version> previousVersions = new HashSet<>();
        Set<Version> nextVersions = new HashSet<>();
        String componentUuid = null;
        for (Class<? extends UpdateTask<T>> task : tasks) {
            final Update annotationEntity = UpdateUtil.getUpdateAnnotation(task);
            if (componentUuid == null) {
                componentUuid = annotationEntity.componentUUID();
            }
            if (!componentUuid.equals(annotationEntity.componentUUID())) {
                throw new UpdateException("Subsystem uuid of task set isn't same");
            }
            Version prevVersion = Version.parseTaskUpdate(annotationEntity.previousVersion());
            Version nextVersion = Version.parseTaskUpdate(annotationEntity.version());
            if (Version.compare(prevVersion, nextVersion) != -1) {
                throw new UpdateException("Integrity error. Update version: " + nextVersion + " is less or equal to previous: " + prevVersion);
            }
            if (!previousVersions.add(prevVersion)) {
                throw new UpdateException("Integrity error. Previous update version is already exist: " + prevVersion);
            }
            if (!nextVersions.add(nextVersion)) {
                throw new UpdateException("Integrity error. Current update version is already exist: " + nextVersion);
            }
        }
        for (Version previousVersion : previousVersions) {
            if (!nextVersions.remove(previousVersion) && !previousVersion.equals(START_VERSION)) {
                throw new UpdateException("Integrity error. Can't find previous update version: " + previousVersion);
            }
        }
        if (nextVersions.size() > 1) {
            throw new UpdateException("Integrity error. Can't find next update versions: " + nextVersions);
        }
    }


    //todo V.Bukharkin нужно покрыть тестами
    public static List<ModuleTaskUpdate> getUpdatesInCorrectOrder(ModuleUpdateEntity[] updates) {
        checkUniqueModule(updates);
        List<ModuleTaskUpdate> result = new ArrayList<>(updates.length);
        Map<String, ModuleUpdateEntity> subsystemUuids = Arrays.stream(updates)
                .collect(Collectors.toMap(ModuleUpdateEntity::getComponentUuid, mu -> mu));
        Set<String> passedMds = new HashSet<>();
        for (ModuleUpdateEntity update : updates) {
            if (!passedMds.contains(update.getComponentUuid())) {
                buildModuleDependency(update, subsystemUuids, passedMds, result);
            }
        }
        return result;
    }

    public static <T extends Component> ModuleTaskUpdate getUpdateTaskObj(Version oldVersion, Version newVersion, T component) {
        Class<UpdateTask<T>> updateTaskClass = getUpdateTaskClass(oldVersion, newVersion, component);
        return getUpdateTaskObj(updateTaskClass, component);
    }

    private static void buildModuleDependency(ModuleUpdateEntity update,
                                              Map<String, ModuleUpdateEntity> subsystemUpdates,
                                              Set<String> passedMds,
                                              List<ModuleTaskUpdate> result) {
        if (passedMds.contains(update.getComponentUuid())) {
            return;
        }
        Class<UpdateTask<Component>> updateTaskClass = getUpdateTaskClass(
                update.getOldVersion(), update.getNewVersion(), update.getComponent());

        ModuleTaskUpdate moduleTaskUpdate;
        if (updateTaskClass == null) {
            moduleTaskUpdate = new ModuleTaskUpdate(update.getComponent(), null);
        } else {
            final Update annotationEntity = UpdateUtil.getUpdateAnnotation(updateTaskClass);
            moduleTaskUpdate = getUpdateTaskObj(updateTaskClass, update.getComponent());
            if (annotationEntity.dependencies().length != 0) {
                Set<String> notCyclicDependencies = new HashSet<>();
                notCyclicDependencies.add(update.getComponentUuid());
                for (Dependency dependency : annotationEntity.dependencies()) {
                    if (!notCyclicDependencies.add(dependency.uuid())) {
                        throw new UpdateException("Cyclic dependency error. Dependency: " + dependency.uuid());
                    }
                    if (!subsystemUpdates.containsKey(dependency.uuid())) {
                        if (dependency.optional()) {
                            continue;
                        } else {
                            throw new UpdateException("Not found required dependency. Dependency: "
                                    + dependency.uuid());
                        }
                    }
                    buildModuleDependency(subsystemUpdates.get(dependency.uuid()), subsystemUpdates, passedMds, result);
                }
            }
        }
        result.add(moduleTaskUpdate);
        passedMds.add(update.getComponentUuid());
    }

    private static void checkUniqueModule(ModuleUpdateEntity[] updates) {
        for (int i = 0; i < updates.length; i++) {
            for (int j = i + 1; j < updates.length; j++) {
                if (updates[i].getComponentUuid().equals(updates[j].getComponentUuid())) {
                    throw new UpdateException("Updates have duplicate module: " + updates[i].getComponentUuid());
                }
            }
        }
    }

    private static <T extends Component> ModuleTaskUpdate getUpdateTaskObj(Class<UpdateTask<T>> updateTaskClass,
                                                                           Component component) {
        UpdateTask<T> updateTask;
        try {
            updateTask = updateTaskClass.getConstructor(component.getClass()).newInstance(component);
        } catch (ReflectiveOperationException e) {
            throw new UpdateException(e);
        }
        return new ModuleTaskUpdate(component, updateTask);
    }

    @SuppressWarnings("unchecked")
    private static <T extends Component> Class<UpdateTask<T>> getUpdateTaskClass(Version oldVersion,
                                                                                 Version newVersion,
                                                                                 T component) {
        if (equalsUpdateVersion(oldVersion, newVersion)) {
            return null;
        }

        for (Class updateTask : new Reflections(component.getInfo().getUuid()).getTypesAnnotatedWith(Update.class, true)) {
            final Update annotationEntity = UpdateUtil.getUpdateAnnotation(updateTask);
            if (equalsUpdateVersion(Version.parseTaskUpdate(annotationEntity.previousVersion()), oldVersion)
                    && equalsUpdateVersion(Version.parseTaskUpdate(annotationEntity.version()), newVersion)) {
                return updateTask;
            }
        }
        throw new UpdateException("Can't find update task " + oldVersion + "->" + newVersion + " for " + component);
    }

    private static boolean equalsUpdateVersion(Version version1, Version version2) {
        return version1.product == version2.product
                && version1.major == version2.major
                && version1.minor == version2.minor;
    }

    public static class ModuleTaskUpdate {

        private final Component component;
        private final UpdateTask<? extends Component> updateTask;

        private ModuleTaskUpdate(Component component, UpdateTask<? extends Component> updateTask) {
            this.component = component;
            this.updateTask = updateTask;
        }

        public Component getComponent() {
            return component;
        }

        public UpdateTask<? extends Component> getUpdateTask() {
            return updateTask;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ModuleTaskUpdate moduleTaskUpdate = (ModuleTaskUpdate) o;
            return Objects.equals(component.getInfo().getUuid(), moduleTaskUpdate.component.getInfo().getUuid());
        }

        @Override
        public int hashCode() {
            return Objects.hash(component.getInfo().getUuid());
        }
    }
}
