package com.fuzzy.cluster.core.service.transport;

import com.fuzzy.cluster.Cluster;
import com.fuzzy.cluster.NetworkTransit;
import com.fuzzy.cluster.component.manager.ManagerComponent;
import com.fuzzy.cluster.core.remote.RemoteTarget;
import com.fuzzy.cluster.core.remote.packer.RemotePacker;
import com.fuzzy.cluster.core.remote.packer.RemotePackerObject;
import com.fuzzy.cluster.core.remote.struct.RController;
import com.fuzzy.cluster.core.service.transport.LocalTransport;
import com.fuzzy.cluster.core.service.transport.executor.ComponentExecutorTransport;
import com.fuzzy.cluster.event.UpdateNodeConnect;
import com.fuzzy.cluster.exception.ExceptionBuilder;
import com.fuzzy.cluster.struct.Component;
import com.fuzzy.cluster.utils.MethodKey;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by kris on 12.09.16.
 */
public class TransportManager {

    public final Cluster cluster;

    private final RemotePackerObject remotePackerObject;

    public final NetworkTransit networkTransit;

    private final Map<Integer, com.fuzzy.cluster.core.service.transport.LocalTransport> localComponentIdTransports;

    public final List<UpdateNodeConnect> updateNodeConnectListeners;
    private final ExceptionBuilder exceptionBuilder;

    public TransportManager(Cluster cluster, NetworkTransit.Builder builderNetworkTransit, List<RemotePacker> remotePackers, List<UpdateNodeConnect> updateNodeConnectListeners, ExceptionBuilder exceptionBuilder) {
        this.cluster = cluster;
        this.remotePackerObject = new RemotePackerObject(remotePackers);
        this.networkTransit = builderNetworkTransit.build(this);

        this.localComponentIdTransports = new ConcurrentHashMap<Integer, com.fuzzy.cluster.core.service.transport.LocalTransport>();

        this.updateNodeConnectListeners = Collections.unmodifiableList(updateNodeConnectListeners);

        this.exceptionBuilder = exceptionBuilder;
    }

    public RemotePackerObject getRemotePackerObject() {
        return remotePackerObject;
    }

    public com.fuzzy.cluster.core.service.transport.LocalTransport createTransport(Component component) {
        return new com.fuzzy.cluster.core.service.transport.LocalTransport(this, component);
    }

    public ExceptionBuilder getExceptionBuilder() {
        return exceptionBuilder;
    }

    public synchronized void registerTransport(com.fuzzy.cluster.core.service.transport.LocalTransport transport) {
        int componentId = transport.getComponent().getId();
        if (componentId < 0) {
            throw new RuntimeException("Internal error: Error in logic");
        }
        if (localComponentIdTransports.put(componentId, transport) != null) {
            throw new RuntimeException("Internal error: Error in logic");
        }
    }

    public synchronized void destroyTransport(com.fuzzy.cluster.core.service.transport.LocalTransport transport) {
        localComponentIdTransports.remove(transport.getComponent().getId());
    }

    public Object request(Component sourceComponent, RemoteTarget target, Class<? extends RController> rControllerClass, Method method, Object[] args) throws Throwable {
        if (target.nodeRuntimeId().equals(networkTransit.getNode().getRuntimeId())) {
            //локальный запрос
            ComponentExecutorTransport targetComponentExecutorTransport = getLocalExecutorTransport(target.componentId());
            return targetComponentExecutorTransport.execute(rControllerClass.getName(), method, args);
        } else {
            //сетевой запрос
            int methodKey = MethodKey.calcMethodKey(method);
            byte[][] sargs;
            if (args == null) {
                sargs = null;
            } else {
                sargs = new byte[args.length][];
                Class[] parameterTypes = method.getParameterTypes();
                for (int i = 0; i < parameterTypes.length; i++) {
                    sargs[i] = remotePackerObject.serialize(sourceComponent, parameterTypes[i], args[i]);
                }
            }
            ComponentExecutorTransport.Result result = networkTransit.getRemoteControllerRequest().request(sourceComponent, target.nodeRuntimeId(), target.componentId(), rControllerClass.getName(), methodKey, sargs);
            if (result.exception() != null) {
                throw (Throwable) remotePackerObject.deserialize(sourceComponent, Throwable.class, result.exception());
            } else {
                return remotePackerObject.deserialize(sourceComponent, method.getReturnType(), result.value());
            }
        }
    }

    public ComponentExecutorTransport.Result localRequest(int targetComponentId, String rControllerClassName, int methodKey, byte[][] byteArgs) {
        try {
            ComponentExecutorTransport targetComponentExecutorTransport = getLocalExecutorTransport(targetComponentId);
            return targetComponentExecutorTransport.execute(rControllerClassName, methodKey, byteArgs);
        } catch (Exception e) {
            ManagerComponent managerComponent = cluster.getAnyLocalComponent(ManagerComponent.class);
            byte[] exception;
            try {
                exception = cluster.getTransportManager().remotePackerObject.serialize(managerComponent, Throwable.class, e);
            } catch (Throwable e1) {
                cluster.getUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e1);
                return null;
            }
            return new ComponentExecutorTransport.Result(null, exception);
        }
    }

    private ComponentExecutorTransport getLocalExecutorTransport(int targetComponentId) throws Exception {
        LocalTransport targetTransport = localComponentIdTransports.get(targetComponentId);
        if (targetTransport == null) {
            throw cluster.getExceptionBuilder().buildRemoteComponentNotFoundException(
                    networkTransit.getNode().getRuntimeId(), targetComponentId
            );
        } else {
            return targetTransport.getExecutor();
        }
    }

    public void destroy() {
        networkTransit.close();
    }
}