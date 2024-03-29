package com.fuzzy.subsystem.core.remote.additionalfield;

import com.infomaximum.platform.component.frontend.context.ContextTransactionRequest;
import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.QueryRemoteController;
import com.infomaximum.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystem.core.domainobject.additionalfield.AdditionalFieldReadable;

import java.util.ArrayList;

public interface RCAdditionalField extends QueryRemoteController {

    AdditionalFieldReadable create(AdditionalFieldCreatingBuilder builder, ContextTransaction context) throws PlatformException;

    AdditionalFieldReadable update(long additionalFieldId, AdditionalFieldUpdatingBuilder builder, ContextTransaction context)
            throws PlatformException;

    void order(ArrayList<Long> order, ContextTransactionRequest context) throws PlatformException;

    boolean remove(long additionalFieldId, ContextTransaction context) throws PlatformException;
}
