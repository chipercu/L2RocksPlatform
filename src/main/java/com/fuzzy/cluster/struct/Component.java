package com.fuzzy.cluster.struct;

import com.fuzzy.cluster.Cluster;
import com.fuzzy.cluster.component.manager.ManagerComponent;
import com.fuzzy.cluster.core.component.RuntimeComponentInfo;
import com.fuzzy.cluster.core.remote.Remotes;
import com.fuzzy.cluster.core.service.transport.LocalTransport;
import com.fuzzy.cluster.core.service.transport.TransportManager;
import com.fuzzy.cluster.core.service.transport.executor.ComponentExecutorTransportImpl;
import com.fuzzy.cluster.exception.ClusterException;
import com.fuzzy.cluster.struct.Info;
import com.fuzzy.cluster.struct.RegistrationState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by kris on 15.06.17.
 */
public abstract class Component {

    private final static Logger log = LoggerFactory.getLogger(Component.class);

    private final com.fuzzy.cluster.struct.Info info;
    private Cluster cluster;
    private TransportManager transportManager;
    private RegistrationState registrationState;
    private LocalTransport transport;
    private Remotes remote;

    public Component() {
        this.info = createInfoBuilder().build();
    }

    public void init(Cluster cluster, TransportManager transportManager) {
        this.cluster = cluster;
        this.transportManager = transportManager;
        this.transport = transportManager.createTransport(this);
        this.remote = new Remotes(cluster, this);

        try {
            transport.setExecutor(getExecutorTransportBuilder().build());
        } catch (ClusterException e) {
            log.error("Error set transport executor", e);
            try {
                transportManager.destroyTransport(transport);
            } catch (Exception ignore) {
            }
            throw e;
        }

        //Регистрируемся у менеджера подсистем
        registerComponent();
        log.info("Register {} ({})", getInfo().getUuid(), getId());
        onInitialized();
    }

    public void onInitialized() {
    }

    protected Cluster getCluster() {
        return cluster;
    }

    //Точка переопределения билдера info
    protected com.fuzzy.cluster.struct.Info.Builder createInfoBuilder() {
        return new com.fuzzy.cluster.struct.Info.Builder(this.getClass());
    }

    public Info getInfo() {
        return info;
    }

    protected ComponentExecutorTransportImpl.Builder getExecutorTransportBuilder() {
        return new ComponentExecutorTransportImpl.Builder(this, cluster.getUncaughtExceptionHandler());
    }

    //Регистрируемся у менджера подсистем
    protected void registerComponent() {
        ManagerComponent managerComponent = cluster.getAnyLocalComponent(ManagerComponent.class);
        this.registrationState = managerComponent.getRegisterComponent().registerActiveComponent(
                new RuntimeComponentInfo(
                        getInfo().getUuid(),
                        getTransport().getExecutor().getClassRControllers()
                )
        );
        transportManager.registerTransport(transport);
    }

    //Снимаем регистрацию у менджера подсистем
    protected void unregisterComponent() {
        ManagerComponent managerComponent = cluster.getAnyLocalComponent(ManagerComponent.class);
        managerComponent.getRegisterComponent().unRegisterActiveComponent(getId());
    }

    public LocalTransport getTransport() {
        return transport;
    }

    public int getId() {
        return registrationState.id;
    }

    public Remotes getRemotes() {
        return remote;
    }

    public void destroy() {
        log.info("{} destroy...", getInfo().getUuid());
        try {
            unregisterComponent();
            log.info("{} destroyed. completed", getInfo().getUuid());
        } catch (Exception e) {
            log.error("{} Error destroy subsystem", getInfo().getUuid(), e);
        }

        try {
            transportManager.destroyTransport(transport);
        } catch (Exception e) {
            log.error("{} Error transport destroy", getInfo().getUuid(), e);
        }
    }
}
