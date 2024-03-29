package com.fuzzy.subsystem.core.remote.mail;

import com.fuzzy.main.cluster.core.remote.struct.RController;
import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.subsystem.core.graphql.query.config.HelpDeskConfig;

public interface RControllerHelpDeskGetter extends RController {
    HelpDeskConfig getConfig() throws PlatformException;
}