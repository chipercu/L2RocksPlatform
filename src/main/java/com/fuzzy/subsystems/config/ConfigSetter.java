package com.fuzzy.subsystems.config;

import com.fuzzy.database.domainobject.filter.HashFilter;
import com.fuzzy.database.utils.TypeConvert;
import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.QueryTransaction;
import com.fuzzy.platform.querypool.RemovableResource;
import com.fuzzy.platform.querypool.ResourceProvider;
import com.fuzzy.platform.sdk.context.ContextTransaction;
import com.fuzzy.platform.sdk.function.Consumer;
import com.fuzzy.subsystem.core.remote.configsetternotification.RCConfigSetterNotification;
import com.fuzzy.subsystems.domainobject.config.ConfigEditable;
import com.fuzzy.subsystems.domainobject.config.ConfigReadable;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Set;

public class ConfigSetter<T extends ConfigReadable & ConfigEditable> {

    protected final RemovableResource<T> configEditableResource;
    private final Set<RCConfigSetterNotification> rcConfigSetterNotifications;

    public ConfigSetter(Class<T> configClass, ResourceProvider resources) {
        configEditableResource = resources.getRemovableResource(configClass);
        rcConfigSetterNotifications = resources.getQueryRemoteControllers(RCConfigSetterNotification.class);
    }

    public <U extends Serializable> void set(Config<U> config, U value, ContextTransaction<?> context) throws PlatformException {
        QueryTransaction transaction = context.getTransaction();
        byte[] binaryValue = TypeConvert.pack(config.getType(), value, config.getConverter());
        T configEditable = configEditableResource.find(new HashFilter(T.FIELD_NAME, config.getName()), transaction);
        if (configEditable == null) {
            configEditable = configEditableResource.create(transaction);
            configEditable.setName(config.getName());
        } else if (Arrays.equals(configEditable.getValue(), binaryValue)) {
            return;
        }
        configEditable.setValue(binaryValue);
        callNotificationFunction(rcConfigSetterNotification ->
                rcConfigSetterNotification.onBeforeChangeConfig(config.getName(), value, context));
        configEditableResource.save(configEditable, transaction);
        callNotificationFunction(rcConfigSetterNotification ->
                rcConfigSetterNotification.onAfterChangeConfig(config.getName(), context));
    }

    public void resetToDefault(Config<?> config, ContextTransaction<?> context) throws PlatformException {
        HashFilter filter = new HashFilter(T.FIELD_NAME, config.getName());
        configEditableResource.removeAll(filter, context.getTransaction());
    }

    private void callNotificationFunction(Consumer<RCConfigSetterNotification> consumer) throws PlatformException {
        if (rcConfigSetterNotifications != null) {
            for (RCConfigSetterNotification rcConfigSetterNotification : rcConfigSetterNotifications) {
                consumer.accept(rcConfigSetterNotification);
            }
        }
    }
}
