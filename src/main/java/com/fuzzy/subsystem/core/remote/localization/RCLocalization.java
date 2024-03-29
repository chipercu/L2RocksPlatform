package com.fuzzy.subsystem.core.remote.localization;

import com.fuzzy.main.cluster.core.remote.struct.RController;
import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.subsystem.core.config.Language;

public interface RCLocalization extends RController {

    String getLocalization(String locKey, Language language) throws PlatformException;
}
