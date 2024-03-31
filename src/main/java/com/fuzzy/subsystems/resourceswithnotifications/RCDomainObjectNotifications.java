package com.fuzzy.subsystems.resourceswithnotifications;

import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.QueryRemoteController;
import com.fuzzy.platform.sdk.context.ContextTransaction;

public interface RCDomainObjectNotifications extends QueryRemoteController {

    default void onAfterCreation(long id, ContextTransaction context) throws PlatformException { }

    default void onBeforeUpdate(long id, ContextTransaction context) throws PlatformException { }

    default void onAfterUpdate(long id, ContextTransaction context) throws PlatformException { }

    default void onBeforeRemoval(long id, ContextTransaction context) throws PlatformException { }

    default void onAfterRemoval(ContextTransaction context) throws PlatformException { }
}
