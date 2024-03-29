package com.fuzzy.subsystem.frontend.component.authcontext.builder;

import com.fuzzy.main.cluster.anotation.DisableValidationRemoteMethod;
import com.fuzzy.main.cluster.graphql.struct.GRequest;
import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.ResourceProvider;
import com.fuzzy.main.platform.sdk.context.ContextTransaction;
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
