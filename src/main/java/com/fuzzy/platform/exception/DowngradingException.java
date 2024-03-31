package com.fuzzy.platform.exception;

import com.fuzzy.platform.sdk.component.version.Version;

public class DowngradingException extends RuntimeException {

    public DowngradingException(Class target, Version newVersion, Version prevVersion) {
        super(target.getName() + " does not support downgrading from ver." + prevVersion + " to ver." + newVersion);
    }

    public DowngradingException(String subsystemUuid, Version newVersion, Version prevVersion) {
        super(subsystemUuid + " does not support downgrading from ver." + prevVersion + " to ver." + newVersion);
    }
}
