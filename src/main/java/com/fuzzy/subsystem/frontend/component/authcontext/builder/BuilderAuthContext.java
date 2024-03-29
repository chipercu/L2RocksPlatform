package com.fuzzy.subsystem.frontend.component.authcontext.builder;

import com.infomaximum.cluster.anotation.DisableValidationRemoteMethod;
import com.infomaximum.cluster.graphql.struct.GRequest;
import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.ResourceProvider;
import com.infomaximum.platform.sdk.context.ContextTransaction;
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
