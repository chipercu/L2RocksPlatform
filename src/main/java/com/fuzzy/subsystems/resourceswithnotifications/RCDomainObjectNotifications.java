package com.fuzzy.subsystems.resourceswithnotifications;

import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.QueryRemoteController;
import com.fuzzy.main.platform.sdk.context.ContextTransaction;

public interface RCDomainObjectNotifications extends QueryRemoteController {

    default void onAfterCreation(long id, ContextTransaction context) throws PlatformException { }

    default void onBeforeUpdate(long id, ContextTransaction context) throws PlatformException { }

    default void onAfterUpdate(long id, ContextTransaction context) throws PlatformException { }

    default void onBeforeRemoval(long id, ContextTransaction context) throws PlatformException { }

    default void onAfterRemoval(ContextTransaction context) throws PlatformException { }
}
