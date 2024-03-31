package com.fuzzy.cluster.core.remote.packer;

import com.fuzzy.cluster.core.remote.packer.RemotePacker;
import com.fuzzy.cluster.exception.ClusterRemotePackerException;
import com.fuzzy.cluster.struct.Component;
import com.fuzzy.cluster.utils.ReflectionUtils;

import java.lang.reflect.Type;
import java.util.List;

public class RemotePackerObject {
    private final List<com.fuzzy.cluster.core.remote.packer.RemotePacker> remotePackers;

    public RemotePackerObject(List<com.fuzzy.cluster.core.remote.packer.RemotePacker> remotePackers) {
        this.remotePackers = remotePackers;
    }

    public byte[] serialize(Component component, Class classType, Object value) {
        if (value == null) {
            return new byte[0];
        }
        for (com.fuzzy.cluster.core.remote.packer.RemotePacker remotePacker : remotePackers) {
            if (remotePacker.isSupport(classType)) {
                byte[] result = remotePacker.serialize(component, value);
                assertResultSerialize(result, value);
                return result;
            }
        }
        throw new ClusterRemotePackerException();
    }

    public Object deserialize(Component component, Class classType, byte[] value) {
        if (value.length == 0) {
            return null;
        }
        for (com.fuzzy.cluster.core.remote.packer.RemotePacker remotePackerObject : remotePackers) {
            if (remotePackerObject.isSupport(classType))
                return remotePackerObject.deserialize(component, classType, value);
        }
        throw new ClusterRemotePackerException();
    }

    public String getClassName(Class classType) {
        for (com.fuzzy.cluster.core.remote.packer.RemotePacker remotePackerObject : remotePackers) {
            if (remotePackerObject.isSupport(classType)) return remotePackerObject.getClassName(classType);
        }
        throw new ClusterRemotePackerException();
    }

    public boolean isSupportAndValidationType(Type classType) {
        for (RemotePacker remotePackerObject : remotePackers) {
            if (remotePackerObject.isSupport(ReflectionUtils.getRawClass(classType))) {
                //Валидируем класс
                remotePackerObject.validation(classType);
                return true;
            }
        }
        return false;
    }

    private void assertResultSerialize(byte[] result, Object value) {
//        if (result.length == 0) {
//            throw new IllegalArgumentException("Критическая ошибка! Value: " + value);
//        }
    }
}
