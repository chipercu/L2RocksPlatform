package com.fuzzy.subsystem.core.remote.mail;

import com.infomaximum.cluster.core.remote.struct.RController;
import com.infomaximum.platform.exception.PlatformException;
import com.fuzzy.subsystem.core.graphql.query.config.HelpDeskConfig;

public interface RControllerHelpDeskGetter extends RController {
    HelpDeskConfig getConfig() throws PlatformException;
}