package com.fuzzy.cluster.core.remote.utils;

import com.fuzzy.cluster.anotation.DisableValidationRemoteMethod;
import com.fuzzy.cluster.core.remote.struct.RController;
import com.fuzzy.cluster.core.remote.utils.RemoteControllerUtils;
import com.fuzzy.cluster.struct.Component;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class RemoteControllerAnalysis {

    private final List<Method> methods;

    public RemoteControllerAnalysis(Component component, Class<? extends RController> interfaceClazz) {
        if (!interfaceClazz.isInterface()) {
            throw new IllegalArgumentException("Class: " + interfaceClazz + " is not interface.");
        }

        this.methods = new ArrayList<>();
        for (Method method : interfaceClazz.getMethods()) {
            //Валидируем метод
            if (!method.isAnnotationPresent(DisableValidationRemoteMethod.class)) {
                RemoteControllerUtils.validationRemoteMethod(component, interfaceClazz, method);
            }

            //Игнорируем права доступа
            method.setAccessible(true);

            methods.add(method);
        }
    }

    public List<Method> getMethods() {
        return methods;
    }
}
