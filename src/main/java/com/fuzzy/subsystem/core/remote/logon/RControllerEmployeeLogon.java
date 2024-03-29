package com.fuzzy.subsystem.core.remote.logon;

import com.infomaximum.platform.component.frontend.context.ContextTransactionRequest;
import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.QueryRemoteController;
import com.fuzzy.subsystem.core.config.LogonType;

public interface RControllerEmployeeLogon extends QueryRemoteController {

    EmployeeLogin logon(String login, String passwordHash, ContextTransactionRequest context)
            throws PlatformException;

    LogonType getLogonType() throws PlatformException;
}