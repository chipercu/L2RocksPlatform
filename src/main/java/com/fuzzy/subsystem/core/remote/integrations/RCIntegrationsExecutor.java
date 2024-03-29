package com.fuzzy.subsystem.core.remote.integrations;

import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.ResourceProvider;
import com.infomaximum.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystems.function.Function;

import java.util.ArrayList;
import java.util.Set;

public class RCIntegrationsExecutor {

    private final Set<RCIntegrations> rcIntegrationsSet;

    public RCIntegrationsExecutor(ResourceProvider resources) {
        rcIntegrationsSet = resources.getQueryRemoteControllers(RCIntegrations.class);
    }

    public ArrayList<String> getIntegrations(String objectType,
                                             String fieldKey,
                                             ContextTransaction<?> context) throws PlatformException {
        ArrayList<String> integrations = new ArrayList<>();
        for (RCIntegrations rcIntegrations : rcIntegrationsSet) {
            ArrayList<String> integrationsPart = rcIntegrations.getIntegrations(objectType, fieldKey, context);
            if (integrationsPart != null) {
                integrations.addAll(integrationsPart);
            }
        }
        return integrations;
    }

    public boolean isSynchronized(String objectType,
                                  String fieldKey,
                                  ContextTransaction<?> context) throws PlatformException {
        return isSynchronized(rc -> rc.getIntegrations(objectType, fieldKey, context));
    }

    public boolean isSynchronized(long objectId,
                                  String objectType,
                                  String fieldKey,
                                  ContextTransaction<?> context) throws PlatformException {
        return isSynchronized(rc -> rc.getIntegrations(objectId, objectType, fieldKey, context));
    }

    private boolean isSynchronized(Function<RCIntegrations, ArrayList<String>> getter) throws PlatformException {
        for (RCIntegrations rcIntegrations : rcIntegrationsSet) {
            ArrayList<String> integrationsPart = getter.apply(rcIntegrations);
            if (integrationsPart != null && !integrationsPart.isEmpty()) {
                return true;
            }
        }
        return false;
    }
}
