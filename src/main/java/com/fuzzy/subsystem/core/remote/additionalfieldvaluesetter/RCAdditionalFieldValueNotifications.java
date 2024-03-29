package com.fuzzy.subsystem.core.remote.additionalfieldvaluesetter;

import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.QueryRemoteController;
import com.infomaximum.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystem.core.domainobject.additionalfieldvalue.AdditionalFieldValueReadable;

public interface RCAdditionalFieldValueNotifications extends QueryRemoteController {

    default void onBeforeSetValue(AdditionalFieldValueReadable readable, ContextTransaction context) throws PlatformException {}

    default void onAfterSetValue(AdditionalFieldValueReadable readable, ContextTransaction context) throws PlatformException {}

    default void onBeforeSetArrayItem(AdditionalFieldValueReadable readable, ContextTransaction context) throws PlatformException {}

    default void onAfterSetArrayItem(AdditionalFieldValueReadable readable, ContextTransaction context) throws PlatformException {}

    default void onBeforeAddArrayItem(AdditionalFieldValueReadable readable, ContextTransaction context) throws PlatformException {}

    default void onAfterAddArrayItem(AdditionalFieldValueReadable readable, ContextTransaction context) throws PlatformException {}

    default void onBeforeRemoveArrayValue(AdditionalFieldValueReadable readable, ContextTransaction context) throws PlatformException {}

    default void onBeforeRemoveValue(long additionalFieldId, long objectId, ContextTransaction context) throws PlatformException {}
}
