package com.fuzzy.subsystems.config;

import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.QueryTransaction;
import com.fuzzy.main.platform.querypool.ReadableResource;
import com.fuzzy.main.rdao.database.domainobject.filter.HashFilter;
import com.fuzzy.main.rdao.database.utils.TypeConvert;
import com.fuzzy.subsystems.domainobject.config.ConfigReadable;

import java.io.Serializable;

public class ConfigGetter<T extends ConfigReadable> {

    private final ReadableResource<T> configReadableResource;

    public ConfigGetter(ReadableResource<T> configReadableResource) {
        this.configReadableResource = configReadableResource;
    }

    public <U extends Serializable> U get(Config<U> config, QueryTransaction transaction) throws PlatformException {
        T configReadable = configReadableResource.find(new HashFilter(T.FIELD_NAME, config.getName()), transaction);
        if (configReadable == null) {
            return config.getDefaultValue();
        }

        byte[] binaryValue = configReadable.getValue();
        return TypeConvert.unpack(config.getType(), binaryValue, config.getConverter());
    }
}