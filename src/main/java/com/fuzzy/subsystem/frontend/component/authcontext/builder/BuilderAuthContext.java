package com.fuzzy.subsystem.frontend.component.authcontext.builder;

import com.fuzzy.cluster.anotation.DisableValidationRemoteMethod;
import com.fuzzy.cluster.graphql.struct.GRequest;
import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.ResourceProvider;
import com.fuzzy.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystem.frontend.authcontext.AuthorizedContext;

public interface BuilderAuthContext {

    /**
     * Если реализация хочет обработать это способ авторизации она должна вернуть true
     * Причем, в целях оптимизации, т.к. не хочется блокить ресурсы, даже если мы не планируем
     * авторизовывать запрос, то лок ресурсов перенесли на фазу prepare
     * @param gRequest
     * @return
     */
    @DisableValidationRemoteMethod
    boolean prepare(ResourceProvider resources, GRequest gRequest);

    AuthorizedContext auth(GRequest gRequest, ContextTransaction context)
            throws PlatformException;

    String getBuilderName() throws PlatformException;

}
