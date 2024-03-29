package com.fuzzy.subsystem.core.remote.mail;

import com.infomaximum.cluster.core.remote.AbstractRController;
import com.infomaximum.platform.exception.PlatformException;
import com.fuzzy.subsystem.core.CoreSubsystem;
import com.fuzzy.subsystem.core.graphql.query.config.HelpDeskConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class RControllerHelpDeskGetterWrapperImpl extends AbstractRController<CoreSubsystem> implements RControllerHelpDeskGetterWrapper {

    private static final Logger log = LoggerFactory.getLogger(RControllerHelpDeskGetterWrapperImpl.class);
    private CoreSubsystem coreSubsystem;


    public RControllerHelpDeskGetterWrapperImpl(CoreSubsystem coreSubsystem) {
        super(coreSubsystem);
        this.coreSubsystem = coreSubsystem;
    }

    @Override
    public HelpDeskConfig getConfig() throws PlatformException {
        final RControllerHelpDeskGetter rControllerHelpDeskGetter = getControllerHelpDeskGetter(coreSubsystem);
        if (Objects.nonNull(rControllerHelpDeskGetter)) {
            return rControllerHelpDeskGetter.getConfig();
        }
        return null;
    }

    private RControllerHelpDeskGetter getControllerHelpDeskGetter(CoreSubsystem coreSubsystem) {
        final RControllerHelpDeskGetter rControllerHelpDeskGetter;
        rControllerHelpDeskGetter = coreSubsystem.getRemotes().getControllers(RControllerHelpDeskGetter.class)
                .stream()
                .findFirst()
                .orElse(null);
        if (Objects.isNull(rControllerHelpDeskGetter)) {
            log.error("RControllerHelpDeskGetter implementation controller not found.");
        }
        return rControllerHelpDeskGetter;
    }

}