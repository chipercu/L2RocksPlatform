package com.fuzzy.main.platform.sdk.remote;

import com.fuzzy.main.cluster.core.remote.utils.RemoteControllerAnalysis;
import com.fuzzy.main.platform.querypool.QueryRemoteController;
import com.fuzzy.main.platform.querypool.ResourceProvider;
import com.fuzzy.main.platform.sdk.component.Component;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class QueryRemotes {

    private final static Logger log = LoggerFactory.getLogger(QueryRemotes.class);

    private final Component component;

    private final Map<Class<? extends QueryRemoteController>, Constructor> queryRemoteControllers;

    public QueryRemotes(Component component) {
        this.component = component;

        //Собираем remoteController'ы
        try {
            this.queryRemoteControllers = new HashMap<>();
            for (Class<? extends QueryRemoteController> classRemoteController : new Reflections(component.getInfo().getUuid()).getSubTypesOf(QueryRemoteController.class)) {
                if (classRemoteController.isInterface()) {
                    //Валидируем интерфейс
                    new RemoteControllerAnalysis(component, classRemoteController);
                } else {
                    //Собираем реализации
                    for (Class interfaceClazz : classRemoteController.getInterfaces()) {
                        if (!QueryRemoteController.class.isAssignableFrom(interfaceClazz)) continue;

                        Constructor constructor = classRemoteController.getConstructor(component.getClass(), ResourceProvider.class);
                        constructor.setAccessible(true);

                        if (queryRemoteControllers.putIfAbsent(interfaceClazz, constructor) != null) {
                            throw new RuntimeException("Конфликт реализации, интерфейс " + interfaceClazz + " имеет дублирующие реализации");
                        }
                    }
                }
            }
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }


    //TODO Ulitin V. переписать на удаленые вызовы
    public <T extends QueryRemoteController> Set<T> getControllers(ResourceProvider resourceProvider, Class<T> remoteControllerClass) {
        if (!remoteControllerClass.isInterface()) {
            throw new IllegalArgumentException("Class " + remoteControllerClass + " is not interface");
        }

        try {
            Set<T> controllers = new HashSet<>();
            for (com.fuzzy.main.cluster.struct.Component item : component.getRemotes().cluster.getLocalComponents()) {
                if (!(item instanceof Component)) {
                    continue;
                }
                Component iComponent = (Component) item;

                Constructor constructor = iComponent.getQueryRemotes().queryRemoteControllers.get(remoteControllerClass);
                if (constructor == null) continue;
                constructor.setAccessible(true);

                T iQueryRemoteController = (T) constructor.newInstance(iComponent, resourceProvider);
                controllers.add(iQueryRemoteController);
            }
            return controllers;
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    //TODO Ulitin V. переписать на удаленые вызовы
    public <T extends QueryRemoteController> T getController(ResourceProvider resourceProvider, Class<? extends Component> componentClass, Class<T> remoteControllerClass) {
        if (!remoteControllerClass.isInterface()) {
            throw new IllegalArgumentException("Class " + remoteControllerClass + " is not interface");
        }
        Component remoteComponent = component.getRemotes().cluster.getAnyLocalComponent(componentClass);
        Constructor constructor = remoteComponent.getQueryRemotes().queryRemoteControllers.get(remoteControllerClass);
        if (constructor == null) {
            throw new RuntimeException("Implements: " + remoteControllerClass + " in component: " + componentClass + " not found");
        }
        constructor.setAccessible(true);

        try {
            return (T) constructor.newInstance(remoteComponent, resourceProvider);
        } catch (ReflectiveOperationException e) {
            log.error("Exception getController, remoteComponent: " + remoteComponent + ", constructor: " + constructor, e);
            log.error("Cause exception (Exception getController)", e.getCause());//Необходимо для InvocationTargetException
            throw new RuntimeException(e);
        }
    }

    //TODO Ulitin V. переписать на удаленые вызовы
    public <T extends QueryRemoteController> T getController(ResourceProvider resourceProvider, String componentUuid, Class<T> remoteControllerClass) {
        if (!remoteControllerClass.isInterface()) {
            throw new IllegalArgumentException("Class " + remoteControllerClass + " is not interface");
        }
        Component remoteComponent = (Component) component.getRemotes().cluster.getAnyLocalComponent(componentUuid);

        Constructor constructor = remoteComponent.getQueryRemotes().queryRemoteControllers.get(remoteControllerClass);
        if (constructor == null) {
            throw new RuntimeException("Implements: " + remoteControllerClass + " in component: " + remoteComponent.getClass() + " not found");
        }
        constructor.setAccessible(true);

        try {
            return (T) constructor.newInstance(remoteComponent, resourceProvider);
        } catch (ReflectiveOperationException e) {
            log.error("Exception getController, componentUuid: " + componentUuid + ", constructor: " + constructor, e);
            log.error("Cause exception (Exception getController)", e.getCause());//Необходимо для InvocationTargetException
            throw new RuntimeException(e);
        }
    }

    //TODO Ulitin V. переписать на удаленые вызовы
    public <T extends QueryRemoteController> boolean isController(String componentUuid, Class<T> remoteControllerClass) {
        if (!remoteControllerClass.isInterface()) {
            throw new IllegalArgumentException("Class " + remoteControllerClass + " is not interface");
        }

        Component remoteComponent = (Component) component.getRemotes().cluster.getAnyLocalComponent(componentUuid);

        return remoteComponent.getQueryRemotes().queryRemoteControllers.containsKey(remoteControllerClass);
    }
}
