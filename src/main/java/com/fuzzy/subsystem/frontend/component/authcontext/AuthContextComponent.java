package com.fuzzy.subsystem.frontend.component.authcontext;

import com.infomaximum.cluster.graphql.struct.GRequest;
import com.infomaximum.platform.component.frontend.authcontext.UnauthorizedContext;
import com.infomaximum.platform.component.frontend.context.ContextTransactionRequest;
import com.infomaximum.platform.component.frontend.utils.GRequestUtils;
import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.ResourceProvider;
import com.fuzzy.subsystem.frontend.FrontendSubsystem;
import com.fuzzy.subsystem.frontend.component.authcontext.builder.BuilderAuthContext;
import com.fuzzy.subsystem.frontend.component.authcontext.builder.apikey.BuilderApiKeyCertificateAuthContext;
import com.fuzzy.subsystem.frontend.component.authcontext.builder.apikey.BuilderApiKeyDefaultAuthContext;
import com.fuzzy.subsystem.frontend.component.authcontext.builder.employee.BuilderEmployeeSessionAuthContext;
import com.fuzzy.subsystems.exception.GeneralExceptionBuilder;
import com.fuzzy.subsystems.remote.authcontext.RControllerExtensionAuthContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class AuthContextComponent {

    private final static Logger log = LoggerFactory.getLogger(AuthContextComponent.class);

    private final GRequest gRequest;
    private BuilderAuthContext builderAuthContext;

    public AuthContextComponent(FrontendSubsystem frontEndSubSystem, GRequest gRequest, ResourceProvider resources) throws PlatformException {
        boolean serviceMode = frontEndSubSystem.getConfig().isServiceMode();

        List<BuilderAuthContext> builderAuthContexts = new ArrayList<>();
        builderAuthContexts.add(new BuilderEmployeeSessionAuthContext(frontEndSubSystem));
        builderAuthContexts.add(new BuilderApiKeyDefaultAuthContext(serviceMode));
        builderAuthContexts.add(new BuilderApiKeyCertificateAuthContext(serviceMode));
        builderAuthContexts.addAll(resources.getQueryRemoteControllers(RControllerExtensionAuthContext.class));

        this.gRequest = gRequest;

        //Ищем кто хочет сработать
        for (BuilderAuthContext iBuilder : builderAuthContexts) {
            if (iBuilder.prepare(resources, gRequest)) {
                if (builderAuthContext == null) {
                    builderAuthContext = iBuilder;
                } else {
                    String message = String.format("Conflict: [%s, %s]",
                            builderAuthContext.getBuilderName(),
                            iBuilder.getBuilderName()
                    );
                    throw GeneralExceptionBuilder.buildAuthAmbiguityException(message);
                }
            }
        }
    }

    public BuilderAuthContext getBuilderAuthContext() {
        return builderAuthContext;
    }

    public UnauthorizedContext getAuthContext(ContextTransactionRequest context) throws PlatformException {
        UnauthorizedContext authorizedContext;

        if (builderAuthContext != null) {
            authorizedContext = builderAuthContext.auth(gRequest, context);
            if (authorizedContext == null) {
                PlatformException exception = GeneralExceptionBuilder.buildInvalidCredentialsException();
                log.debug("Request {}, context: null, builder: {}, return code: {}",
                        GRequestUtils.getTraceRequest(gRequest),
                        builderAuthContext,
                        exception.getCode()
                );
                throw exception;
            }
        } else {
            authorizedContext = new UnauthorizedContext();
        }

        return authorizedContext;
    }
}
