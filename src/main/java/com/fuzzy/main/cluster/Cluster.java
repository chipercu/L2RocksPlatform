package com.fuzzy.main.cluster;

import com.fuzzy.main.cluster.component.manager.ManagerComponent;
import com.fuzzy.main.cluster.core.remote.packer.RemotePacker;
import com.fuzzy.main.cluster.core.remote.packer.impl.*;
import com.fuzzy.main.cluster.core.service.componentuuid.ComponentUuidManager;
import com.fuzzy.main.cluster.core.service.transport.TransportManager;
import com.fuzzy.main.cluster.core.service.transport.network.local.LocalNetworkTransit;
import com.fuzzy.main.cluster.exception.*;
import com.fuzzy.main.cluster.struct.Component;
import com.fuzzy.main.cluster.utils.RandomUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * Класс потоконебезопасен
 */
public class Cluster implements AutoCloseable {

    private final static Logger log = LoggerFactory.getLogger(Cluster.class);

    public final Node node;

    private final TransportManager transportManager;
    private final ComponentUuidManager componentUuidManager;

    private final Map<Class<? extends Component>, List<Component>> components;
    private final List<Component> dependencyOrderedComponents;

    private final Object context;

    private final ExceptionBuilder exceptionBuilder;
    private final Thread.UncaughtExceptionHandler uncaughtExceptionHandler;

    private Cluster(Builder builder) {
        this.components = new HashMap<>();

        this.transportManager = new TransportManager(this, builder.builderNetworkTransit, builder.remotePackers, builder.updateNodeConnectListeners, builder.exceptionBuilder);

        this.node = transportManager.networkTransit.getNode();

        this.componentUuidManager = new ComponentUuidManager();

        this.dependencyOrderedComponents = new ArrayList<>();

        this.context = builder.context;

        this.exceptionBuilder = builder.exceptionBuilder;
        this.uncaughtExceptionHandler = builder.uncaughtExceptionHandler;

        log.info("Cluster created.");
    }

    public TransportManager getTransportManager() {
        return transportManager;
    }

    public List<Node> getRemoteNodes() {
        return transportManager.networkTransit.getRemoteNodes();
    }

    public ExceptionBuilder getExceptionBuilder() {
        return exceptionBuilder;
    }

    private void appendComponent(Component component) throws ClusterException {
        component.init(this, transportManager);

        List<Component> componentInstances = components.get(component.getClass());
        if (componentInstances == null) {
            componentInstances = new ArrayList<>();
            components.put(component.getClass(), componentInstances);
        }
        componentInstances.add(component);
        dependencyOrderedComponents.add(component);
    }

    public <T extends Component> T getAnyLocalComponent(Class<T> classComponent) {
        List<Component> components = this.components.get(classComponent);
        if (components == null) {
            return null;
        }
        return (T) components.get(RandomUtil.random.nextInt(components.size()));
    }

    //Не предпологаются частые вызовы - если будем дергать часто - необходимо переписать на итератор
    public Collection<Component> getLocalComponents() {
        List<Component> result = new ArrayList<>();
        for (Map.Entry<Class<? extends Component>, List<Component>> entry : this.components.entrySet()) {
            result.addAll(entry.getValue());
        }
        return result;
    }

    public String getUuid(Class<? extends Component> classComponent) {
        return componentUuidManager.getUuid(classComponent);
    }

    public <T> T getContext() {
        return (T) context;
    }

    public Thread.UncaughtExceptionHandler getUncaughtExceptionHandler() {
        return uncaughtExceptionHandler;
    }

    public Component getAnyLocalComponent(String uuidComponent) {
        for (Map.Entry<Class<? extends Component>, List<Component>> entry : this.components.entrySet()) {
            List<Component> components = entry.getValue();
            if (components.isEmpty()) continue;
            Component component = components.get(0);
            if (component.getInfo().getUuid().equals(uuidComponent)) return component;
        }
        return null;
    }

    public <T extends Component> List<T> getDependencyOrderedComponentsOf(Class<T> baseClass) {
        return dependencyOrderedComponents.stream()
                .filter(component -> baseClass.isAssignableFrom(component.getClass()))
                .map(component -> (T) component)
                .collect(Collectors.toList());
    }

    public void removeComponent(Component component) throws ClusterException {
        for (int i = dependencyOrderedComponents.size() - 1; i > -1; --i) {
            Component another = dependencyOrderedComponents.get(i);
            if (another == component) {
                dependencyOrderedComponents.remove(i);
                break;
            }

            if (Arrays.asList(another.getInfo().getDependencies()).contains(component.getClass())) {
                throw new ClusterDependencyException(another, component);
            }
        }

        closeComponent(component);
    }

    @Override
    public void close() {
        for (int i = dependencyOrderedComponents.size() - 1; i > -1; --i) {
            closeComponent(dependencyOrderedComponents.remove(i));
        }

        transportManager.destroy();
    }

    private void closeComponent(Component component) {
        List<Component> list = components.get(component.getClass());
        if (list != null) {
            list.remove(component);
            if (list.isEmpty()) {
                components.remove(component.getClass());
            }
        }

        component.destroy();
    }

    public static class Builder {

        private final List<RemotePacker> remotePackers;

        private NetworkTransit.Builder builderNetworkTransit;

        private final List<Component> components;

        private Object context;

        private final List<UpdateNodeConnect> updateNodeConnectListeners;

        private ExceptionBuilder exceptionBuilder;

        private final Thread.UncaughtExceptionHandler uncaughtExceptionHandler;

        public Builder(Thread.UncaughtExceptionHandler uncaughtExceptionHandler) {
            this.uncaughtExceptionHandler = uncaughtExceptionHandler;

            this.remotePackers = new ArrayList<>();
            this.remotePackers.add(new RemotePackerRemoteObject());
            this.remotePackers.add(new RemotePackerFuture());
            this.remotePackers.add(new RemotePackerOptional());
            this.remotePackers.add(new RemotePackerClusterInputStream());
            this.remotePackers.add(new RemotePackerSerializable());
            this.builderNetworkTransit = new LocalNetworkTransit.Builder();

            this.components = new ArrayList<>();
            this.components.add(new ManagerComponent());

            this.updateNodeConnectListeners = new CopyOnWriteArrayList<>();

            this.exceptionBuilder = new ExceptionBuilderImpl();
        }

        public Builder withNetworkTransport(NetworkTransit.Builder builderNetworkTransit) {
            this.builderNetworkTransit = builderNetworkTransit;
            return this;
        }

        public Builder withRemotePackerObject(RemotePacker remotePackerObject) {
            remotePackers.add(remotePackerObject);
            return this;
        }

        public Builder withComponent(Component component) {
            if (containsComponent(component)) {
                throw new RuntimeException(component.getClass() + " already exists.");
            }
            components.add(component);
            return this;
        }

        public Builder withComponent(ComponentBuilder componentBuilder) {
            if (containsComponent(componentBuilder)) {
                throw new RuntimeException(componentBuilder.getComponentClass() + " already exists.");
            }
            components.add(componentBuilder.build());
            return this;
        }

        public Builder withComponentIfNotExist(ComponentBuilder componentBuilder) {
            if (!containsComponent(componentBuilder)) {
                withComponent(componentBuilder);
            }
            return this;
        }

        public Builder withContext(Object context) {
            this.context = context;
            return this;
        }

        public Builder withListenerUpdateConnect(UpdateNodeConnect updateConnect) {
            updateNodeConnectListeners.add(updateConnect);
            return this;
        }

        public Builder withExceptionBuilder(ExceptionBuilder exceptionBuilder) {
            this.exceptionBuilder = exceptionBuilder;
            return this;
        }

        public Cluster build() throws ClusterException {
            Cluster cluster = null;
            try {
                cluster = new Cluster(this);

//                List<Component> clusterComponents = new ArrayList<>(components.size() + 1);
//                clusterComponents.add(new ManagerComponent());
//                clusterComponents.addAll(components);
//                appendNotExistenceDependencies(cluster, components);

                appendNotExistenceDependencies(components);
                while (!components.isEmpty()) {
                    Component nextComponent = null;
                    int componentIndex = 0;
                    for (; componentIndex < components.size(); ++componentIndex) {
                        //Проверяем все ли зависимости загружены
                        Cluster finalCluster = cluster;
                        Component component = components.get(componentIndex);
                        boolean isSuccessDependencies = Arrays.stream(component.getInfo().getDependencies())
                                .noneMatch(aClass -> finalCluster.getAnyLocalComponent(aClass) == null);
                        if (isSuccessDependencies) {
                            nextComponent = component;
                            break;
                        }
                    }

                    if (nextComponent == null) {
                        throw new ClusterDependencyCycleException(components.stream().map(cb -> cb.getClass().getName()).collect(Collectors.toList()));
                    }

                    cluster.appendComponent(nextComponent);

                    components.remove(componentIndex);
                }

                cluster.transportManager.networkTransit.start();
            } catch (ClusterException ex) {
                if (cluster != null) {
                    cluster.close();
                }
                throw ex;
            }

            return cluster;
        }

        private static void appendNotExistenceDependencies(List<Component> source) throws ClusterException {
            Set<Class> componentClasses = source.stream().map(Component::getClass).collect(Collectors.toSet());
            for (int i = 0; i < source.size(); ++i) {
                for (Class dependence : source.get(i).getInfo().getDependencies()) {
                    if (!componentClasses.contains(dependence)) {
                        source.add(new ComponentBuilder(dependence).build());
                        componentClasses.add(dependence);
                    }
                }
            }
        }

        private boolean containsComponent(ComponentBuilder builder) {
            return components.stream().anyMatch(cb -> cb.getClass() == builder.getComponentClass());
        }

        private boolean containsComponent(Component component) {
            return components.stream().anyMatch(cb -> cb.getClass() == component.getClass());
        }
    }
}
