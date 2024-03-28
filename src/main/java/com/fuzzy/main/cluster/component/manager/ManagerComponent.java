package com.fuzzy.main.cluster.component.manager;

import com.fuzzy.main.cluster.Cluster;
import com.fuzzy.main.cluster.anotation.Info;
import com.fuzzy.main.cluster.component.manager.core.ManagerRegisterComponents;
import com.fuzzy.main.cluster.core.service.transport.TransportManager;
import com.fuzzy.main.cluster.struct.Component;

/**
 * Created by kris on 23.09.16.
 */
@Info(uuid = ManagerComponent.UUID)
public class ManagerComponent extends Component {

    public static final String UUID = "com.fuzzy.main.cluster.component.manager";

    private static final int COMPONENT_UNIQUE_ID_MANAGER = 0;

    private ManagerRegisterComponents registerComponent;

    @Override
    public void init(Cluster cluster, TransportManager transportManager) {
        super.init(cluster, transportManager);
        registerComponent = new ManagerRegisterComponents(this);

        //Регистрируем себя
        transportManager.registerTransport(getTransport());
    }

    @Override
    public int getId() {
        return COMPONENT_UNIQUE_ID_MANAGER;
    }

    //Переопределяем - логика регистрации у менеджера подсистем не стандартная
    @Override
    protected void registerComponent() {

    }

    //Переопределяем - логика снятия регистрации у менеджера подсистем не стандартная
    @Override
    protected void unregisterComponent() {
    }

    public ManagerRegisterComponents getRegisterComponent() {
        return registerComponent;
    }
}
