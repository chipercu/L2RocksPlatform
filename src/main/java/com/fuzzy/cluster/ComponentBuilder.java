package com.fuzzy.cluster;

import com.fuzzy.cluster.exception.ClusterException;
import com.fuzzy.cluster.struct.Component;

/**
 * Created by kris on 19.06.17.
 */
public class ComponentBuilder {

    private final Class<? extends Component> componentClass;

    public ComponentBuilder(Class<? extends Component> componentClass) {
        this.componentClass = componentClass;
    }

    public Class<? extends Component> getComponentClass() {
        return componentClass;
    }

    protected Component build() throws ClusterException {
        try {
            return componentClass.getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException ex) {
            throw new ClusterException(ex);
        }
    }
}
