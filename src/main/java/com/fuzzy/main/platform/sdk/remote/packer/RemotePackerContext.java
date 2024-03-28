package com.fuzzy.main.platform.sdk.remote.packer;

import com.fuzzy.main.cluster.core.remote.packer.RemotePacker;
import com.fuzzy.main.cluster.struct.Component;
import com.fuzzy.main.platform.sdk.context.Context;

import java.lang.reflect.Type;

/**
 * Created by user on 06.09.2017.
 * TODO Ulitin V. Когда будем разъезжаться по серверам реализовать
 */
public class RemotePackerContext implements RemotePacker<Context> {

    @Override
    public boolean isSupport(Class classType) {
        return Context.class.isAssignableFrom(classType) && classType.isInterface();
    }

    @Override
    public String getClassName(Class classType) {
        return Context.class.getName();
    }

    @Override
    public void validation(Type classType) {
    }

    @Override
    public byte[] serialize(Component component, Context value) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Context deserialize(Component component, Class classType, byte[] value) {
        throw new RuntimeException("Not implemented");
    }
}
