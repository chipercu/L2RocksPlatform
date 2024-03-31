package com.fuzzy.subsystem.core.remote.mail;

import com.fuzzy.cluster.core.remote.struct.RController;
import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.subsystem.core.graphql.query.config.HelpDeskConfig;

public interface RControllerHelpDeskGetter extends RController {
    HelpDeskConfig getConfig() throws PlatformException;
}